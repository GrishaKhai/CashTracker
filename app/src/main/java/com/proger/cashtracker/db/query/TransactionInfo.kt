package com.proger.cashtracker.db.query

import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.Currency
import com.proger.cashtracker.models.Transaction

data class TransactionInfoFull(
    val id: Long,
    val date: Long,
    val comment: String,
    val value: Double,

    val category_image: String,
    val category_name: String,

    val account_name: String,
    val account_image: String,
    val balance: Double,

    val currency_code: String,
    val currency_name: String,
    val current_exchange_rate_to_base: Double
)

data class TransactionInfoShort(
    val id: Long,
    var image: String,
    val name_category: String,
    val suma: Double
)

fun fromTransactionInfoFullToTransaction(tif: TransactionInfoFull) : Transaction {
    return Transaction(
        id = tif.id,
        date = tif.date,
        comment = tif.comment,
        value = tif.value,
        category = Category(
            nameCategory = tif.category_name,
            image = tif.category_image
        ),
        account = Account(
            name = tif.account_name,
            balance = tif.balance,
            image = tif.account_image,
            currency = Currency(
                currencyCode = tif.currency_code,
                currencyName = tif.currency_name,
                rateToBase = tif.current_exchange_rate_to_base
            )
        )
    )
}