package com.proger.cashtracker.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.google.android.material.circularreveal.CircularRevealHelper.Strategy
import com.proger.cashtracker.db.table.CurrencyEntity
import com.proger.cashtracker.db.table.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao : BaseDao<CurrencyEntity> {
    @Query("SELECT * FROM currencies ORDER BY currency_code ASC")
    fun getCurrencies() : Flow<MutableList<CurrencyEntity>>
    /*@Query("SELECT * FROM currencies ORDER BY currency_code ASC")
    fun getCurrencies(): PagingSource<Int, CurrencyEntity>*/

//    @Update
//    fun update(currencies: CurrencyEntity)

    @Query("UPDATE currencies SET current_exchange_rate_to_base = :rate WHERE currency_code = :code")
    fun update(rate: Double, code: String)


    @Query("UPDATE currencies SET current_exchange_rate_to_base = current_exchange_rate_to_base / :rate")
    fun updateAllCurrencyRate(rate: Double)

    @Query("SELECT * FROM currencies WHERE currency_code = :currencyCode")
    fun get(currencyCode: String): Flow<CurrencyEntity>
}