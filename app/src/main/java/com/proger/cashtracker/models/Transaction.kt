package com.proger.cashtracker.models

import com.proger.cashtracker.utils.DateHelper

data class Transaction(
    var id: Long? = null,
    var date: Long?,
    var comment: String?,
    var value: Double?,
    var category: Category?,
    var account: Account?
) {
    fun validate() {
        if (date == null) throw EmptyFieldException(Field.Date)
        if (DateHelper.getStartNextDay().time < date!!) throw IncorrectDateFormatException()
//        if (DateHelper.getFinishDay(Date(date!!)).time < date!!) throw IncorrectDateFormatException()
        if (value == null) throw EmptyFieldException(Field.Value)
        if (value!! <= 0) throw IncorrectValueFormatException()
        if (category == null) throw EmptyFieldException(Field.Category)
        if (account == null) throw EmptyFieldException(Field.Account)
    }
}