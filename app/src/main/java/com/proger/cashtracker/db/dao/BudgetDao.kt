package com.proger.cashtracker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import com.proger.cashtracker.db.query.BudgetFullInfo
import com.proger.cashtracker.db.query.BudgetShortInfo
import com.proger.cashtracker.db.table.BudgetEntity
import com.proger.cashtracker.db.table.ExpenseCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao : BaseDao<BudgetEntity>{

//    @Query("SELECT b.id as id, b.date_start as date_start, b.date_finish as date_finish, b.total_cash as total_cash, ec.name_category as name_category, ec.image as image " +
//            "FROM budgets b, expense_categories ec " +
//            "WHERE b.expense_category_id = ec.id")
//    fun getAll(): Flow<MutableList<BudgetFullInfo>>
//    @Query("SELECT b.id as id, b.date_start as date_start, b.date_finish as date_finish, b.total_cash as total_cash, ec.name_category as name_category, ec.image as image " +
//            "FROM budgets b, expense_categories ec " +
//            "WHERE b.expense_category_id = ec.id " +
//            "AND :startDate BETWEEN date_start AND date_finish OR :finishDate BETWEEN date_start AND date_finish")
//    fun getByDateInterval(startDate: Long, finishDate: Long): Flow<MutableList<BudgetFullInfo>>

//    @Query("SELECT min(b.date_start) as date_start, max(b.date_finish) as date_finish, sum(b.total_cash) as total_cash, ec.name_category as name_category, ec.image as image " +
//            "FROM budgets b, expense_categories ec " +
//            "WHERE b.expense_category_id = ec.id " +
//            "AND (:startDate BETWEEN date_start AND date_finish OR :finishDate BETWEEN date_start AND date_finish) " +
//            "GROUP BY name_category")
//    fun getTotalByCategories(startDate: Long, finishDate: Long): Flow<MutableList<BudgetShortInfo>>

    //sorting
    @Query("SELECT b.id as id, b.date_start, b.date_finish, COALESCE(SUM(e.expense / c.current_exchange_rate_to_base), 0) as spend_cash, b.total_cash as budget, ec.name_category, ec.image AS image " +
            "FROM budgets b " +
            "JOIN expense_categories ec ON ec.id = b.expense_category_id " +
            "LEFT JOIN expenses e ON e.date BETWEEN b.date_start AND b.date_finish AND e.expense_category_id = b.expense_category_id " +
            "LEFT JOIN accounts a ON e.account_id = a.id AND a.is_visible = 1 " +
            "LEFT JOIN currencies c ON a.currency_id = c.id " +
//            "WHERE ((b.date_start >= :start AND b.date_finish <= :finish) OR (b.date_start < :start AND b.date_finish > :finish) OR (b.date_start BETWEEN :start AND :finish) OR (b.date_finish BETWEEN :start AND :finish)) " +
            "WHERE ((b.date_start BETWEEN :start AND :finish) OR (b.date_finish BETWEEN :start AND :finish) OR (:start BETWEEN b.date_start AND b.date_finish) OR (:finish BETWEEN b.date_start AND b.date_finish)) " +
            "AND name_category = :categoryName " +
            "GROUP BY b.id " +
            "ORDER BY budget ASC")
    fun getFullBudgetDetailsValueAsc(start: Long, finish: Long, categoryName: String): Flow<MutableList<BudgetFullInfo>>
    @Query("SELECT b.id as id, b.date_start, b.date_finish, COALESCE(SUM(e.expense / c.current_exchange_rate_to_base), 0) as spend_cash, b.total_cash as budget, ec.name_category, ec.image AS image " +
            "FROM budgets b " +
            "JOIN expense_categories ec ON ec.id = b.expense_category_id " +
            "LEFT JOIN expenses e ON e.date BETWEEN b.date_start AND b.date_finish AND e.expense_category_id = b.expense_category_id " +
            "LEFT JOIN accounts a ON e.account_id = a.id AND a.is_visible = 1 " +
            "LEFT JOIN currencies c ON a.currency_id = c.id " +
//            "WHERE ((b.date_start >= :start AND b.date_finish <= :finish) OR (b.date_start < :start AND b.date_finish > :finish) OR (b.date_start BETWEEN :start AND :finish) OR (b.date_finish BETWEEN :start AND :finish)) " +
            "WHERE ((b.date_start BETWEEN :start AND :finish) OR (b.date_finish BETWEEN :start AND :finish) OR (:start BETWEEN b.date_start AND b.date_finish) OR (:finish BETWEEN b.date_start AND b.date_finish)) " +
            "AND name_category = :categoryName " +
            "GROUP BY b.id " +
            "ORDER BY budget DESC")
    fun getFullBudgetDetailsValueDesc(start: Long, finish: Long, categoryName: String): Flow<MutableList<BudgetFullInfo>>
    @Query("SELECT b.id as id, b.date_start, b.date_finish, COALESCE(SUM(e.expense / c.current_exchange_rate_to_base), 0) as spend_cash, b.total_cash as budget, ec.name_category, ec.image AS image " +
            "FROM budgets b " +
            "JOIN expense_categories ec ON ec.id = b.expense_category_id " +
            "LEFT JOIN expenses e ON e.date BETWEEN b.date_start AND b.date_finish AND e.expense_category_id = b.expense_category_id " +
            "LEFT JOIN accounts a ON e.account_id = a.id AND a.is_visible = 1 " +
            "LEFT JOIN currencies c ON a.currency_id = c.id " +
//            "WHERE ((b.date_start >= :start AND b.date_finish <= :finish) OR (b.date_start < :start AND b.date_finish > :finish) OR (b.date_start BETWEEN :start AND :finish) OR (b.date_finish BETWEEN :start AND :finish)) " +
            "WHERE ((b.date_start BETWEEN :start AND :finish) OR (b.date_finish BETWEEN :start AND :finish) OR (:start BETWEEN b.date_start AND b.date_finish) OR (:finish BETWEEN b.date_start AND b.date_finish)) " +
            "AND name_category = :categoryName " +
            "GROUP BY b.id " +
            "ORDER BY date_start ASC")
    fun getFullBudgetDetailsDateAsc(start: Long, finish: Long, categoryName: String): Flow<MutableList<BudgetFullInfo>>
    @Query("SELECT b.id as id, b.date_start, b.date_finish, COALESCE(SUM(e.expense / c.current_exchange_rate_to_base), 0) as spend_cash, b.total_cash as budget, ec.name_category, ec.image AS image " +
            "FROM budgets b " +
            "JOIN expense_categories ec ON ec.id = b.expense_category_id " +
            "LEFT JOIN expenses e ON e.date BETWEEN b.date_start AND b.date_finish AND e.expense_category_id = b.expense_category_id " +
            "LEFT JOIN accounts a ON e.account_id = a.id AND a.is_visible = 1 " +
            "LEFT JOIN currencies c ON a.currency_id = c.id " +
//            "WHERE ((b.date_start >= :start AND b.date_finish <= :finish) OR (b.date_start < :start AND b.date_finish > :finish) OR (b.date_start BETWEEN :start AND :finish) OR (b.date_finish BETWEEN :start AND :finish)) " +
            "WHERE ((b.date_start BETWEEN :start AND :finish) OR (b.date_finish BETWEEN :start AND :finish) OR (:start BETWEEN b.date_start AND b.date_finish) OR (:finish BETWEEN b.date_start AND b.date_finish)) " +
            "AND name_category = :categoryName " +
            "GROUP BY b.id " +
            "ORDER BY date_finish DESC")
    fun getFullBudgetDetailsDateDesc(start: Long, finish: Long, categoryName: String): Flow<MutableList<BudgetFullInfo>>





    @Query("SELECT min(bt.date_start) as date_start, max(bt.date_finish) as date_finish, sum(bt.budget) as budget, sum(bt.expense) as spend_cash, ec.name_category as name_category, ec.image as image " +
            "FROM expense_categories ec, " +
            "(SELECT b.id AS budget_id, b.date_start, b.date_finish, SUM(e.expense / c.current_exchange_rate_to_base) AS expense, b.total_cash AS budget, b.expense_category_id AS expense_category_id " +
            "FROM budgets b " +
            "LEFT JOIN expenses e ON e.expense_category_id = b.expense_category_id " +
            "JOIN accounts a ON e.account_id = a.id AND a.is_visible = 1 " +
            "JOIN currencies c ON a.currency_id = c.id " +
            "WHERE e.date BETWEEN b.date_start AND b.date_finish " +
//            "AND ((b.date_start >= :startDate AND b.date_finish <= :finishDate) OR (b.date_start < :startDate AND b.date_finish > :finishDate) OR (b.date_start BETWEEN :startDate AND :finishDate) OR (b.date_finish BETWEEN :startDate AND :finishDate)) " +
            "AND ((b.date_start BETWEEN :start AND :finish) OR (b.date_finish BETWEEN :start AND :finish) OR (:start BETWEEN b.date_start AND b.date_finish) OR (:finish BETWEEN b.date_start AND b.date_finish)) " +
            "GROUP BY b.id, b.date_start, b.date_finish " +
            "ORDER BY b.id) bt " +
            "WHERE ec.id = bt.expense_category_id " +
            "GROUP BY bt.expense_category_id")
    fun getTotalByCategories(start: Long, finish: Long): Flow<MutableList<BudgetShortInfo>>

    @Update
    suspend fun update(budgetEntity: BudgetEntity)

    @Query("SELECT count(*) FROM budgets WHERE id = :id")
    fun getCountById(id: Int): Int

    @Query("SELECT b.id as id, b.date_start as date_start, b.date_finish as date_finish, b.total_cash as budget, 0 as spend_cash, ec.name_category as name_category, ec.image as image " +
            "FROM budgets b, expense_categories ec " +
            "WHERE b.expense_category_id = ec.id AND ec.name_category = :nameCategory " +
//            "AND ((b.date_start >= :startDate AND b.date_finish <= :finishDate) OR (b.date_start < :startDate AND b.date_finish > :finishDate) OR (b.date_start BETWEEN :startDate AND :finishDate) OR (b.date_finish BETWEEN :startDate AND :finishDate))"
            "AND ((b.date_start BETWEEN :start AND :finish) OR (b.date_finish BETWEEN :start AND :finish) OR (:start BETWEEN b.date_start AND b.date_finish) OR (:finish BETWEEN b.date_start AND b.date_finish)) "
    )//"AND ((b.date_start BETWEEN :startDate AND :finishDate) OR (b.date_finish BETWEEN :startDate AND :finishDate))")
    fun getConflictingBudgets(nameCategory: String, start: Long, finish: Long): Flow<MutableList<BudgetFullInfo>>

    @Query("DELETE FROM budgets WHERE date_start = :dateStart AND date_finish = :dateFinish AND expense_category_id = :expenseCategoryId")
    fun delete(dateStart: Long, dateFinish: Long, expenseCategoryId: Int)
}