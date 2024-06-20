package com.proger.cashtracker.ui.dialog.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proger.cashtracker.utils.share

class CalculatorContract : ViewModel() {
    private var _data: MutableLiveData<Double> = MutableLiveData()
    fun getCalculatorValue() = _data.value
    fun setCalculatorValue(data: Double?){
        this._data.value = data?: 0.0
    }
    val data = _data.share()
}