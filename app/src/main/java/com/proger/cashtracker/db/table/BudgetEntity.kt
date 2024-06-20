package com.proger.cashtracker.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "budgets",
        foreignKeys = [
            ForeignKey(entity = ExpenseCategoryEntity::class,
                       parentColumns = ["id"],
                       childColumns = ["expense_category_id"],
                       onDelete = ForeignKey.CASCADE)])
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "date_start") var dateStart: Long,
    @ColumnInfo(name = "date_finish") var dateFinish: Long,
    @ColumnInfo(name = "total_cash") var totalCash: Double,

    @ColumnInfo(name = "expense_category_id") var expenseCategoryId: Int,
)