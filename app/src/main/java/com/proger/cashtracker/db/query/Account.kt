package com.proger.cashtracker.db.query

data class AccountWithCurrency(
    var account_id: Int? = null,
    var name: String,
    var balance: Double,
    var image: String,

    var currency_code: String,
    var currency_name: String,
    var rate_to_base: Double
)

data class AccountIdAndBalance(
    val id: Int,
    val summ: Double,
)