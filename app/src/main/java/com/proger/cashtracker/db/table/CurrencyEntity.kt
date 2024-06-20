package com.proger.cashtracker.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "currencies", indices = [Index(value = ["currency_code"], unique = true)])
data class CurrencyEntity (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "currency_code") var currencyCode: String,
    @ColumnInfo(name = "currency_name") var currencyName: String,
    @ColumnInfo(name = "current_exchange_rate_to_base") var rateToBase: Double
)