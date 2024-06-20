package com.proger.cashtracker.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "expenses",
    foreignKeys = [
        ForeignKey(entity = ExpenseCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["expense_category_id"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE)
    ])
data class ExpenseEntity (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "date") var date: Long,
    @ColumnInfo(name = "comment") var comment: String,
    @ColumnInfo(name = "expense") var expense: Double,

    @ColumnInfo(name = "expense_category_id") var expenseCategoryId: Int,
    @ColumnInfo(name = "account_id") var accountId: Int
)