package com.proger.cashtracker.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "contributions",
    foreignKeys = [
        ForeignKey(entity = DebtEntity::class,
            parentColumns = ["id"],
            childColumns = ["debt_id"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE)
    ])
data class ContributionEntity (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "date") var date: Long,
    @ColumnInfo(name = "contribution") var contribution: Double,

    @ColumnInfo(name = "debt_id") var debtId: Int,
    @ColumnInfo(name = "account_id") var accountId: Int
)