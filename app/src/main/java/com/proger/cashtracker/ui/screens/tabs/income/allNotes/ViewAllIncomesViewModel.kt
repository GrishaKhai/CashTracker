package com.proger.cashtracker.ui.screens.tabs.income.allNotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.proger.cashtracker.db.IncomeRepository
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.utils.DataStatus
import com.proger.cashtracker.utils.MutableUnitLiveEvent
import com.proger.cashtracker.utils.publishEvent
import com.proger.cashtracker.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ViewAllIncomesViewModel
@Inject constructor(
    private val incomeRepository: IncomeRepository
) : ViewModel() {
    private val _duplicateEvent = MutableLiveData<Transaction>()
    var duplicateEvent = _duplicateEvent.share()
    fun clearDuplicate() { duplicateEvent = MutableLiveData() }

    private val _incomes = MutableLiveData<DataStatus<List<Transaction>>>()
    val incomes : LiveData<DataStatus<List<Transaction>>>
        get() = _incomes

    private val sortMode = MutableLiveData<Int>()
    fun setSortMode(mode: Int) { sortMode.value = mode }
    fun getSortMode() = sortMode.value

    fun getFullIncomeDetailsValueAsc(start: Long, finish: Long, accountName: String, categoryName: String) = viewModelScope.launch {
        _incomes.postValue(DataStatus.loading())
        incomeRepository.getFullIncomeDetailsValueAsc(start, finish, accountName, categoryName)
            .catch { _incomes.postValue(DataStatus.error(it.message.toString())) }
            .collect { _incomes.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullIncomeDetailsValueDesc(start: Long, finish: Long, accountName: String, categoryName: String) = viewModelScope.launch {
        _incomes.postValue(DataStatus.loading())
        incomeRepository.getFullIncomeDetailsValueDesc(start, finish, accountName, categoryName)
            .catch { _incomes.postValue(DataStatus.error(it.message.toString())) }
            .collect { _incomes.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullIncomeDetailsDateAsc(start: Long, finish: Long, accountName: String, categoryName: String) = viewModelScope.launch {
        _incomes.postValue(DataStatus.loading())
        incomeRepository.getFullIncomeDetailsDateAsc(start, finish, accountName, categoryName)
            .catch { _incomes.postValue(DataStatus.error(it.message.toString())) }
            .collect { _incomes.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullIncomeDetailsDateDesc(start: Long, finish: Long, accountName: String, categoryName: String) = viewModelScope.launch {
        _incomes.postValue(DataStatus.loading())
        incomeRepository.getFullIncomeDetailsDateDesc(start, finish, accountName, categoryName)
            .catch { _incomes.postValue(DataStatus.error(it.message.toString())) }
            .collect { _incomes.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullIncomeDetailsByComment(start: Long, finish: Long, accountName: String, categoryName: String, comment: String) = viewModelScope.launch {
        incomeRepository.getFullIncomeDetailsByComment(start, finish, accountName, categoryName, comment).collect {
            _incomes.postValue(DataStatus.success(it, it.isEmpty()))
        }
    }

    fun deleteIncome(transaction: Transaction) = viewModelScope.launch {
        incomeRepository.deleteIncome(transaction)
    }
    fun recoveryIncome(transaction: Transaction) = viewModelScope.launch {
        incomeRepository.insertIncome(transaction)
    }
    fun duplicateIncome(transaction: Transaction) = viewModelScope.launch {
        transaction.id = incomeRepository.insertIncome(transaction)
        _duplicateEvent.value = transaction
    }
}