package com.proger.cashtracker.api.currencyConversion

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApiService {
    @GET(EndPoints.CONVERT_URL)
    suspend fun getCurrencyRates(
        @Query("api_key") accessKey: String,
        @Query("from") from: String,
        @Query("amount") amount: Double
    ) : Response<CurrencyResponse>
}


//    @GET("convert")
//    suspend fun getCurrencyRates(
//        @Query("api_key") access_key: String = "6e23fc2dbcb4d47e4170d27014bb565fe43c39df",
//        @Query("from") from: String = "UAH",
////        @Query("to") to: String,
//        @Query("amount") amount: Double = 1.0,
//        @Query("format") format: String = "json"
//    ) : Response<CurrencyResponse>
