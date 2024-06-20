package com.proger.cashtracker.ui.screens.tabs.debt.allContributions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.DebtRepository
import com.proger.cashtracker.models.Contribution
import com.proger.cashtracker.models.Debt
import com.proger.cashtracker.models.DebtRole
import com.proger.cashtracker.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewPayForDebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
): ViewModel() {
    private val _pays = MutableLiveData<DataStatus<List<Contribution>>>()
    val pays : LiveData<DataStatus<List<Contribution>>>
        get() = _pays

    private val _debt: MutableLiveData<Debt> = MutableLiveData()
    val debt: LiveData<Debt> = _debt
    fun foundDebt(idDebt: Int) = viewModelScope.launch {
        _debt.postValue(debtRepository.getDebt(idDebt))
    }

    fun getContributions(debtId: Int) = viewModelScope.launch {
        _pays.postValue(DataStatus.loading())
        debtRepository.getContributions(debtId)
            .catch { _pays.postValue(DataStatus.error(it.message.toString())) }
            .collect { _pays.postValue(DataStatus.success(it, it.isEmpty()))}
    }

    fun deleteExpense(pay: Contribution, roleOwner: DebtRole) = viewModelScope.launch {
        debtRepository.deleteContribution(pay, roleOwner)
    }
    fun recoveryExpense(pay: Contribution, roleOwner: DebtRole, debtId: Int) = viewModelScope.launch {
        debtRepository.insertContribution(pay, debtId, roleOwner)
    }
}