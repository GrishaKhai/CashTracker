package com.proger.cashtracker.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "incomes",
        foreignKeys = [
            ForeignKey(entity = IncomeCategoryEntity::class,
                parentColumns = ["id"],
                childColumns = ["income_category_id"],
                onDelete = ForeignKey.CASCADE),
            ForeignKey(entity = AccountEntity::class,
                parentColumns = ["id"],
                childColumns = ["account_id"],
                onDelete = ForeignKey.CASCADE)
    ])
data class IncomeEntity (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "date") var date: Long,
    @ColumnInfo(name = "comment") var comment: String,
    @ColumnInfo(name = "income") var income: Double,

    @ColumnInfo(name = "income_category_id") var incomeCategoryId: Int,
    @ColumnInfo(name = "account_id") var accountId: Int
)