package com.proger.cashtracker.api.currencyConversion

data class CurrencyResponse(
    val amount: String,
    val base_currency_code: String,
    val base_currency_name: String,
    var rates: Map<String, Rate> = HashMap(),
    val status: String,
    val updated_date: String
)
