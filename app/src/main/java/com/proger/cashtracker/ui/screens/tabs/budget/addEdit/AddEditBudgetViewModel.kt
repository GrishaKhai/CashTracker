package com.proger.cashtracker.ui.screens.tabs.budget.addEdit

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.R
import com.proger.cashtracker.db.BudgetRepository
import com.proger.cashtracker.db.ExpenseRepository
import com.proger.cashtracker.models.Budget
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.ConflictBudgetException
import com.proger.cashtracker.models.EditedNoteNotExistInDbException
import com.proger.cashtracker.models.EmptyFieldException
import com.proger.cashtracker.models.Field
import com.proger.cashtracker.models.IncorrectDateFormatException
import com.proger.cashtracker.models.IncorrectValueFormatException
import com.proger.cashtracker.models.NoteNotExistInDbException
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
class AddEditBudgetViewModel @Inject constructor(
    private val context: Context,
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository,
): ViewModel() {
    //сообщение об ошибках или успехе
    private val _showToastEvent = MutableLiveEvent<String>()
    val showToastEvent = _showToastEvent.share()

    //событие возврата к главному меню
    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    //состояние заполнения данных
    private val _state = MutableLiveData(State())
    val state = _state.share()

    //связь с категориями из БД
    val categories: LiveData<List<Category>> = expenseRepository.getExpenseCategories().asLiveData()

    fun editBudget(oldId: Int, budget: Budget){
        viewModelScope.launch {
            showProgress()
            try {
                budgetRepository.updateBudget(oldId, budget)
                showSuccessMessage()
                goBack()
            } catch (e: EmptyFieldException) {
                processEmptyFieldException(e)
            } catch (e: IncorrectValueFormatException){
                processValueIsNegativeException()
            } catch (e: IncorrectDateFormatException){
                showErrorMessage(context.getString(R.string.incorrect_form_interval))
            } catch (e: ConflictBudgetException){
                showErrorMessage(context.getString(R.string.budget_date_conflict) + " " + e.message.toString())
            } catch (e: NoteNotExistInDbException){
                showErrorMessage(context.getString(R.string.category_lost) + " (${e.message.toString()})")
            } catch (e: EditedNoteNotExistInDbException){
                showErrorMessage(context.getString(R.string.edited_note_losted))
                goBack()
            } finally {
                hideProgress()
                onCleared()
            }
        }
    }
    fun addBudget(budget: Budget){
        viewModelScope.launch {
            showProgress()
            try {
                budgetRepository.insertBudget(budget)
                showSuccessMessage()
                goBack()
            } catch (e: EmptyFieldException) {
                processEmptyFieldException(e)
            } catch (e: IncorrectValueFormatException){
                processValueIsNegativeException()
            } catch (e: IncorrectDateFormatException){
                showErrorMessage(context.getString(R.string.incorrect_form_interval))
            } catch (e: ConflictBudgetException){
                showErrorMessage(context.getString(R.string.budget_date_conflict) + " " + e.message.toString())
            } catch (e: NoteNotExistInDbException){
                showErrorMessage(context.getString(R.string.category_lost) + " (${e.message.toString()})")
            } finally {
                hideProgress()
                onCleared()
            }
        }
    }


    private fun processEmptyFieldException(e: EmptyFieldException) {
        when (e.field) {
            Field.Date -> showErrorMessage(context.getString(R.string.date_is_empty))
            Field.Value -> _state.value = _state.requireValue().copy(valueTransactionMessageRes = R.string.field_is_empty)
            Field.Category -> showErrorMessage(context.getString(R.string.category_is_empty))
            else -> throw IllegalStateException("Unknown field")
        }
    }

    private fun showSuccessMessage() = _showToastEvent.publishEvent(context.getString(R.string.success_insert_budget))
    private fun showErrorMessage(message: String) = _showToastEvent.publishEvent(message)
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