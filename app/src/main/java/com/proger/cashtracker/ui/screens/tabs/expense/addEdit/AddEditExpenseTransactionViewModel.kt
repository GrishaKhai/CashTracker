package com.proger.cashtracker.ui.screens.tabs.expense.addEdit

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.R
import com.proger.cashtracker.db.AccountRepository
import com.proger.cashtracker.db.ExpenseRepository
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.EditedNoteNotExistInDbException
import com.proger.cashtracker.models.EmptyFieldException
import com.proger.cashtracker.models.Field
import com.proger.cashtracker.models.IncorrectDateFormatException
import com.proger.cashtracker.models.IncorrectValueFormatException
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
class AddEditExpenseTransactionViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    //сообщение об ошибках или успехе
    private val _showToastEvent = MutableLiveEvent<Int>()
    val showToastEvent = _showToastEvent.share()

    //событие возврата к главному меню
    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    //состояние заполнения данных
    private val _state = MutableLiveData(State())
    val state = _state.share()

    //связь с категориями из БД
    val categories: LiveData<List<Category>> = expenseRepository.getExpenseCategories().asLiveData()
    //все счета
    val accounts: LiveData<List<Account>> = accountRepository.accountsWithCurrency.asLiveData()

    fun editExpense(oldId: Int, expense: Transaction){
        viewModelScope.launch {
            showProgress()
            try {
                expenseRepository.updateExpense(oldId, expense)
                showSuccessMessage()
                goBack()
            } catch (e: EmptyFieldException) {
                processEmptyFieldException(e)
            } catch (e: IncorrectValueFormatException){
                processValueIsNegativeException()
            } catch (e: IncorrectDateFormatException){
                showErrorMessage(R.string.date_in_future)
            } catch (e: EditedNoteNotExistInDbException){
                showErrorMessage(R.string.edited_note_losted)
                goBack()
            } finally {
                hideProgress()
                onCleared()
            }
        }
    }
    fun addExpense(income: Transaction){
        viewModelScope.launch {
            showProgress()
            try {
                expenseRepository.insertExpense(income)
                showSuccessMessage()
                goBack()
            } catch (e: EmptyFieldException) {
                processEmptyFieldException(e)
            } catch (e: IncorrectValueFormatException){
                processValueIsNegativeException()
            } catch (e: IncorrectDateFormatException){
                showErrorMessage(R.string.date_in_future)
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

    private fun showSuccessMessage() = _showToastEvent.publishEvent(R.string.success_insert_expense)
    private fun showErrorMessage(idMessage: Int) = _showToastEvent.publishEvent(idMessage)
    private fun processValueIsNegativeException() {
        _state.value = _state.requireValue()
            .copy(valueTransactionMessageRes = R.string.value_is_negative)
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
        val pushDataProgress: Boolean = false,
    ) {
        val showProgress: Boolean get() = pushDataProgress
        val enableViews: Boolean get() = !pushDataProgress
    }
}