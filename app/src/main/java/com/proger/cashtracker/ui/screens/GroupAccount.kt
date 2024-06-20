package com.proger.cashtracker.ui.screens

import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Currency

/*
 * Этот класс представляет собой всю логику работы со счетом, который объединяет все счета, работа с
 * ним означает что идет работа со всеми одновременно. Например, если выбирается этот счет, то это
 * значит что пользователю будет показана информация обо всех счетах и данными с ними связанными
 */
class GroupAccount {
    companion object{
        private const val NAME_ACCOUNT = "Всі"
        fun getName() = NAME_ACCOUNT

//        fun extensionAccountArray(array: Array<Account>): Array<Account> {
//            val newAccount = Account(name = "Всі", balance = 0.0, image = "asset_wallet", currency = null)
//            return array.plus(newAccount)
//        }
        fun extensionAccountArray(array: List<Account>, baseCurrencyCode: String): Array<Account> {
            var balance = 0.0
            array.forEach { item -> balance += item.balance / item.currency!!.rateToBase }

            val newAccount = Account(name = NAME_ACCOUNT, balance = balance, image = "asset_wallet", currency = Currency(currencyName = "", currencyCode = baseCurrencyCode, rateToBase = 1.0))
            return array.plus(newAccount).toTypedArray()
        }

        fun isGroupAccount(accountName: String) = accountName == NAME_ACCOUNT
    }
}