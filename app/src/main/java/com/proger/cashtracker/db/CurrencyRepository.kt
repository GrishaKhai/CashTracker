package com.proger.cashtracker.db

import com.proger.cashtracker.db.dao.CurrencyDao
import com.proger.cashtracker.db.table.CurrencyEntity
import com.proger.cashtracker.api.currencyConversion.ApiDataSource
import com.proger.cashtracker.api.currencyConversion.BaseDataSource
import com.proger.cashtracker.api.currencyConversion.CurrencyResponse
import com.proger.cashtracker.models.Currency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepository
@Inject constructor(
    private var currencyDao: CurrencyDao,
    private val apiDataSource: ApiDataSource,
    private val db: AppDatabase
) : BaseDataSource() {

    val currencies: Flow<List<Currency>> = currencyDao.getCurrencies().map { entity -> entity.map {
        Currency(
            currencyName = it.currencyName,
            currencyCode = it.currencyCode,
            rateToBase = it.rateToBase,
        )
    } }

    suspend fun updateCurrencies(currencyCode: String) {
        withContext(Dispatchers.IO) {
            val data = getConvertedData(currencyCode)
            val currencyList = data?.let { makeCurrencyList(it) }
            if (currencyList != null) {
                db.runInTransaction{
                    for (currency in currencyList) {
                        currencyDao.update(currency.rateToBase, currency.currencyCode)
                    }
                }
            }
        }
    }
    suspend fun updateCurrencies(newCurrencyList: List<Currency>) {
        withContext(Dispatchers.IO) {
            db.runInTransaction{
                for (currency in newCurrencyList) {
                    currencyDao.update(currency.rateToBase, currency.currencyCode)
                }
            }
        }
    }

    private suspend fun getConvertedData(currencyCode: String): CurrencyResponse? {
        return apiDataSource.getConvertedRate(currencyCode).body()
    }

    private fun makeCurrencyList(currencyResponse: CurrencyResponse): ArrayList<CurrencyEntity> {
        val currency = ArrayList<CurrencyEntity>()
        val rates = currencyResponse.rates
        for (rate in rates) {
            currency.add(
                CurrencyEntity(
                    null,
                    rate.key,
                    rate.value.currency_name,
                    rate.value.rate_for_amount
                )
            )
        }
        return currency
    }

    suspend fun doConvert(rateToBase: Double) {
        withContext(Dispatchers.IO) {
            currencyDao.updateAllCurrencyRate(rateToBase)
        }
    }
}
