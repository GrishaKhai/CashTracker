package com.proger.cashtracker.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "debts",
    foreignKeys = [
        ForeignKey(entity = DebtorEntity::class,
            parentColumns = ["id"],
            childColumns = ["creditor_or_debtor_id"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["account_id"],
            onDelete = ForeignKey.CASCADE)
    ])
data class DebtEntity (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "debt") var debt: Double,
    @ColumnInfo(name = "start_date") var startDate: Long,
    @ColumnInfo(name = "finish_date") var finishDate: Long,
    @ColumnInfo(name = "comment") var comment: String,
    @ColumnInfo(name = "is_returned") var isReturned: Boolean,
    @ColumnInfo(name = "is_debtor") var isDebtor: Boolean,

    @ColumnInfo(name = "creditor_or_debtor_id") var creditorOrDebtorId: Int,//той хто дає грощі у займи чи бере гроші у займи
    @ColumnInfo(name = "account_id") var accountId: Int
)