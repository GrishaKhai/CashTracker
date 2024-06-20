package com.proger.cashtracker.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.proger.cashtracker.db.table.DebtorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtorDao : BaseDao<DebtorEntity> {
    @Query("SELECT * FROM debtors WHERE id > 1")
    fun getAll() : Flow<MutableList<DebtorEntity>>

    @Query("SELECT * FROM debtors WHERE name_debtor = :nameDebtor")
    fun get(nameDebtor: String): Flow<DebtorEntity>

    @Query("SELECT count(*) FROM debtors WHERE name_debtor = :name")
    fun getCountByName(name: String): Int
}
//    @Query("SELECT * FROM debtors WHERE id = 1")
//    fun getFirst() : Flow<DebtorEntity>
