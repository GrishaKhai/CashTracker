package com.proger.cashtracker.db.dao

import androidx.room.Dao
import com.proger.cashtracker.db.table.TransferEntity

@Dao
interface TransferDao : BaseDao<TransferEntity> {
}