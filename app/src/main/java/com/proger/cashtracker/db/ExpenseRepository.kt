package com.proger.cashtracker.db

import com.proger.cashtracker.db.dao.AccountDao
import com.proger.cashtracker.db.dao.ExpenseCategoryDao
import com.proger.cashtracker.db.dao.ExpenseDao
import com.proger.cashtracker.db.query.AccountIdAndBalance
import com.proger.cashtracker.db.query.fromTransactionInfoFullToTransaction
import com.proger.cashtracker.db.table.ExpenseCategoryEntity
import com.proger.cashtracker.db.table.ExpenseEntity
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
class ExpenseRepository
@Inject constructor(
    private var expenseDao: ExpenseDao,
    private var expenseCategoryDao: ExpenseCategoryDao,
    private var accountDao: AccountDao
) {


    //====================================== EXPENSE CATEGORY ======================================
    //    suspend fun insertExpenseCategories(expenseCategories: List<ExpenseCategory>){
//        withContext(Dispatchers.IO){
//            expenseCategoryDao.insert(expenseCategories)
//        }
//    }
//    fun getExpenseCategories(): Flow<List<ExpenseCategoryEntity>> {
//        return expenseCategoryDao.getAll()
//    }
    fun getExpenseCategories(): Flow<MutableList<Category>>{
        return expenseCategoryDao.getAll().map { list ->
            list.map {
                Category(
                    nameCategory = it.nameCategory,
                    image = it.image
                )
            }.toMutableList()
        }
    }

    suspend fun insertExpenseCategory(category: Category) {
        withContext(Dispatchers.IO) {
            category.validate()//поверхностная проверка полей
            if (expenseCategoryDao.getCountByName(category.nameCategory) > 0) {//такая категория уже есть в БД
                throw NoteExistInDbException(category.nameCategory)
            }
            delay(200)

            expenseCategoryDao.insert(
                ExpenseCategoryEntity(
                    id = null,
                    nameCategory = category.nameCategory,
                    image = category.image
                )
            )
        }
    }
    suspend fun updateExpenseCategory(oldNameCategory: String, category: Category) {
        withContext(Dispatchers.IO) {
            category.validate()//поверхностная проверка полей
            val oldExpenseCategoryId = expenseCategoryDao.getId(oldNameCategory)
                ?: throw EditedNoteNotExistInDbException()//редактируемой записи нет
            //новое имя записи уже есть в БД, но при этом это не редактируемая запись
            val newExpenseCategoryId = expenseCategoryDao.getId(category.nameCategory)
            if ((newExpenseCategoryId != null) && newExpenseCategoryId != oldExpenseCategoryId) throw NewNoteDataExitsInDb()
            delay(200)

            val expenseCategory = ExpenseCategoryEntity(
                id = oldExpenseCategoryId,
                nameCategory = category.nameCategory,
                image = category.image
            )
            expenseCategoryDao.update(expenseCategory)
        }
    }

    suspend fun moveExpensesToNewCategory(oldCategoryName: String, newCategoryName: String) {
        withContext(Dispatchers.IO) {
            if (expenseCategoryDao.getCountByName(oldCategoryName) == 0)
                throw NoteNotExistInDbException(oldCategoryName)
            if(expenseCategoryDao.getCountByName(newCategoryName) == 0)
                throw NoteNotExistInDbException(newCategoryName)

            val oldId = expenseCategoryDao.getId(oldCategoryName)!!
            val newId = expenseCategoryDao.getId(newCategoryName)!!

            expenseDao.updateByCategoryId(oldId, newId)
        }
    }

    suspend fun deleteExpenseCategory(nameCategory: String){
        withContext(Dispatchers.IO){
            val expenseCategory = expenseCategoryDao.get(nameCategory)
                ?: throw NoteNotExistInDbException(nameCategory)
            expenseCategoryDao.delete(expenseCategory)
        }
    }


    //========================================== EXPENSE ==========================================
    fun getShortExpensesByCategories(startDate: Long, finishDate: Long, accountName: String): Flow<MutableList<TransactionByCategories>>{
        return expenseDao.getShortExpenseDetails(startDate, finishDate, accountName).map { list ->
            list.map {
                TransactionByCategories(
                    image = it.image,
                    nameCategory = it.name_category,
                    sumTransaction = it.suma
                )
            }.toMutableList()
        }
    }
    fun getShortExpenseDetailsFromAllAccounts(startDate: Long, finishDate: Long): Flow<MutableList<TransactionByCategories>>{
        return expenseDao.getShortExpenseDetailsFromAllAccounts(startDate, finishDate).map { list ->
            list.map {
                TransactionByCategories(
                    image = it.image,
                    nameCategory = it.name_category,
                    sumTransaction = it.suma
                )
            }.toMutableList()
        }
    }
    fun getFullExpenseDetailsValueAsc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<Transaction>> {
        return expenseDao.getFullExpenseDetailsValueAsc(startDate, finishDate, accountName, categoryName).map { list ->
            list.map { fromTransactionInfoFullToTransaction(it) }.toMutableList()}
    }
    fun getFullExpenseDetailsValueDesc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<Transaction>> {
        return expenseDao.getFullExpenseDetailsValueDesc(startDate, finishDate, accountName, categoryName).map { list ->
            list.map { fromTransactionInfoFullToTransaction(it) }.toMutableList()}
    }
    fun getFullExpenseDetailsDateAsc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<Transaction>> {
        return expenseDao.getFullExpenseDetailsDateAsc(startDate, finishDate, accountName, categoryName).map { list ->
            list.map { fromTransactionInfoFullToTransaction(it) }.toMutableList()}
    }
    fun getFullExpenseDetailsDateDesc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<Transaction>> {
        return expenseDao.getFullExpenseDetailsDateDesc(startDate, finishDate, accountName, categoryName).map { list ->
            list.map { fromTransactionInfoFullToTransaction(it) }.toMutableList()}
    }
    fun getFullExpenseDetailsByComment(startDate: Long, finishDate: Long, accountName: String, categoryName: String, comment: String): Flow<MutableList<Transaction>> {
        return expenseDao.getFullExpenseDetailsByComment(startDate, finishDate, accountName, categoryName, comment).map { list ->
            list.map { fromTransactionInfoFullToTransaction(it) }.toMutableList()}
    }
    fun getExpenses(): Flow<MutableList<Transaction>> {
        return expenseDao.getFullExpenseDetails().map { list ->
            list.map {
                fromTransactionInfoFullToTransaction(it)
            }.toMutableList()
        }
    }


    suspend fun insertExpense(expense: Transaction) = withContext(Dispatchers.IO) {
        expense.validate()//поверхностная проверка полей
        val account = accountDao.get(expense.account!!.name)
        delay(200)

        val categoryId = expenseCategoryDao.getId(expense.category!!.nameCategory)
        val expenseEntity = ExpenseEntity(
            id = null,
            date = expense.date!!,
            comment = expense.comment!!,
            expense = expense.value!!,
            expenseCategoryId = categoryId!!,
            accountId = account.id!!
        )
        val id = expenseDao.insert(expenseEntity)
        accountDao.updateAccountBalance(account.id!!, (-1) * expense.value!!)
        return@withContext id
    }
    suspend fun updateExpense(oldExpenseId: Int, expense: Transaction) {
        withContext(Dispatchers.IO) {
            expense.validate()//поверхностная проверка полей
            if (expenseDao.getCountById(oldExpenseId) == 0) throw EditedNoteNotExistInDbException()//редактируемой записи нет
            delay(200)

            val oldExpenseNote = expenseDao.get(oldExpenseId)
            val account = accountDao.get(expense.account!!.name)
            val categoryId = expenseCategoryDao.getId(expense.category!!.nameCategory)

            val expenseEntity = ExpenseEntity(
                id = oldExpenseId,
                date = expense.date!!,
                comment = expense.comment!!,
                expense = expense.value!!,
                expenseCategoryId = categoryId!!,
                accountId = account.id!!
            )
            val difference = oldExpenseNote.first().expense - expense.value!!
            expenseDao.update(expenseEntity)
            accountDao.updateAccountBalance(account.id!!, difference)
        }
    }
    suspend fun deleteExpense(expense: Transaction) {
        withContext(Dispatchers.IO) {
            val account = accountDao.get(expense.account!!.name)
            delay(100)

            val categoryId = expenseCategoryDao.getId(expense.category!!.nameCategory)
            val expenseEntity = ExpenseEntity(
                id = expense.id!!.toInt(),
                date = expense.date!!,
                comment = expense.comment!!,
                expense = expense.value!!,
                expenseCategoryId = categoryId!!,
                accountId = account.id!!
            )
            expenseDao.delete(expenseEntity)
            accountDao.updateAccountBalance(account.id!!, -expense.value!!)
        }
    }

    suspend fun updateBalanceAccountsFromExpenseCategory(categoryName: String) {
        withContext(Dispatchers.IO){
            val incomeForAccounts: List<AccountIdAndBalance> = expenseDao.getExpenseForCategoryForAccounts(categoryName).first()
            for(account in incomeForAccounts){
                accountDao.updateAccountBalance(account.id, -account.summ)
            }
        }
    }

}