package com.proger.cashtracker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.proger.cashtracker.ui.activity.MainActivity


object NavigationUtils {
    /*
     * Хранит режим открытой вкладки
     */
    private val optionPageNavigation = MutableLiveData<MainActivity.Option>().apply { value = MainActivity.Option.Income }
    fun getOptionPageLiveData(): LiveData<MainActivity.Option> = optionPageNavigation
    fun getOptionPage() = optionPageNavigation.value
    fun setOptionPage(option: MainActivity.Option) { optionPageNavigation.value = option}
}