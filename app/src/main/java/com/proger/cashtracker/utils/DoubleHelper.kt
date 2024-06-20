package com.proger.cashtracker.utils

class DoubleHelper {
    companion object{
        fun roundValue(value: Double) = String.format("%.2f", value).replace(',', '.')
    }
}

