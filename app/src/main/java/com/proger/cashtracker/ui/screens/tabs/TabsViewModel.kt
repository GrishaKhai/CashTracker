package com.proger.cashtracker.ui.screens.tabs

import androidx.core.util.Pair
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proger.cashtracker.models.Account
import java.util.Date

class TabsViewModel : ViewModel() {
    //todo delete here comment
    /*private val accountAndDateInterval: MutableLiveData<Pair<Account, Pair<Date, Date>>> = MutableLiveData()
    fun getAccountAndDateIntervalLiveData() = accountAndDateInterval

    fun getAccount(): Account = accountAndDateInterval.value!!.first
    fun setAccount(account: Account) {
        this.accountAndDateInterval.value = Pair(account, accountAndDateInterval.value!!.second)
    }

    fun getDateInterval(): Pair<Date, Date> = accountAndDateInterval.value!!.second
    fun setDateInterval(start: Date, finish: Date) {
        this.accountAndDateInterval.value = Pair(accountAndDateInterval.value!!.first, Pair(start, finish))
    }*/


    private val account: MutableLiveData<Account> = MutableLiveData()
    fun getAccountLiveData() = account
    fun getAccount() = account.value
    fun setAccount(account: Account) { this.account.value = account }

    private val dateInterval: MutableLiveData<Pair<Date, Date>> = MutableLiveData()
    fun getDateIntervalLiveData() = dateInterval
    fun getDateInterval() = dateInterval.value
    fun setDateInterval(start: Date, finish: Date) { this.dateInterval.value = Pair(start, finish) }
}