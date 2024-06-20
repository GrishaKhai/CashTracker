package com.proger.cashtracker.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "transfers",
    foreignKeys = [
        ForeignKey(entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["from_account_id"],
            onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["to_account_id"],
            onDelete = ForeignKey.CASCADE)
    ])
data class TransferEntity (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "date") var date: Long,
    @ColumnInfo(name = "comment") var comment: String,
    @ColumnInfo(name = "transfer") var transfer: Double,

    @ColumnInfo(name = "from_account_id") var fromAccountId: Int,
    @ColumnInfo(name = "to_account_id") var toAccountId: Int
)