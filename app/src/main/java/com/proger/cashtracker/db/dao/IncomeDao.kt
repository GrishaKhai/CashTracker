package com.proger.cashtracker.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.proger.cashtracker.db.query.AccountIdAndBalance
import com.proger.cashtracker.db.query.TransactionInfoFull
import com.proger.cashtracker.db.query.TransactionInfoShort
import com.proger.cashtracker.db.table.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao : BaseDao<IncomeEntity> {
//    @Query("SELECT * FROM incomes")
//    fun getAllEntity(): Flow<MutableList<IncomeEntity>>

    //сортировки
    @Query("SELECT incomes.id as id, incomes.date as date, incomes.comment as comment, incomes.income as value, income_categories.image as category_image, income_categories.name_category as category_name, accounts.name as account_name, accounts.image as account_image, accounts.balance as balance, currencies.currency_code as currency_code, currencies.currency_name as currency_name, currencies.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM incomes, income_categories, accounts, currencies " +
            "WHERE incomes.income_category_id = income_categories.id AND incomes.account_id = accounts.id AND accounts.currency_id = currencies.id AND accounts.is_visible = 1 " +
            "AND accounts.name like :accountName AND incomes.date BETWEEN :startDate AND :finishDate AND category_name LIKE :categoryName " +
            "ORDER BY (value / current_exchange_rate_to_base) ASC")
    fun getFullIncomeDetailsValueAsc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<TransactionInfoFull>>
    @Query("SELECT incomes.id as id, incomes.date as date, incomes.comment as comment, incomes.income as value, income_categories.image as category_image, income_categories.name_category as category_name, accounts.name as account_name, accounts.image as account_image, accounts.balance as balance, currencies.currency_code as currency_code, currencies.currency_name as currency_name, currencies.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM incomes, income_categories, accounts, currencies " +
            "WHERE incomes.income_category_id = income_categories.id AND incomes.account_id = accounts.id AND accounts.currency_id = currencies.id AND accounts.is_visible = 1 " +
            "AND accounts.name like :accountName AND incomes.date BETWEEN :startDate AND :finishDate AND category_name LIKE :categoryName " +
            "ORDER BY (value / current_exchange_rate_to_base) DESC")
    fun getFullIncomeDetailsValueDesc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<TransactionInfoFull>>
    @Query("SELECT incomes.id as id, incomes.date as date, incomes.comment as comment, incomes.income as value, income_categories.image as category_image, income_categories.name_category as category_name, accounts.name as account_name, accounts.image as account_image, accounts.balance as balance, currencies.currency_code as currency_code, currencies.currency_name as currency_name, currencies.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM incomes, income_categories, accounts, currencies " +
            "WHERE incomes.income_category_id = income_categories.id AND incomes.account_id = accounts.id AND accounts.currency_id = currencies.id AND accounts.is_visible = 1 " +
            "AND accounts.name like :accountName AND incomes.date BETWEEN :startDate AND :finishDate AND category_name LIKE :categoryName " +
            "ORDER BY date ASC")
    fun getFullIncomeDetailsDateAsc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<TransactionInfoFull>>
    @Query("SELECT incomes.id as id, incomes.date as date, incomes.comment as comment, incomes.income as value, income_categories.image as category_image, income_categories.name_category as category_name, accounts.name as account_name, accounts.image as account_image, accounts.balance as balance, currencies.currency_code as currency_code, currencies.currency_name as currency_name, currencies.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM incomes, income_categories, accounts, currencies " +
            "WHERE incomes.income_category_id = income_categories.id AND incomes.account_id = accounts.id AND accounts.currency_id = currencies.id AND accounts.is_visible = 1 " +
            "AND accounts.name like :accountName AND incomes.date BETWEEN :startDate AND :finishDate AND category_name LIKE :categoryName " +
            "ORDER BY date DESC")
    fun getFullIncomeDetailsDateDesc(startDate: Long, finishDate: Long, accountName: String, categoryName: String): Flow<MutableList<TransactionInfoFull>>
    @Query("SELECT incomes.id as id, incomes.date as date, incomes.comment as comment, incomes.income as value, income_categories.image as category_image, income_categories.name_category as category_name, accounts.name as account_name, accounts.image as account_image, accounts.balance as balance, currencies.currency_code as currency_code, currencies.currency_name as currency_name, currencies.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM incomes, income_categories, accounts, currencies " +
            "WHERE incomes.income_category_id = income_categories.id AND incomes.account_id = accounts.id AND accounts.currency_id = currencies.id AND accounts.is_visible = 1 " +
            "AND accounts.name like :accountName AND incomes.date BETWEEN :startDate AND :finishDate AND category_name LIKE :categoryName " +
            "AND comment like :comment")
    fun getFullIncomeDetailsByComment(startDate: Long, finishDate: Long, accountName: String, categoryName: String, comment: String): Flow<MutableList<TransactionInfoFull>>

    @Query("SELECT incomes.id as id, incomes.date as date, incomes.comment as comment, incomes.income as value, income_categories.image as category_image, income_categories.name_category as category_name, accounts.name as account_name, accounts.image as account_image, accounts.balance as balance, currencies.currency_code as currency_code, currencies.currency_name as currency_name, currencies.current_exchange_rate_to_base as current_exchange_rate_to_base " +
            "FROM incomes, income_categories, accounts, currencies " +
            "WHERE incomes.income_category_id = income_categories.id AND incomes.account_id = accounts.id AND accounts.currency_id = currencies.id AND accounts.is_visible = 1 ")
    fun getFullIncomeDetails(): Flow<MutableList<TransactionInfoFull>>

    /*@Query("SELECT sum(converted) " +
            "FROM (SELECT i.income * c.current_exchange_rate_to_base AS converted " +
            "FROM accounts a, incomes i, currencies c " +
            "WHERE a.id = i.account_id AND c.id = a.currency_id AND i.date BETWEEN :startDate AND :finishDate)")
    fun getConvertedIncomeFromAllAccounts(startDate: Long, finishDate: Long): Flow<Double>*/

    @Query("SELECT ic.id AS id, ic.image AS image, ic.name_category, SUM(i.income / c.current_exchange_rate_to_base) AS suma " +
            "FROM incomes i " +
            "JOIN income_categories ic ON i.income_category_id = ic.id " +
            "JOIN accounts a ON i.account_id = a.id AND a.is_visible = 1 " +
            "JOIN currencies c ON a.currency_id = c.id " +
            "WHERE i.date BETWEEN :startDate AND :finishDate " +
            "GROUP BY ic.name_category " +
            "ORDER BY suma DESC")
    fun getShortIncomeDetailsFromAllAccounts(startDate: Long, finishDate: Long): Flow<MutableList<TransactionInfoShort>>
    @Query("SELECT income_categories.id as id, income_categories.image AS image, income_categories.name_category AS name_category, sum(incomes.income) AS suma " +
            "FROM income_categories, incomes, accounts " +
            "WHERE income_categories.id = incomes.income_category_id AND incomes.account_id = accounts.id AND accounts.is_visible = 1 AND incomes.date BETWEEN :startDate AND :finishDate AND accounts.name LIKE :accountName " +
            "GROUP BY name_category ORDER BY suma DESC")
    fun getShortIncomeDetails(startDate: Long, finishDate: Long, accountName: String): Flow<MutableList<TransactionInfoShort>>
    /*@Query("SELECT incomes.id as id, income_categories.image AS image, income_categories.name_category AS name_category, sum(incomes.income) AS suma " +
            "FROM income_categories, incomes, accounts " +
            "WHERE income_categories.id = incomes.income_category_id AND incomes.account_id = accounts.id " +
            "GROUP BY name_category ORDER BY suma DESC")
    fun getShortIncomeDetails(): Flow<MutableList<TransactionInfoShort>>*/

    @Query("SELECT * FROM incomes WHERE id = :id")
    fun get(id: Int): Flow<IncomeEntity>

    @Query("SELECT COUNT(*) FROM incomes WHERE id = :id")
    fun getCountById(id: Int): Int

    @Query("UPDATE incomes SET income_category_id = :newCategoryId WHERE income_category_id = :oldCategoryId")
    suspend fun updateByCategoryId(oldCategoryId: Int, newCategoryId: Int)

    @Update
    suspend fun update(income: IncomeEntity)

    @Query("SELECT a.id AS id, sum(income) AS summ " +
            "FROM incomes i, income_categories ic, accounts a " +
            "WHERE i.account_id = a.id AND i.income_category_id = ic.id AND a.is_visible = 1 " +
            "AND ic.name_category = :nameCategory " +
            "GROUP BY a.name")
    fun getIncomeForCategoryForAccounts(nameCategory: String): Flow<MutableList<AccountIdAndBalance>>
}