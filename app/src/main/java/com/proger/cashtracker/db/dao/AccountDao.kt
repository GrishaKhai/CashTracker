package com.proger.cashtracker.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.proger.cashtracker.db.query.AccountWithCurrency
import com.proger.cashtracker.db.table.AccountEntity
import com.proger.cashtracker.db.table.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao : BaseDao<AccountEntity>{

    @Query("SELECT * FROM accounts WHERE is_visible = 1")
    fun getAll() : Flow<MutableList<AccountEntity>>

    @Query("SELECT a.id AS account_id, a.name AS name, a.balance AS balance, a.image AS image, " +
            "c.currency_code AS currency_code, c.currency_name AS currency_name, c.current_exchange_rate_to_base AS rate_to_base " +
            "FROM accounts AS a, currencies AS c WHERE a.currency_id = c.id AND a.is_visible = 1")
    fun getAccountsWithCurrency() : Flow<MutableList<AccountWithCurrency>>

    @Query("SELECT id FROM accounts WHERE name = :nameAccount AND is_visible = 1")
    fun getId(nameAccount: String) : Int?

    @Query("SELECT * FROM accounts WHERE name = :nameAccount AND is_visible = 1")
    fun get(nameAccount: String) : AccountEntity
    @Query("SELECT * FROM accounts WHERE id = :id AND is_visible = 1")
    fun get(id: Int) : AccountEntity

    @Query("UPDATE accounts SET balance = balance + :addedValue WHERE id = :accountId AND is_visible = 1")
    suspend fun updateAccountBalance(accountId: Int, addedValue: Double)

    @Query("SELECT COUNT(*) FROM accounts WHERE name = :name AND is_visible = 1 ")
    fun getCountByName(name: String): Int

    @Query("UPDATE accounts SET is_visible = :isVisible WHERE name = :accountName")
    suspend fun updateActiveState(accountName: String, isVisible: Int)

    @Update
    suspend fun update(account: AccountEntity)
}