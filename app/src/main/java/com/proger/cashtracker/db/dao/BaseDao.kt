package com.proger.cashtracker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert

@Dao
interface BaseDao<T> {
    @Insert
    suspend fun insert(obj: T) : Long

    @Insert
    suspend fun insert(vararg obj: T)

    @Delete
    suspend fun delete(obj: T)

    @Delete
    suspend fun delete(vararg obj: T)
}

    //    suspend fun insert(obj: ArrayList<T>)
//(onConflict = OnConflictStrategy.IGNORE)