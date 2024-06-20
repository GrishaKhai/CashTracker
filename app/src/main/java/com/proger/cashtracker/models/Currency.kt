package com.proger.cashtracker.models

data class Currency (
//    var id: Int? = null,
    var currencyCode: String,
    var currencyName: String,
    var rateToBase: Double
)