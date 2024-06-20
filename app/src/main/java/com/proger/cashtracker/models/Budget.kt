package com.proger.cashtracker.models

data class Budget(
    var id: Long? = null,
    var dateStart: Long?,
    var dateFinish: Long?,
    var budget: Double?,
    var spendCash: Double,
    var expenseCategory: Category?,
){
    fun validate(){
        if (dateStart == null) throw EmptyFieldException(Field.Date)
        if (dateFinish == null) throw EmptyFieldException(Field.Date)
        if (budget == null) throw EmptyFieldException(Field.Value)

        if(dateStart!! > dateFinish!!) throw IncorrectDateFormatException()
        if(budget!! < 0) throw IncorrectValueFormatException()
        if(expenseCategory == null) throw EmptyFieldException(Field.Category)
        if(expenseCategory!!.nameCategory.isBlank() || expenseCategory!!.image.isBlank()) throw EmptyFieldException(Field.Category)
    }
}