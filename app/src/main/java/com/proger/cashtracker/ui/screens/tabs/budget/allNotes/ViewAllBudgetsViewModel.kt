package com.proger.cashtracker.ui.screens.tabs.budget.allNotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.BudgetRepository
import com.proger.cashtracker.models.Budget
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewAllBudgetsViewModel 
@Inject constructor(
    private val budgetRepository: BudgetRepository
): ViewModel() {
    private var _budgets: MutableLiveData<DataStatus<List<Budget>>> = MutableLiveData()
    val budgets: LiveData<DataStatus<List<Budget>>> get() = _budgets

    private val sortMode = MutableLiveData<Int>()
    fun setSortMode(mode: Int) { sortMode.value = mode }
    fun getSortMode() = sortMode.value

    fun getFullBudgetDetailsValueAsc(start: Long, finish: Long, categoryName: String) = viewModelScope.launch {
        _budgets.postValue(DataStatus.loading())
        budgetRepository.getFullBudgetDetailsValueAsc(start, finish, categoryName)
            .catch { _budgets.postValue(DataStatus.error(it.message.toString())) }
            .collect { _budgets.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullBudgetDetailsValueDesc(start: Long, finish: Long, categoryName: String) = viewModelScope.launch {
        _budgets.postValue(DataStatus.loading())
        budgetRepository.getFullBudgetDetailsValueDesc(start, finish, categoryName)
            .catch { _budgets.postValue(DataStatus.error(it.message.toString())) }
            .collect { _budgets.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullBudgetDetailsDateAsc(start: Long, finish: Long, categoryName: String) = viewModelScope.launch {
        _budgets.postValue(DataStatus.loading())
        budgetRepository.getFullBudgetDetailsDateAsc(start, finish, categoryName)
            .catch { _budgets.postValue(DataStatus.error(it.message.toString())) }
            .collect { _budgets.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullBudgetDetailsDateDesc(start: Long, finish: Long, categoryName: String) = viewModelScope.launch {
        _budgets.postValue(DataStatus.loading())
        budgetRepository.getFullBudgetDetailsDateDesc(start, finish, categoryName)
            .catch { _budgets.postValue(DataStatus.error(it.message.toString())) }
            .collect { _budgets.postValue(DataStatus.success(it, it.isEmpty()))}
    }

    fun deleteBudget(budget: Budget) = viewModelScope.launch {
        budgetRepository.deleteBudget(budget)
    }
    fun recoveryBudget(budget: Budget) = viewModelScope.launch {
        budgetRepository.insertBudget(budget)
    }
}