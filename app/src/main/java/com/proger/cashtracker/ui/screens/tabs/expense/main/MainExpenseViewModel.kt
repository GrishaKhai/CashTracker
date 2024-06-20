package com.proger.cashtracker.ui.screens.tabs.expense.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.ExpenseRepository
import com.proger.cashtracker.models.TransactionByCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private var _expenses: MutableLiveData<List<TransactionByCategories>> = MutableLiveData()
    val expenses: LiveData<List<TransactionByCategories>> = _expenses

    fun updateExpenses(startDate: Long, finishDate: Long, accountName: String) =
        viewModelScope.launch {
            expenseRepository.getShortExpensesByCategories(startDate, finishDate, accountName)
                .collect {
                    _expenses.postValue(it)
                }
        }
    fun updateExpenses(startDate: Long, finishDate: Long) =
        viewModelScope.launch {
            expenseRepository.getShortExpenseDetailsFromAllAccounts(startDate, finishDate)
                .collect {
                    _expenses.postValue(it)
                }
        }
}