package com.proger.cashtracker.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.CurrencyRepository
import com.proger.cashtracker.utils.DateHelper
import com.proger.cashtracker.utils.LocalData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
): ViewModel() {
//    private val option: MutableLiveData<MainActivity.Option> = MutableLiveData(MainActivity.Option.Income)
//    fun getOption() = option.value
//    fun setOption(option: MainActivity.Option){
//        this.option.value = option
//    }

    fun updateCurrency(localDate: LocalData) =
        viewModelScope.launch {
            if(!DateHelper.isToday(Date(localDate.getLastDateSyncWithApi()))){
                currencyRepository.updateCurrencies(localDate.getBaseCurrency()!!)
                localDate.updateDateSyncWithApi(Date())
            }
        }
}