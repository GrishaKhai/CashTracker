package com.proger.cashtracker.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "debtors", indices = [Index(value = ["name_debtor"], unique = true)])
data class DebtorEntity (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "name_debtor") var nameDebtor: String
)