package com.proger.cashtracker.ui.screens.tabs.debt.addEdit

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.R
import com.proger.cashtracker.db.AccountRepository
import com.proger.cashtracker.db.DebtRepository
import com.proger.cashtracker.db.IncomeRepository
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.Debt
import com.proger.cashtracker.models.DebtRole
import com.proger.cashtracker.models.Debtor
import com.proger.cashtracker.models.DebtorAndCreditorEqualsException
import com.proger.cashtracker.models.EditedNoteNotExistInDbException
import com.proger.cashtracker.models.EmptyFieldException
import com.proger.cashtracker.models.Field
import com.proger.cashtracker.models.IncorrectDateFormatException
import com.proger.cashtracker.models.IncorrectValueFormatException
import com.proger.cashtracker.models.NoteNotExistInDbException
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.utils.MutableLiveEvent
import com.proger.cashtracker.utils.MutableUnitLiveEvent
import com.proger.cashtracker.utils.NO_ERROR_MESSAGE
import com.proger.cashtracker.utils.publishEvent
import com.proger.cashtracker.utils.requireValue
import com.proger.cashtracker.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditDebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
    accountRepository: AccountRepository,
)  : ViewModel()  {

    //сообщение об ошибках или успехе
    private val _showToastEvent = MutableLiveEvent<Int>()
    val showToastEvent = _showToastEvent.share()

    //событие возврата к главному меню
    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    //состояние заполнения данных
    private val _state = MutableLiveData(State())
    val state = _state.share()

    val debtors: LiveData<List<Debtor>> = debtRepository.getDebtors().asLiveData()
//    val debtorI: MutableLiveData<Debtor> = MutableLiveData()

//    init{
//        getDebtorI()
//    }
//
//    fun getDebtorI() {
//        viewModelScope.launch{
//            debtorI.postValue(debtRepository.getDebtorFirst())
//        }
//    }
    //все счета
    val accounts: LiveData<List<Account>> = accountRepository.accountsWithCurrency.asLiveData()

    //аккаунт
    private var selectedAccount: MutableLiveData<Account> = MutableLiveData()
    fun setSelectedAccount(account: Account){ selectedAccount.value = account }
    fun getSelectedAccount() = selectedAccount.value
    //интервал дат
    private var selectedIntervalDate: MutableLiveData<Pair<Long, Long>> = MutableLiveData()
    fun setSelectedIntervalDate(start: Long, finish: Long) { selectedIntervalDate.value = Pair(start, finish) }
    fun setSelectedIntervalDate(interval: Pair<Long, Long>) { selectedIntervalDate.value = interval }
    fun getSelectedIntervalDate() = selectedIntervalDate.value
    //число
    private val enteredValue: MutableLiveData<Double> = MutableLiveData()
    fun setEnteredValue(value: Double){ enteredValue.value = value }
    fun getEnteredValue() = enteredValue.value

    fun editDebt(oldId: Int, debt: Debt, debtRole: DebtRole){
        viewModelScope.launch {
            showProgress()
            try {
                debtRepository.updateDebt(oldId, debt, debtRole)
                showSuccessMessage()
                goBack()
            } catch (e: EmptyFieldException) {
                processEmptyFieldException(e)
            } catch (e: IncorrectValueFormatException){
                processValueIsNegativeException()
            } catch (e: IncorrectDateFormatException){
                showErrorMessage(R.string.incorrect_form_interval)
            } /*catch (e: DebtorAndCreditorEqualsException){
                processDebtorAndCreditorEqualsException()
            }*/ catch (e: NoteNotExistInDbException){
                showErrorMessage(R.string.account_not_exist_in_db)
            } catch (e: EditedNoteNotExistInDbException){
                showErrorMessage(R.string.edited_note_losted)
                goBack()
            } finally {
                hideProgress()
                onCleared()
            }
        }
    }
    fun addDebt(debt: Debt, debtRole: DebtRole){
        viewModelScope.launch {
            showProgress()
            try {
                debtRepository.insertDebt(debt, debtRole)
                showSuccessMessage()
                goBack()
            } catch (e: EmptyFieldException) {
                processEmptyFieldException(e)
            } catch (e: IncorrectValueFormatException){
                processValueIsNegativeException()
            } catch (e: IncorrectDateFormatException){
                showErrorMessage(R.string.incorrect_form_interval)
            } /*catch (e: DebtorAndCreditorEqualsException){
                processDebtorAndCreditorEqualsException()
            }*/ catch (e: NoteNotExistInDbException){
                showErrorMessage(R.string.account_not_exist_in_db)
            } finally {
                hideProgress()
                onCleared()
            }
        }
    }

    private fun processEmptyFieldException(e: EmptyFieldException) {
        when (e.field) {
            Field.Date -> showErrorMessage(R.string.date_is_empty)
            Field.Value -> _state.value = _state.requireValue().copy(valueTransactionMessageRes = R.string.field_is_empty)
            Field.Category -> showErrorMessage(R.string.category_is_empty)
            Field.Account -> showErrorMessage(R.string.account_is_empty)
            else -> throw IllegalStateException("Unknown field")
        }
    }

    private fun showSuccessMessage() = _showToastEvent.publishEvent(R.string.success_insert_debt)
    private fun showErrorMessage(idMessage: Int) = _showToastEvent.publishEvent(idMessage)
    private fun processValueIsNegativeException() {
        _state.value = _state.requireValue()
            .copy(valueTransactionMessageRes = R.string.value_is_negative)
    }
    private fun processDebtorAndCreditorEqualsException() {
        _state.value = _state.requireValue()
            .copy(valueTransactionMessageRes = R.string.names_debtor_and_creditor_equals)
    }

    private fun goBack() = _goBackEvent.publishEvent()

    private fun showProgress() {
        _state.value = State(pushDataProgress = true)
    }
    private fun hideProgress() {
        _state.value = _state.requireValue().copy(pushDataProgress = false)
    }

    data class State(
        @StringRes val valueTransactionMessageRes: Int = NO_ERROR_MESSAGE,
        @StringRes val valueDebtorAndCreditorMessageRes: Int = NO_ERROR_MESSAGE,
        val pushDataProgress: Boolean = false,
    ) {
        val showProgress: Boolean get() = pushDataProgress
        val enableViews: Boolean get() = !pushDataProgress
    }
}