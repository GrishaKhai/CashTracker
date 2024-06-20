package com.proger.cashtracker.db

import com.proger.cashtracker.db.dao.AccountDao
import com.proger.cashtracker.db.dao.IncomeCategoryDao
import com.proger.cashtracker.db.dao.IncomeDao
import com.proger.cashtracker.db.query.AccountIdAndBalance
import com.proger.cashtracker.db.query.fromTransactionInfoFullToTransaction
import com.proger.cashtracker.db.table.IncomeCategoryEntity
import com.proger.cashtracker.db.table.IncomeEntity
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.NoteExistInDbException
import com.proger.cashtracker.models.EditedNoteNotExistInDbException
import com.proger.cashtracker.models.NewNoteDataExitsInDb
import com.proger.cashtracker.models.NoteNotExistInDbException
import com.proger.cashtracker.models.Transaction
import com.proger.cashtracker.models.TransactionByCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IncomeRepository
@Inject constructor(
    private var incomeDao: IncomeDao,
    private var incomeCategoryDao: IncomeCategoryDao,
    private var accountDao: AccountDao
) {


    //====================================== INCOME CATEGORY ======================================
//    suspend fun insertIncomeCategory(incomeCategory: IncomeCategoryEntity) {
//        withContext(Dispatchers.IO) {
//            incomeCategoryDao.insert(incomeCategory)
//        }
//    }
//    fun getIncomeCategories(): Flow<MutableList<IncomeCategoryEntity>> {
//        return incomeCategoryDao.getAll()
//    }
    fun getIncomeCategories(): Flow<MutableList<Category>> {
        return incomeCategoryDao.getAll().map { list ->
            list.map {
                Category(
                    nameCategory = it.nameCategory,
                    image = it.image
                )
            }.toMutableList()
        }
    }

    suspend fun insertIncomeCategory(category: Category) {
        withContext(Dispatchers.IO) {
            category.validate()//поверхностная проверка полей
            if (incomeCategoryDao.getCountByName(category.nameCategory) > 0) {//такая категория уже есть в БД
                throw NoteExistInDbException(category.nameCategory)
            }

            incomeCategoryDao.insert(
                IncomeCategoryEntity(
                    id = null,
                    nameCategory = category.nameCategory,
                    image = category.image
                )
            )
        }
    }

    suspend fun updateIncomeCategory(oldNameCategory: String, category: Category) {
        withContext(Dispatchers.IO) {
            category.validate()//поверхностная проверка полей
            val oldIncomeCategoryId = incomeCategoryDao.getId(oldNameCategory)
                ?: throw EditedNoteNotExistInDbException()//редактируемой записи нет
            //новое имя записи уже есть в БД, но при этом это не редактируемая запись
            val newIncomeCategoryId = incomeCategoryDao.getId(category.nameCategory)
            if ((newIncomeCategoryId != null) && newIncomeCategoryId != oldIncomeCategoryId) throw NewNoteDataExitsInDb()

            val incomeCategory = IncomeCategoryEntity(
                id = oldIncomeCategoryId,
                nameCategory = category.nameCategory,
                image = category.image
            )
            incomeCategoryDao.update(incomeCategory)
        }
    }

    suspend fun moveIncomesToNewCategory(oldCategoryName: String, newCategoryName: String) {
        withContext(Dispatchers.IO) {
            if (incomeCategoryDao.getCountByName(oldCategoryName) == 0)
                throw NoteNotExistInDbException(oldCategoryName)
            if(incomeCategoryDao.getCountByName(newCategoryName) == 0)
                throw NoteNotExistInDbException(newCategoryName)

            val oldId = incomeCategoryDao.getId(oldCategoryName)!!
            val newId = incomeCategoryDao.getId(newCategoryName)!!

            incomeDao.updateByCategoryId(oldId, newId)
        }
    }

    suspend fun deleteIncomeCategory(nameCategory: String){
        withContext(Dispatchers.IO){
            val incomeCategory = incomeCategoryDao.get(nameCategory)
                ?: throw NoteNotExistInDbException(nameCategory)
            incomeCategoryDao.delete(incomeCategory)
        }
    }


    //======================================== INCOME ========================================
    fun getShortIncomesByCategories(
        startDate: Long,
        finishDate: Long,
        accountName: String
    ): Flow<MutableList<TransactionByCategories>> {
        return incomeDao.getShortIncomeDetails(startDate, finishDate, accountName).map { list ->
            list.map {
                TransactionByCategories(
                    image = it.image,
                    nameCategory = it.name_category,
                    sumTransaction = it.suma
                )
            }.toMutableList()
        }
    }

    fun getShortIncomeDetailsFromAllAccounts(
        startDate: Long,
        finishDate: Long
    ): Flow<MutableList<TransactionByCategories>> {
        return incomeDao.getShortIncomeDetailsFromAllAccounts(startDate, finishDate).map { list ->
            list.map {
                TransactionByCategories(
                    image = it.image,
                    nameCategory = it.name_category,
                    sumTransaction = it.suma
                )
            }.toMutableList()
        }
    }

    fun getFullIncomeDetailsValueAsc(
        startDate: Long,
        finishDate: Long,
        accountName: String,
        categoryName: String
    ): Flow<MutableList<Transaction>> {
        return incomeDao.getFullIncomeDetailsValueAsc(
            startDate,
            finishDate,
            accountName,
            categoryName
        ).map { list ->
            list.map { fromTransactionInfoFullToTransaction(it) }.toMutableList()
        }
    }

    fun getFullIncomeDetailsValueDesc(
        startDate: Long,
        finishDate: Long,
        accountName: String,
        categoryName: String
    ): Flow<MutableList<Transaction>> {
        return incomeDao.getFullIncomeDetailsValueDesc(
            startDate,
            finishDate,
            accountName,
            categoryName
        ).map { list ->
            list.map { fromTransactionInfoFullToTransaction(it) }.toMutableList()
        }
    }

    fun getFullIncomeDetailsDateAsc(
        startDate: Long,
        finishDate: Long,
        accountName: String,
        categoryName: String
    ): Flow<MutableList<Transaction>> {
        return incomeDao.getFullIncomeDetailsDateAsc(
            startDate,
            finishDate,
            accountName,
            categoryName
        ).map { list ->
            list.map { fromTransactionInfoFullToTransaction(it) }.toMutableList()
        }
    }

    fun getFullIncomeDetailsDateDesc(
        startDate: Long,
        finishDate: Long,
        accountName: String,
        categoryName: String
    ): Flow<MutableList<Transaction>> {
        return incomeDao.getFullIncomeDetailsDateDesc(
            startDate,
            finishDate,
            accountName,
            categoryName
        ).map { list ->
            list.map { fromTransactionInfoFullToTransaction(it) }.toMutableList()
        }
    }

    fun getFullIncomeDetailsByComment(
        startDate: Long,
        finishDate: Long,
        accountName: String,
        categoryName: String,
        comment: String
    ): Flow<MutableList<Transaction>> {
        return incomeDao.getFullIncomeDetailsByComment(
            startDate,
            finishDate,
            accountName,
            categoryName,
            comment
        ).map { list ->
            list.map { fromTransactionInfoFullToTransaction(it) }.toMutableList()
        }
    }

    fun getIncomes(): Flow<MutableList<Transaction>> {
        return incomeDao.getFullIncomeDetails().map { list ->
            list.map {
                fromTransactionInfoFullToTransaction(it)
            }.toMutableList()
        }
    }


    suspend fun insertIncome(income: Transaction) = withContext(Dispatchers.IO) {
        income.validate()//поверхностная проверка полей
        val account = accountDao.get(income.account!!.name)
//        delay(200)

        val categoryId = incomeCategoryDao.getId(income.category!!.nameCategory)
        val incomeEntity = IncomeEntity(
            id = null,
            date = income.date!!,
            comment = income.comment!!,
            income = income.value!!,
            incomeCategoryId = categoryId!!,
            accountId = account.id!!
        )
        val id = incomeDao.insert(incomeEntity)
        accountDao.updateAccountBalance(account.id!!, income.value!!)
        return@withContext id
    }

    suspend fun updateIncome(oldIncomeId: Int, income: Transaction) {
        withContext(Dispatchers.IO) {
            income.validate()//поверхностная проверка полей
            if (incomeDao.getCountById(oldIncomeId) == 0) throw EditedNoteNotExistInDbException()//редактируемой записи нет
//            delay(200)

            val oldIncomeNote = incomeDao.get(oldIncomeId)
            val account = accountDao.get(income.account!!.name)
            val categoryId = incomeCategoryDao.getId(income.category!!.nameCategory)

            val incomeEntity = IncomeEntity(
                id = oldIncomeId,
                date = income.date!!,
                comment = income.comment!!,
                income = income.value!!,
                incomeCategoryId = categoryId!!,
                accountId = account.id!!
            )
            val difference = income.value!! - oldIncomeNote.first().income
            incomeDao.update(incomeEntity)
            accountDao.updateAccountBalance(account.id!!, difference)
        }
    }

    suspend fun deleteIncome(income: Transaction) {
        withContext(Dispatchers.IO) {
            val account = accountDao.get(income.account!!.name)
//            delay(100)

            val categoryId = incomeCategoryDao.getId(income.category!!.nameCategory)
            val incomeEntity = IncomeEntity(
                id = income.id!!.toInt(),
                date = income.date!!,
                comment = income.comment!!,
                income = income.value!!,
                incomeCategoryId = categoryId!!,
                accountId = account.id!!
            )
            incomeDao.delete(incomeEntity)
            accountDao.updateAccountBalance(account.id!!, -income.value!!)
        }
    }

    suspend fun updateBalanceAccountsFromIncomeCategory(categoryName: String) {
        withContext(Dispatchers.IO){
            val incomeForAccounts: List<AccountIdAndBalance> = incomeDao.getIncomeForCategoryForAccounts(categoryName).first()
            for(account in incomeForAccounts){
                accountDao.updateAccountBalance(account.id, -account.summ)
            }
        }
    }

}