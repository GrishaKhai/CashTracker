package com.proger.cashtracker.ui.screens.tabs.expense.allNotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.ExpenseRepository
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.utils.DataStatus
import com.proger.cashtracker.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewAllExpensesViewModel
@Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel(){
    private val _duplicateEvent = MutableLiveData<Transaction>()
    var duplicateEvent = _duplicateEvent.share()
    fun clearDuplicate() { duplicateEvent = MutableLiveData() }

    private val _expenses = MutableLiveData<DataStatus<List<Transaction>>>()
    val expenses : LiveData<DataStatus<List<Transaction>>>
        get() = _expenses

    private val sortMode = MutableLiveData<Int>()
    fun setSortMode(mode: Int) { sortMode.value = mode }
    fun getSortMode() = sortMode.value

    fun getFullExpenseDetailsValueAsc(start: Long, finish: Long, accountName: String, categoryName: String) = viewModelScope.launch {
        _expenses.postValue(DataStatus.loading())
        expenseRepository.getFullExpenseDetailsValueAsc(start, finish, accountName, categoryName)
            .catch { _expenses.postValue(DataStatus.error(it.message.toString())) }
            .collect { _expenses.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullExpenseDetailsValueDesc(start: Long, finish: Long, accountName: String, categoryName: String) = viewModelScope.launch {
        _expenses.postValue(DataStatus.loading())
        expenseRepository.getFullExpenseDetailsValueDesc(start, finish, accountName, categoryName)
            .catch { _expenses.postValue(DataStatus.error(it.message.toString())) }
            .collect { _expenses.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullExpenseDetailsDateAsc(start: Long, finish: Long, accountName: String, categoryName: String) = viewModelScope.launch {
        _expenses.postValue(DataStatus.loading())
        expenseRepository.getFullExpenseDetailsDateAsc(start, finish, accountName, categoryName)
            .catch { _expenses.postValue(DataStatus.error(it.message.toString())) }
            .collect { _expenses.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullExpenseDetailsDateDesc(start: Long, finish: Long, accountName: String, categoryName: String) = viewModelScope.launch {
        _expenses.postValue(DataStatus.loading())
        expenseRepository.getFullExpenseDetailsDateDesc(start, finish, accountName, categoryName)
            .catch { _expenses.postValue(DataStatus.error(it.message.toString())) }
            .collect { _expenses.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullExpenseDetailsByComment(start: Long, finish: Long, accountName: String, categoryName: String, comment: String) = viewModelScope.launch {
        expenseRepository.getFullExpenseDetailsByComment(start, finish, accountName, categoryName, comment).collect {
            _expenses.postValue(DataStatus.success(it, it.isEmpty()))
        }
    }

    fun deleteExpense(transaction: Transaction) = viewModelScope.launch {
        expenseRepository.deleteExpense(transaction)
    }
    fun recoveryExpense(transaction: Transaction) = viewModelScope.launch {
        expenseRepository.insertExpense(transaction)
    }
    fun duplicateExpense(transaction: Transaction) = viewModelScope.launch {
        transaction.id = expenseRepository.insertExpense(transaction)
        _duplicateEvent.value = transaction
    }
}