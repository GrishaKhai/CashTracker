package com.proger.cashtracker.models

data class Contribution (
    val contributionId: Long? = null,
    val debtId: Long? = null,

    val debtValue: Double? = null,
    val returnedValue: Double? = null,
    val isDebtor: Boolean? = null,


    var date: Long,
    var contribution: Double?,
    val account: Account?,
){
    fun validate(){
        if(contribution == null) throw EmptyFieldException(Field.Value)
        if(contribution!! <= 0) throw IncorrectValueFormatException()
        if (account == null) throw EmptyFieldException(Field.Account)
        if(account.name.isBlank()) throw EmptyFieldException(Field.Account)
//        if (debt == null) throw EmptyFieldException(Field.Debt)
//        if(debt.id == null) throw EmptyFieldException(Field.Debt)
    }
}

//data class ContributionWithDebtInfo (
//    val contributionId: Long? = null,
//    val debtId: Long? = null,
//
//    val debtValue: Double,
//    val returnedValue: Double,
//    val isDebtor: Boolean,
//
//    var date: Long,
//    var contribution: Double?,
//    val account: Account?,
//)