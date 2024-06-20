package com.proger.cashtracker.models

data class Category (
    var nameCategory: String,
    var image: String
){
    fun validate(){
        if (nameCategory.isBlank()) throw EmptyFieldException(Field.Category)
        if (image.isBlank()) throw EmptyFieldException(Field.Image)
    }
}