package com.proger.cashtracker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.proger.cashtracker.db.AccountRepository
import com.proger.cashtracker.db.ExpenseRepository
import com.proger.cashtracker.db.IncomeRepository
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.Currency
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.ui.screens.transaction.ModeTransaction
import com.proger.cashtracker.utils.share
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ViewDetailsForMainViewModel
@Inject constructor(
    private val incomeRepository: IncomeRepository,
//    private val expenseRepository: ExpenseRepository,
) : ViewModel() {

    //    private val _incomes = MutableLiveData<List<Transaction>>()
//    val incomes: LiveData<List<Transaction>> get() = _incomes//incomeRepository.getIncomes().asLiveData()
    val incomes: LiveData<List<Transaction>> = incomeRepository.getIncomes().asLiveData()




//    fun getIncomes(startDate: Long, finishDate: Long, accountName: String) =
//        incomeRepository.getIncomes(startDate, finishDate, accountName).asLiveData().value
//    fun getIncomes() = incomeRepository.getIncomes().asLiveData().value


    init {
//        fetchUsers()
//        _incomes.postValue(incomeRepository.getIncomes().asLiveData())
//        _incomes.postValue(incomeRepository.getIncomes().first())

//        viewModelScope.launch{
//            incomes.value = incomeRepository.getIncomes().asLiveData().value//.first()
//        }
    }

    /*private fun fetchUsers() {
        viewModelScope.launch {
            incomeRepository.getIncomesEntity()
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    // handle exception
                }
                .collect { list ->
                    val transactions = list.map { Transaction(
                        date = it.date,
                        comment = it.comment,
                        value = it.income,
                        category = Category(
                            nameCategory = it.category_name,
                            image = it.category_image
                        ),
                        account = Account(
                            name = it.account_name,
                            balance = it.balance,
                            image = it.account_image,
                            currency = Currency(
                                currencyCode = it.currency_code,
                                currencyName = it.currency_name,
                                rateToUAH = it.current_exchange_rate_to_UAH
                            )
                        )
                    ) }
                    _incomes.postValue(transactions)
                }
        }
    }*/


//    val updaterView: MutableLiveData<DataForUpdateShortDetails> by lazy {
//        MutableLiveData<DataForUpdateShortDetails>()
//    }
//
//    class DataForUpdateShortDetails(
//        dateInterval: Pair<Date, Date>,
//        mode: ModeTransaction,
//        account: String
//    )


}