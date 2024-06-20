package com.proger.cashtracker.ui.screens.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.ExpenseRepository
import com.proger.cashtracker.db.IncomeRepository
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val incomeRepository: IncomeRepository,
    private val expenseRepository: ExpenseRepository,
) : ViewModel() {

//    val incomeCategories: LiveData<List<Category>> =
//        incomeRepository.getIncomeCategories().asLiveData()

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


//    private val _expenses = MutableLiveData<DataStatus<List<Transaction>>>()
//    val expenses : LiveData<DataStatus<List<Transaction>>>
//        get() = _expenses
//
//    private val _income = MutableLiveData<DataStatus<List<Transaction>>>()
//    val income : LiveData<DataStatus<List<Transaction>>>
//        get() = _income


//    fun getAccounts() = viewModelScope.launch {
//        _expenses.postValue(DataStatus.loading())
//        expenseRepository.getExpenseCategories()
//            .catch { _expenses.postValue(DataStatus.error(it.message.toString())) }
//            .collect { _expenses.postValue(DataStatus.success(it, it.isEmpty()))}
//    }
}