package com.proger.cashtracker.db.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "income_categories", indices = [Index(value = ["name_category"], unique = true)])
data class IncomeCategoryEntity (
    @PrimaryKey(autoGenerate = true) var id: Int? = null,
    @ColumnInfo(name = "name_category") var nameCategory: String,
    @ColumnInfo(name = "image") var image: String
)