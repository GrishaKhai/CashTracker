package com.proger.cashtracker.ui.screens.currency

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.CurrencyRepository
import com.proger.cashtracker.utils.LocalData
import com.proger.cashtracker.models.Currency
import com.proger.cashtracker.utils.DataStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ViewAllCurrenciesViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
): ViewModel() {
    val currencies: MutableLiveData<DataStatus<List<Currency>>> = MutableLiveData()

    fun updateCurrency() = viewModelScope.launch {
        currencies.postValue(DataStatus.loading())
        currencyRepository.currencies
            .catch { currencies.postValue(DataStatus.error(it.message.toString())) }
            .collect{ currencies.postValue(DataStatus.success(it, it.isEmpty())) }
    }

    private val selectedCurrency: MutableLiveData<Currency> = MutableLiveData()
    fun setSelectedCurrency(selectedCurrency: Currency) { this.selectedCurrency.value = selectedCurrency}
    fun getSelectedCurrency() = selectedCurrency.value

    fun updateCurrencyNow(localDate: LocalData) =
        viewModelScope.launch {
            currencyRepository.updateCurrencies(localDate.getBaseCurrency()!!)
            localDate.updateDateSyncWithApi(Date())
        }
    fun updateCurrency(rateToBase: Double) = viewModelScope.launch {
        currencyRepository.doConvert(rateToBase)
    }
}