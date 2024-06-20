package com.proger.cashtracker.ui.screens.tabs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.proger.cashtracker.models.Category

class AddEditTransactionViewModel : ViewModel() {
    //состояния выбранных данных пользователем
    //категория
    private var selectedCategory: MutableLiveData<Category> = MutableLiveData()//название картинки
    fun setSelectedCategory(category: Category){
        selectedCategory.value = category
    }
    fun getSelectedCategory() = selectedCategory.value

    //аккаунт
    private var selectedAccount: MutableLiveData<String> = MutableLiveData()//название картинки
    fun setSelectedAccount(accountName: String){
        selectedAccount.value = accountName
    }
    fun getSelectedAccount() = selectedAccount.value

    //дата
    private var selectedDate: MutableLiveData<Long> = MutableLiveData()
    fun setSelectedDate(date: Long){
        selectedDate.value = date
    }
    fun getSelectedDate() = selectedDate.value

    //интервал дат
    private var selectedIntervalDate: MutableLiveData<Pair<Long, Long>> = MutableLiveData()
    fun setSelectedIntervalDate(start: Long, finish: Long) { selectedIntervalDate.value = Pair(start, finish) }
    fun getSelectedIntervalDate() = selectedIntervalDate.value

    //число
    private val enteredValue: MutableLiveData<Double> = MutableLiveData()
    fun setEnteredValue(value: Double){
        enteredValue.value = value
    }
    fun getEnteredValue() = enteredValue.value
}