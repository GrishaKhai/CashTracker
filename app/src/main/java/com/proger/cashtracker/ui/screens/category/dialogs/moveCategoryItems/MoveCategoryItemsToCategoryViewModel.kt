package com.proger.cashtracker.ui.screens.category.dialogs.moveCategoryItems

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.AccountRepository
import com.proger.cashtracker.db.ExpenseRepository
import com.proger.cashtracker.db.IncomeRepository
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoveCategoryItemsToCategoryViewModel @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
    private val accountRepository: AccountRepository,
): ViewModel() {
    private val _incomeCategories: MutableLiveData<DataStatus<List<Category>>> = MutableLiveData()
    val incomeCategories: LiveData<DataStatus<List<Category>>> = _incomeCategories

    private val _expenseCategories: MutableLiveData<DataStatus<List<Category>>> = MutableLiveData()
    val expenseCategories: LiveData<DataStatus<List<Category>>> = _expenseCategories

    fun getIncomeCategories() = viewModelScope.launch {
        _incomeCategories.postValue(DataStatus.loading())
        incomeRepository.getIncomeCategories()
            .catch { _incomeCategories.postValue(DataStatus.error(it.message.toString())) }
            .collect { _incomeCategories.postValue(DataStatus.success(it, it.isEmpty())) }
    }
    fun getExpenseCategories() = viewModelScope.launch {
        _expenseCategories.postValue(DataStatus.loading())
        expenseRepository.getExpenseCategories()
            .catch { _expenseCategories.postValue(DataStatus.error(it.message.toString())) }
            .collect { _expenseCategories.postValue(DataStatus.success(it, it.isEmpty())) }
    }



    fun moveIncomesToNewCategory(oldCategoryName: String, newCategoryName: String){
        viewModelScope.launch {
            incomeRepository.moveIncomesToNewCategory(oldCategoryName, newCategoryName)
            incomeRepository.deleteIncomeCategory(oldCategoryName)
        }
    }
    fun moveIncomesToNewCategory(oldCategoryName: String){
        viewModelScope.launch {
            incomeRepository.updateBalanceAccountsFromIncomeCategory(oldCategoryName)
            incomeRepository.deleteIncomeCategory(oldCategoryName)
        }
    }

    fun moveExpensesToNewCategory(oldCategoryName: String, newCategoryName: String){
        viewModelScope.launch {
            expenseRepository.moveExpensesToNewCategory(oldCategoryName, newCategoryName)
            expenseRepository.deleteExpenseCategory(oldCategoryName)
        }
    }
    fun moveExpensesToNewCategory(oldCategoryName: String){
        viewModelScope.launch {
            expenseRepository.updateBalanceAccountsFromExpenseCategory(oldCategoryName)
            expenseRepository.deleteExpenseCategory(oldCategoryName)
        }
    }
}