package com.proger.cashtracker.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.proger.cashtracker.db.query.AccountIdAndBalance
import com.proger.cashtracker.db.query.TransactionInfoFull
import com.proger.cashtracker.db.query.TransactionInfoShort
import com.proger.cashtracker.db.table.ExpenseEntity
import com.proger.cashtracker.db.table.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao : BaseDao<ExpenseEntity> {
    //сортировки
    @Query("SELECT e.id as id, e.date as date, e.comment as comment, e.expense as value, ec.image as category_image, ec.name_category as category_name, a.name as account_name, a.image as account_image, a.balance as balance, c.currency_code as currency_code, c.currency_name as currency_name, c.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM expenses e, expense_categories ec, accounts a, currencies c " +
            "WHERE e.expense_category_id = ec.id AND e.account_id = a.id AND a.currency_id = c.id AND a.is_visible = 1 " +
            "AND a.name like :accountName AND e.date BETWEEN :startDate AND :finishDate AND category_name LIKE :categoryName " +
            "ORDER BY (value / current_exchange_rate_to_base) ASC")
    fun getFullExpenseDetailsValueAsc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<TransactionInfoFull>>
    @Query("SELECT e.id as id, e.date as date, e.comment as comment, e.expense as value, ec.image as category_image, ec.name_category as category_name, a.name as account_name, a.image as account_image, a.balance as balance, c.currency_code as currency_code, c.currency_name as currency_name, c.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM expenses e, expense_categories ec, accounts a, currencies c " +
            "WHERE e.expense_category_id = ec.id AND e.account_id = a.id AND a.currency_id = c.id AND a.is_visible = 1 " +
            "AND a.name like :accountName AND e.date BETWEEN :startDate AND :finishDate AND category_name LIKE :categoryName " +
            "ORDER BY (value / current_exchange_rate_to_base) DESC")
    fun getFullExpenseDetailsValueDesc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<TransactionInfoFull>>
    @Query("SELECT e.id as id, e.date as date, e.comment as comment, e.expense as value, ec.image as category_image, ec.name_category as category_name, a.name as account_name, a.image as account_image, a.balance as balance, c.currency_code as currency_code, c.currency_name as currency_name, c.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM expenses e, expense_categories ec, accounts a, currencies c " +
            "WHERE e.expense_category_id = ec.id AND e.account_id = a.id AND a.currency_id = c.id AND a.is_visible = 1 " +
            "AND a.name like :accountName AND e.date BETWEEN :startDate AND :finishDate AND category_name LIKE :categoryName " +
            "ORDER BY date ASC")
    fun getFullExpenseDetailsDateAsc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<TransactionInfoFull>>
    @Query("SELECT e.id as id, e.date as date, e.comment as comment, e.expense as value, ec.image as category_image, ec.name_category as category_name, a.name as account_name, a.image as account_image, a.balance as balance, c.currency_code as currency_code, c.currency_name as currency_name, c.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM expenses e, expense_categories ec, accounts a, currencies c " +
            "WHERE e.expense_category_id = ec.id AND e.account_id = a.id AND a.currency_id = c.id AND a.is_visible = 1 " +
            "AND a.name like :accountName AND e.date BETWEEN :startDate AND :finishDate AND category_name LIKE :categoryName " +
            "ORDER BY date DESC")
    fun getFullExpenseDetailsDateDesc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<TransactionInfoFull>>

    @Query("SELECT e.id as id, e.date as date, e.comment as comment, e.expense as value, ec.image as category_image, ec.name_category as category_name, a.name as account_name, a.image as account_image, a.balance as balance, c.currency_code as currency_code, c.currency_name as currency_name, c.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM expenses e, expense_categories ec, accounts a, currencies c " +
            "WHERE e.expense_category_id = ec.id AND e.account_id = a.id AND a.currency_id = c.id AND a.is_visible = 1 " +
            "AND a.name like :accountName AND e.date BETWEEN :startDate AND :finishDate AND category_name LIKE :categoryName " +
            "AND comment like :comment")
    fun getFullExpenseDetailsByComment(startDate: Long, finishDate: Long, accountName: String, categoryName: String, comment: String): Flow<MutableList<TransactionInfoFull>>
    @Query("SELECT expenses.id as id, expenses.date as date, expenses.comment as comment, expenses.expense as value, expense_categories.image as category_image, expense_categories.name_category as category_name, accounts.name as account_name, accounts.image as account_image, accounts.balance as balance, currencies.currency_code as currency_code, currencies.currency_name as currency_name, currencies.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM expenses, expense_categories, accounts, currencies " +
            "WHERE expenses.expense_category_id = expense_categories.id AND expenses.account_id = accounts.id AND accounts.currency_id = currencies.id AND accounts.is_visible = 1 ")
    fun getFullExpenseDetails(): Flow<MutableList<TransactionInfoFull>>

    @Query("SELECT ec.id AS id, ec.image AS image, ec.name_category, SUM(e.expense / c.current_exchange_rate_to_base) AS suma \n" +
            "FROM expenses e " +
            "JOIN expense_categories ec ON e.expense_category_id = ec.id " +
            "JOIN accounts a ON e.account_id = a.id AND a.is_visible = 1 " +
            "JOIN currencies c ON a.currency_id = c.id " +
            "WHERE e.date BETWEEN :startDate AND :finishDate " +
            "GROUP BY ec.name_category " +
            "ORDER BY suma DESC")
    fun getShortExpenseDetailsFromAllAccounts(startDate: Long, finishDate: Long): Flow<MutableList<TransactionInfoShort>>
    @Query("SELECT expenses.id AS id, expense_categories.image AS image, expense_categories.name_category AS name_category, sum(expenses.expense) AS suma " +
            "FROM expense_categories, expenses, accounts " +
            "WHERE expense_categories.id = expenses.expense_category_id AND expenses.account_id = accounts.id AND expenses.date BETWEEN :startDate AND :finishDate AND accounts.name = :accountName AND accounts.is_visible = 1 " +
            "GROUP BY name_category ORDER BY suma DESC")
    fun getShortExpenseDetails(startDate: Long, finishDate: Long, accountName: String): Flow<MutableList<TransactionInfoShort>>

    @Query("SELECT * FROM expenses WHERE id = :id")
    fun get(id: Int): Flow<ExpenseEntity>

    @Query("SELECT COUNT(*) FROM expenses WHERE id = :id")
    fun getCountById(id: Int): Int

    @Query("UPDATE expenses SET expense_category_id = :newCategoryId WHERE expense_category_id = :oldCategoryId")
    suspend fun updateByCategoryId(oldCategoryId: Int, newCategoryId: Int)

    @Update
    suspend fun update(expenses: ExpenseEntity)

    /*@Query("SELECT expenses.id as id, expenses.date as date, expenses.comment as comment, expenses.expense as value, expense_categories.image as category_image, expense_categories.name_category as category_name, accounts.name as account_name, accounts.image as account_image, accounts.balance as balance, currencies.currency_code as currency_code, currencies.currency_name as currency_name, currencies.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM expenses, expense_categories, accounts, currencies " +
            "WHERE expenses.expense_category_id = expense_categories.id AND expenses.account_id = accounts.id AND accounts.currency_id = currencies.id " +
            "AND accounts.name = :accountName AND expenses.date BETWEEN :startDate AND :finishDate")
    fun getFullExpenseDetails(startDate: Long, finishDate: Long, accountName: String): Flow<MutableList<TransactionInfoFull>>*/

    @Query("SELECT a.id AS id, sum(expense) AS summ " +
            "FROM expenses e, expense_categories ec, accounts a " +
            "WHERE e.account_id = a.id AND e.expense_category_id = ec.id AND a.is_visible = 1 " +
            "AND ec.name_category = :nameCategory " +
            "GROUP BY a.name")
    fun getExpenseForCategoryForAccounts(nameCategory: String): Flow<MutableList<AccountIdAndBalance>>
}