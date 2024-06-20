package com.proger.cashtracker.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.proger.cashtracker.db.table.IncomeCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeCategoryDao : BaseDao<IncomeCategoryEntity> {
    @Update
    suspend fun update(incomeCategory: IncomeCategoryEntity)

    @Query("SELECT c.* FROM income_categories c LEFT JOIN incomes i ON c.id = i.income_category_id " +
            "GROUP BY c.name_category ORDER BY COUNT(i.income_category_id) DESC")
    fun getAll() : Flow<MutableList<IncomeCategoryEntity>>

    @Query("SELECT id FROM income_categories WHERE name_category = :nameCategory")
    fun getId(nameCategory: String) : Int?

    @Query("SELECT * FROM income_categories WHERE name_category = :nameCategory")
    fun get(nameCategory: String) : IncomeCategoryEntity?


    @Query("SELECT COUNT(*) FROM income_categories WHERE name_category = :nameCategory")
    fun getCountByName(nameCategory: String): Int

    @Query("SELECT COUNT(*) FROM income_categories WHERE id = :oldIncomeCategoryId")
    fun getCountById(oldIncomeCategoryId: Int): Int
}