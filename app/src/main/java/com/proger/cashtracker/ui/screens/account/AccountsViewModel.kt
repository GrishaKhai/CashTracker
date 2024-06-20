package com.proger.cashtracker.ui.screens.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.AccountRepository
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _accounts = MutableLiveData<DataStatus<List<Account>>>()
    val accounts: LiveData<DataStatus<List<Account>>>
        get() = _accounts //accountRepository.accountsWithCurrency.asLiveData()



    fun getAccounts() = viewModelScope.launch {
        _accounts.postValue(DataStatus.loading())
        accountRepository.accountsWithCurrency
            .catch { _accounts.postValue(DataStatus.error(it.message.toString())) }
            .collect { _accounts.postValue(DataStatus.success(it, it.isEmpty()))}
    }

    fun changeStateAccount(accountName: String, isActive: Boolean) = viewModelScope.launch {
        accountRepository.setActiveStateAccount(accountName, isActive)
    }
}