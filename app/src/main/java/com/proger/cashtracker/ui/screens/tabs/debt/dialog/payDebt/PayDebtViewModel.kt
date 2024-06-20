package com.proger.cashtracker.ui.screens.tabs.debt.dialog.payDebt

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.R
import com.proger.cashtracker.db.AccountRepository
import com.proger.cashtracker.db.DebtRepository
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Contribution
import com.proger.cashtracker.models.Debt
import com.proger.cashtracker.models.DebtRole
import com.proger.cashtracker.models.EditedNoteNotExistInDbException
import com.proger.cashtracker.models.EmptyFieldException
import com.proger.cashtracker.models.Field
import com.proger.cashtracker.models.IncorrectValueFormatException
import com.proger.cashtracker.utils.MutableLiveEvent
import com.proger.cashtracker.utils.MutableUnitLiveEvent
import com.proger.cashtracker.utils.NO_ERROR_MESSAGE
import com.proger.cashtracker.utils.publishEvent
import com.proger.cashtracker.utils.requireValue
import com.proger.cashtracker.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PayDebtViewModel @Inject constructor(
    accountRepository: AccountRepository,
    private val debtRepository: DebtRepository,
) : ViewModel() {

    //сообщение об ошибках
    private val _showToastEvent = MutableLiveEvent<Int>()
    val showToastEvent = _showToastEvent.share()
    //состояние заполнения данных
    private val _state = MutableLiveData(State())
    val state = _state.share()
    //событие возврата к окну добавления/редактирования дохода или расхода
    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    private val _debt: MutableLiveData<Debt> = MutableLiveData()
    val debt: LiveData<Debt> = _debt
    fun foundDebt(idDebt: Int) = viewModelScope.launch {
        _debt.postValue(debtRepository.getDebt(idDebt))
    }

    //все счета
    val accounts: LiveData<List<Account>> = accountRepository.accountsWithCurrency.asLiveData()
    //выбранный счет
    private val selectedAccount: MutableLiveData<Account> = MutableLiveData()
    fun setSelectedAccount(account: Account) { selectedAccount.value = account }
    fun getSelectedAccount() = selectedAccount.value
    //выбранная дата
    private val selectedDate: MutableLiveData<Date> = MutableLiveData()
    fun setSelectedDate(date: Long) { selectedDate.value = Date(date) }
    fun getSelectedDate() = selectedDate.value

    fun addPay(contribution: Contribution, debtId: Int, debtRole: DebtRole){
        viewModelScope.launch{
            try{
                debtRepository.insertContribution(contribution, debtId, debtRole)
                showSuccessMessage()
                goBack()
            } catch (e: EmptyFieldException){
                processEmptyFieldException(e)
            } catch (e: IncorrectValueFormatException){
                processValueIsNegativeException()
            }
        }
    }
    fun editPay(contribution: Contribution, debtId: Int, contributionId: Int, debtRole: DebtRole){
        viewModelScope.launch{
            try{
                debtRepository.updateContribution(contribution, debtId, contributionId, debtRole)
                showSuccessMessage()
                goBack()
            } catch (e: EmptyFieldException){
                processEmptyFieldException(e)
            } catch(e: IncorrectValueFormatException) {
                processValueIsNegativeException()
            } catch (e: EditedNoteNotExistInDbException){
                goBack()
            }
        }
    }

    private fun processEmptyFieldException(e: EmptyFieldException) {
        when (e.field) {
            Field.Account -> _state.value = _state.requireValue().copy(payDebtValueMessageRes = R.string.account_is_empty)
            Field.Value -> showErrorMessage(R.string.field_is_empty)
            else -> throw IllegalStateException("Unknown field")
        }
    }
    private fun processValueIsNegativeException() {
        _state.value = _state.requireValue()
            .copy(payDebtValueMessageRes = R.string.value_is_negative)
    }

    private fun showSuccessMessage() = _showToastEvent.publishEvent(R.string.success_insert_pay_to_debt)
    private fun showErrorMessage(idMessage: Int) = _showToastEvent.publishEvent(idMessage)
    private fun goBack() = _goBackEvent.publishEvent()

    data class State(
        @StringRes val payDebtValueMessageRes: Int = NO_ERROR_MESSAGE
    )
}