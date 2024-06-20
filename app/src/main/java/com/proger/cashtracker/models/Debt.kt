package com.proger.cashtracker.models

data class Debt (
    var id: Long?,
    var debt: Double?,
    var returned: Double = 0.0,
    var startDate: Long?,
    var finishDate: Long?,
    var comment: String?,
    var isReturned: Boolean = false,
    var isDebtor: Boolean,
    var creditorOrDebtor: Debtor?,
    var account: Account?
){
    fun validate(){
        if(debt == null) throw EmptyFieldException(Field.Value)
        if(debt!! < 0) throw IncorrectValueFormatException()

        if(startDate == null) throw EmptyFieldException(Field.Date)
        if(finishDate == null) throw EmptyFieldException(Field.Date)
        if(startDate!! > finishDate!!) throw IncorrectDateFormatException()

        if(creditorOrDebtor == null) throw EmptyFieldException(Field.Debtor)

        if (account == null) throw EmptyFieldException(Field.Account)
        if(account!!.name.isBlank()) throw EmptyFieldException(Field.Account)
    }
}