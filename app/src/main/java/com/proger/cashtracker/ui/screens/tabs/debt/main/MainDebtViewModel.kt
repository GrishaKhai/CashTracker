package com.proger.cashtracker.ui.screens.tabs.debt.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.DebtRepository
import com.proger.cashtracker.db.query.ShortDebtDetails
import com.proger.cashtracker.models.Debt
import com.proger.cashtracker.models.DebtRole
import com.proger.cashtracker.models.Debtor
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainDebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository
) : ViewModel() {

    private var _shortByAllDebts: MutableLiveData<DataStatus<List<ShortDebtDetails>>> =
        MutableLiveData()
    val shortByAllDebts: LiveData<DataStatus<List<ShortDebtDetails>>> = _shortByAllDebts

    private var _activeDebts: MutableLiveData<List<Debt>> = MutableLiveData()
    val activeDebts: LiveData<List<Debt>> = _activeDebts


    fun updateShortDebtsGrope(startDate: Long, finishDate: Long, accountName: String = "%") =
        viewModelScope.launch {
            _shortByAllDebts.postValue(DataStatus.loading())
            debtRepository.shortDetailsForDebtsGroup(startDate, finishDate, accountName)
                .catch { _shortByAllDebts.postValue(DataStatus.error(it.message.toString())) }
                .collect { _shortByAllDebts.postValue(DataStatus.success(it, it.isEmpty()))}
        }

    fun updateActiveDebts(accountName: String = "%") =
        viewModelScope.launch {
            debtRepository.getDebts(false, accountName).collect {
                _activeDebts.postValue(it)
            }
        }

    fun deleteDebt(debt: Debt, debtRole: DebtRole) = viewModelScope.launch {
        debtRepository.deleteDebt(debt, debtRole)
    }

    fun recoveryDebt(debt: Debt, debtRole: DebtRole) = viewModelScope.launch {
        debtRepository.insertDebt(debt, debtRole)
    }
}