package com.proger.cashtracker.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.proger.cashtracker.db.table.ExpenseCategoryEntity
import com.proger.cashtracker.db.table.IncomeCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseCategoryDao : BaseDao<ExpenseCategoryEntity> {
    //    fun getExpenseCategories() : Flow<MutableList<ExpenseCategory>>
//    suspend fun getExpenseCategories() : MutableList<ExpenseCategory>
    @Update
    suspend fun update(expenseCategory: ExpenseCategoryEntity)

    @Query("SELECT c.* FROM expense_categories c LEFT JOIN expenses e ON c.id = e.expense_category_id " +
            "GROUP BY c.name_category ORDER BY COUNT(e.expense_category_id) DESC")
    fun getAll(): Flow<MutableList<ExpenseCategoryEntity>>

    @Query("SELECT id FROM expense_categories WHERE name_category = :nameCategory")
    fun getId(nameCategory: String): Int?

    @Query("SELECT * FROM expense_categories WHERE name_category = :nameCategory")
    fun get(nameCategory: String) : ExpenseCategoryEntity?

    @Query("SELECT COUNT(*) FROM expense_categories WHERE name_category = :nameCategory")
    fun getCountByName(nameCategory: String): Int

    @Query("SELECT COUNT(*) FROM expense_categories WHERE id = :oldExpenseCategoryId")
    fun getCountById(oldExpenseCategoryId: Int): Int
}