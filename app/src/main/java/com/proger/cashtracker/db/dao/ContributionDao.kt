package com.proger.cashtracker.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.proger.cashtracker.db.query.ContributionFullDetails
import com.proger.cashtracker.db.table.ContributionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContributionDao : BaseDao<ContributionEntity>{
//    @Query("SELECT sum(contribution) as returned FROM contributions WHERE debt_id = :debtId ORDER BY date ASC")
//    fun getReturned(debtId: Int): Double

    @Query("SELECT * FROM contributions WHERE debt_id = :debtId")
    fun getByDebtId(debtId: Int): Flow<MutableList<ContributionEntity>>

    @Query("SELECT co.id, debt_id, co.date, co.contribution, " +
            "a.name as account_name, a.balance, a.image, " +
            "cu.currency_code, cu.currency_name, cu.current_exchange_rate_to_base as rate_to_base " +
            "FROM contributions co " +
            "JOIN accounts a ON co.account_id = a.id " +
            "JOIN currencies cu ON a.currency_id = cu.id " +
            "WHERE co.id = :contributionId")
    fun get(contributionId: Int): Flow<ContributionFullDetails>

    @Query("SELECT co.id, debt_id, co.date, co.contribution, " +
            "a.name as account_name, a.balance, a.image, " +
            "cu.currency_code, cu.currency_name, cu.current_exchange_rate_to_base as rate_to_base " +
            "FROM contributions co " +
            "JOIN accounts a ON co.account_id = a.id " +
            "JOIN currencies cu ON a.currency_id = cu.id " +
            "WHERE debt_id = :debtId")
    fun getFullByDebtId(debtId: Int): Flow<MutableList<ContributionFullDetails>>

    @Query("SELECT COALESCE(SUM(co.contribution / cu.current_exchange_rate_to_base), 0) " + // as returned, d.debt / cu.current_exchange_rate_to_base as debt " +
            "FROM contributions co " +
            "JOIN accounts a ON co.account_id = a.id " +
            "JOIN currencies cu ON a.currency_id = cu.id " +
            "JOIN debts d ON co.debt_id = d.id " +
            "WHERE debt_id = :debtId")
    fun getReturnedValueByDebtId(debtId: Int): Double

    @Query("SELECT COUNT(*) FROM contributions WHERE id = :contributionId")
    fun getCountById(contributionId: Int): Int

    @Query("DELETE FROM contributions WHERE debt_id = :debtId")
    suspend fun deleteByDebtId(debtId: Int)
    @Query("DELETE FROM contributions WHERE id = :contributionId")
    suspend fun delete(contributionId: Int)

    @Query("UPDATE contributions SET contribution = :value WHERE id = :idContribution")
    suspend fun update(idContribution: Int, value: Double)
    @Update
    suspend fun update(contribution: ContributionEntity)


//    @Query("UPDATE contributions SET contribution = :value, date = :date WHERE id = :idContribution")
//    suspend fun update(idContribution: Int, value: Double, date: Long)
}