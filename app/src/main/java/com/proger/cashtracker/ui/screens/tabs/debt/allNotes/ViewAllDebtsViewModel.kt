package com.proger.cashtracker.ui.screens.tabs.debt.allNotes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.DebtRepository
import com.proger.cashtracker.models.Debt
import com.proger.cashtracker.models.DebtRole
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewAllDebtsViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
): ViewModel() {

    private val _debts = MutableLiveData<DataStatus<List<Debt>>>()
    val debts : LiveData<DataStatus<List<Debt>>>
        get() = _debts

    private val sortMode = MutableLiveData<Int>()
    fun setSortMode(mode: Int) { sortMode.value = mode }
    fun getSortMode() = sortMode.value



    fun getFullDebtDetailsValueAsc(start: Long, finish: Long, isDebtor: Boolean, nameDebtorOrCreditor: String, accountName: String) = viewModelScope.launch {
        _debts.postValue(DataStatus.loading())
        debtRepository.getFullDebtDetailsValueAsc(start, finish, isDebtor, nameDebtorOrCreditor, accountName)
            .catch { _debts.postValue(DataStatus.error(it.message.toString())) }
            .collect { _debts.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullDebtDetailsValueDesc(start: Long, finish: Long, isDebtor: Boolean, nameDebtorOrCreditor: String, accountName: String) = viewModelScope.launch {
        _debts.postValue(DataStatus.loading())
        debtRepository.getFullDebtDetailsValueDesc(start, finish, isDebtor, nameDebtorOrCreditor, accountName)
            .catch { _debts.postValue(DataStatus.error(it.message.toString())) }
            .collect { _debts.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullDebtDetailsDateAsc(start: Long, finish: Long, isDebtor: Boolean, nameDebtorOrCreditor: String, accountName: String) = viewModelScope.launch {
        _debts.postValue(DataStatus.loading())
        debtRepository.getFullDebtDetailsDateAsc(start, finish, isDebtor, nameDebtorOrCreditor, accountName)
            .catch { _debts.postValue(DataStatus.error(it.message.toString())) }
            .collect { _debts.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullDebtDetailsDateDesc(start: Long, finish: Long, isDebtor: Boolean, nameDebtorOrCreditor: String, accountName: String) = viewModelScope.launch {
        _debts.postValue(DataStatus.loading())
        debtRepository.getFullDebtDetailsDateDesc(start, finish, isDebtor, nameDebtorOrCreditor, accountName)
            .catch { _debts.postValue(DataStatus.error(it.message.toString())) }
            .collect { _debts.postValue(DataStatus.success(it, it.isEmpty()))}
    }
    fun getFullDebtDetailsByComment(start: Long, finish: Long, isDebtor: Boolean, nameDebtorOrCreditor: String, accountName: String, comment: String) = viewModelScope.launch {
        debtRepository.getFullDebtDetailsByComment(start, finish, isDebtor, nameDebtorOrCreditor, accountName, comment).collect {
            _debts.postValue(DataStatus.success(it, it.isEmpty()))
        }
    }

    fun deleteDebt(debt: Debt) = viewModelScope.launch {
        val roleOwner = if(debt.isDebtor) DebtRole.Debtor else DebtRole.Creditor
        debtRepository.deleteDebt(debt, roleOwner)
    }
//    fun recoveryDebt(debt: Debt) = viewModelScope.launch {
//        val roleOwner = if(debt.isDebtor) DebtRole.Debtor else DebtRole.Creditor
//        debtRepository.insertDebt(debt, roleOwner)
//    }
}