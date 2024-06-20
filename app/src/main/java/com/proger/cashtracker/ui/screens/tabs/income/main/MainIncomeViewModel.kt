package com.proger.cashtracker.ui.screens.tabs.income.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.IncomeRepository
import com.proger.cashtracker.models.TransactionByCategories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainIncomeViewModel
@Inject constructor(
    private val incomeRepository: IncomeRepository
) : ViewModel() {

    private var _incomes: MutableLiveData<List<TransactionByCategories>> = MutableLiveData()
    val incomes: LiveData<List<TransactionByCategories>> = _incomes

    fun updateIncomes(startDate: Long, finishDate: Long, accountName: String) =
        viewModelScope.launch {
            incomeRepository.getShortIncomesByCategories(startDate, finishDate, accountName)
                .collect {
                    _incomes.postValue(it)
                }
        }
    fun updateIncomes(startDate: Long, finishDate: Long) =
        viewModelScope.launch {
            incomeRepository.getShortIncomeDetailsFromAllAccounts(startDate, finishDate)
                .collect {
                    _incomes.postValue(it)
                }
        }
}