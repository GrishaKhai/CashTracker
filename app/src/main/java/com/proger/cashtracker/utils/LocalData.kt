package com.proger.cashtracker.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.Date
import javax.inject.Inject


class LocalData @Inject constructor(
    @ActivityContext private val context: Context
) {

    private val NAME = "appPrefs"
    private val DATE_KEY = "lastDateSyncApi"
    private val BASE_CURRENCY_KEY = "baseCurrency"
    private val IS_USE_PIN_CODE_KEY = "isUsePinCode"


    private val localSharedPref = context.getSharedPreferences(NAME, MODE_PRIVATE)


    fun updateDateSyncWithApi(date: Date) {
        val editor = localSharedPref.edit()
        editor.putLong(DATE_KEY, date.time)
        editor.apply()
    }
    fun getLastDateSyncWithApi() = localSharedPref.getLong(DATE_KEY, 0)

    fun updateBaseCurrency(currency: String){
        val editor = localSharedPref.edit()
        editor.putString(BASE_CURRENCY_KEY, currency)
        editor.apply()
    }
    fun getBaseCurrency() = localSharedPref.getString(BASE_CURRENCY_KEY, "UAH")

    fun setIsUsingPinCode(isUsing: Boolean){
        val editor = localSharedPref.edit()
        editor.putBoolean(IS_USE_PIN_CODE_KEY, isUsing)
        editor.apply()
    }
    fun getIsUsingPinCode() = localSharedPref.getBoolean(IS_USE_PIN_CODE_KEY, false)
}