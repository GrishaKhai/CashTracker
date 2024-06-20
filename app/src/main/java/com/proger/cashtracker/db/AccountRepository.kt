package com.proger.cashtracker.db

import com.proger.cashtracker.db.dao.AccountDao
import com.proger.cashtracker.db.dao.CurrencyDao
import com.proger.cashtracker.db.table.AccountEntity
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Currency
import com.proger.cashtracker.models.EditedNoteNotExistInDbException
import com.proger.cashtracker.models.NewNoteDataExitsInDb
import com.proger.cashtracker.models.NoteExistInDbException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository
@Inject constructor(
    private var accountDao: AccountDao,
    private var currencyDao: CurrencyDao,
    private val db: AppDatabase
) {
//    val accounts: Flow<List<Account>> = accountDao.getAccounts().map { entity -> entity.map { it.toAccount() } }

    val accountsWithCurrency: Flow<List<Account>> = accountDao.getAccountsWithCurrency().map { entity -> entity.map {
        Account(it.name, it.balance, it.image, Currency(it.currency_code, it.currency_name, it.rate_to_base)) } }

    suspend fun getAccounts(): List<Account> {
        var result : List<Account> = ArrayList<Account>()
        accountDao.getAll().collect { list ->
            result = list.map { it -> it.toAccount() }
        }
        return result
    }

//    fun getAccount(nameAccount: String): Flow<Account> = accountDao.getAccount(nameAccount).collect{ it.toAccount()}. .toAccount()//: Flow<Account>
//    suspend fun getAccount(nameAccount: String): Account {
//        return accountDao.getAccount(nameAccount).toAccount()
//    }
    /**
     * Удаление с возможностью перенести все средства (доходы, расходы, долги)
     */
    suspend fun deleteAccount(sourceAccount: Account, destinationAccount: Account){
//todo реализуй функцию        withContext(Dispatchers.IO){
//            val deletedAccount = accountDao.get(account.name)
//            if(accountDao.getCountByName(account.name) > 0) {//удаление записи
//                accountDao.delete(deletedAccount)
//            }
//        }
    }

    /**
     * Удаление без возможности перенести все средства
     */
    suspend fun deleteAccount(account: Account){
        withContext(Dispatchers.IO){
            val deletedAccount = accountDao.get(account.name)
            if(accountDao.getCountByName(account.name) > 0) {//удаление записи
                accountDao.delete(deletedAccount)
            }
        }
    }
    suspend fun setActiveStateAccount(accountName: String, isActive: Boolean) {
        withContext(Dispatchers.IO){
            if(accountDao.getCountByName(accountName) > 0) {
                accountDao.updateActiveState(accountName, if(isActive) 1 else 0)
            }
        }
    }
    suspend fun insertAccount(account: Account){
        withContext(Dispatchers.IO){
            account.validate()
            if(accountDao.getCountByName(account.name) > 0) throw NoteExistInDbException(account.name)
            delay(200)

            val currency = currencyDao.get(account.currency!!.currencyCode).first()

            val accountEntity = AccountEntity(
                id = null,
                name = account.name,
                balance = account.balance,
                image = account.image,
                currencyId = currency.id!!
            )
            accountDao.insert(accountEntity)
        }
    }
    //при выполнении обновления счета и в случае если пользователь сменил валюту, то все доходы и траты связанные с этим счетом изменяться не будут
    suspend fun updateAccount(oldAccountName: String, account: Account){
        withContext(Dispatchers.IO){
            account.validate()
            val oldAccountId = accountDao.getId(oldAccountName) ?: throw EditedNoteNotExistInDbException()//редактируемой записи нет
            //новое имя записи уже есть в БД, но при этом это не редактируемая запись
            val newAccountId = accountDao.getId(account.name)
            if ((newAccountId != null) && newAccountId != oldAccountId) throw NewNoteDataExitsInDb()
            delay(200)

            val currency = currencyDao.get(account.currency!!.currencyCode).first()

            val accountEntity = AccountEntity(
                id = null,
                name = account.name,
                balance = account.balance,
                image = account.image,
                currencyId = currency.id!!
            )
            accountDao.update(accountEntity)
        }
    }


    fun AccountEntity.toAccount(): Account {
        return Account(this.name, this.balance, this.image, null)
    }
}