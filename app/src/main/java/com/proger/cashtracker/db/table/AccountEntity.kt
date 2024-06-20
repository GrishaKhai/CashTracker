package com.proger.cashtracker.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "accounts", indices = [Index(value = ["name"], unique = true)],
        foreignKeys = [
            ForeignKey(entity = CurrencyEntity::class,
                       parentColumns = ["id"],
                       childColumns = ["currency_id"],
                       onDelete = ForeignKey.CASCADE)
        ])
data class AccountEntity (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "balance") var balance: Double,
    @ColumnInfo(name = "image") var image: String,
    @ColumnInfo(name = "is_visible") var isVisible: Boolean = true,

    @ColumnInfo(name = "currency_id") var currencyId: Int
)