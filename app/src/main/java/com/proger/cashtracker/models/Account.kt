package com.proger.cashtracker.models

data class Account(
//    var id: Int,
    var name: String,
    var balance: Double,
    var image: String,
    var currency: Currency?
){
    override fun toString() = name
    fun validate(){
        if (name.isBlank()) throw EmptyFieldException(Field.Account)
        if (balance < 0) throw IncorrectValueFormatException()
        if (image.isBlank()) throw EmptyFieldException(Field.Image)
        if (currency == null) throw EmptyFieldException(Field.Currency)
    }
}