package com.proger.cashtracker.db.query

data class FullDebtDetails(
    var id: Long,
    var start_date: Long,
    var finish_date: Long,
    var debt: Double,
    var comment: String,
    var is_returned: Boolean,//todo возможно будет проблема преобразования с типом из БД
    var is_debtor: Boolean,
    var creditor_or_debtor: String,
    var name_account: String,
    var balance_account: Double,
    var image: String,
    var currency_code: String,
    var currency_name: String,
    var current_exchange_rate_to_base: Double,
    var returned: Double,
)

data class ShortDebtDetails(
    var creditor_or_debtor: String,
    var is_debtor: Boolean,
    var debt_sum: Double,
    var returned_sum: Double
)