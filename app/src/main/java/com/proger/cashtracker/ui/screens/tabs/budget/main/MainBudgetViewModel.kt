package com.proger.cashtracker.ui.screens.tabs.budget.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.BudgetRepository
import com.proger.cashtracker.models.Budget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainBudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository
) : ViewModel() {
    private var _budgets: MutableLiveData<List<Budget>> = MutableLiveData()
    val budgets: LiveData<List<Budget>> = _budgets

    fun updateBudgets(startDate: Long, finishDate: Long) =
        viewModelScope.launch {
            budgetRepository.getTotalBudgetsByCategories(startDate, finishDate).collect{ _budgets.postValue(it) }
        }
}