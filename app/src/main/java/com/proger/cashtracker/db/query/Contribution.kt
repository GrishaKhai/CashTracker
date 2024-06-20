package com.proger.cashtracker.db.query

data class ContributionFullDetails(
    val id: Long,
    val debt_id: Long,

    var date: Long,
    var contribution: Double,

    var account_name: String,
    var balance: Double,
    var image: String,

    var currency_code: String,
    var currency_name: String,
    var rate_to_base: Double
)

//data class ContributionShortDetails(
//    var date: Long,
//    var contribution: Double,
//
//    var account_name: String,
//    var balance: Double,
//    var image: String,
//
//    var currency_code: String,
//    var currency_name: String,
//    var rate_to_base: Double
//)