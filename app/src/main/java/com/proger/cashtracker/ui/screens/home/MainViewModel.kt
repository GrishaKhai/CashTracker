package com.proger.cashtracker.ui.screens.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.proger.cashtracker.db.AccountRepository
import com.proger.cashtracker.db.IncomeRepository
import com.proger.cashtracker.models.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    accountRepository: AccountRepository
) : ViewModel() {
    //все счета
    val accounts: LiveData<List<Account>> = accountRepository.accountsWithCurrency.asLiveData()
}