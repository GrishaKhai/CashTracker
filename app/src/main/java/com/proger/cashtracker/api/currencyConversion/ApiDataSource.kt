package com.proger.cashtracker.api.currencyConversion

import javax.inject.Inject

class ApiDataSource @Inject constructor(private val apiService: CurrencyApiService) {
    suspend fun getConvertedRate(currencyCode: String) =
        apiService.getCurrencyRates(EndPoints.API_KEY, currencyCode, 1.0)
}
//    suspend fun getConvertedRate() =
//        apiService.getCurrencyRates(EndPoints.API_KEY, "UAH", 1.0)
