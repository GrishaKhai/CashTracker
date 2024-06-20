package com.proger.cashtracker.models

data class Debtor(
    var nameDebtor: String
){
    override fun toString(): String {
        return nameDebtor
    }
}