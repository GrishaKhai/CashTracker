package com.proger.cashtracker.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.proger.cashtracker.db.query.FullDebtDetails
import com.proger.cashtracker.db.query.ShortDebtDetails
import com.proger.cashtracker.db.table.DebtEntity
import com.proger.cashtracker.models.Debt
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao : BaseDao<DebtEntity> {
    //                                          СОРТИРОВКА ФИЛЬТРАЦИЯ
    @Query("SELECT d.id, d.start_date, d.finish_date, (d.debt / cu.current_exchange_rate_to_base) as debt, d.comment, d.is_returned, d.is_debtor, dd.name_debtor as creditor_or_debtor, a.name as name_account, a.balance as balance_account,  " +
            "a.image, cu.currency_code, cu.currency_name, cu.current_exchange_rate_to_base, COALESCE(SUM(co.contribution / cur.current_exchange_rate_to_base), 0) as returned " +
            "FROM debts d " +
            "JOIN accounts a ON d.account_id = a.id AND a.name like :accountName " +
            "JOIN currencies cu ON a.currency_id = cu.id " +
            "JOIN debtors dd ON d.creditor_or_debtor_id = dd.id AND dd.name_debtor like :nameDebtorOrCreditor " +
            "LEFT JOIN contributions co ON co.debt_id = d.id " +
            "LEFT JOIN accounts ar ON co.account_id = ar.id " +
            "LEFT JOIN currencies cur ON ar.currency_id = cur.id " +
            "WHERE ((start_date BETWEEN :start AND :finish) OR (finish_date BETWEEN :start AND :finish) OR (:start BETWEEN start_date AND finish_date) OR (:finish BETWEEN start_date AND finish_date))" +
            "AND is_debtor = :isDebtor " +
            "GROUP BY d.id " +
            "ORDER BY (returned / debt) ASC, debt ASC, returned ASC")
    fun getFullDebtDetailsValueAsc(start: Long, finish: Long, isDebtor: Int, nameDebtorOrCreditor: String, accountName: String): Flow<MutableList<FullDebtDetails>>
    @Query("SELECT d.id, d.start_date, d.finish_date, (d.debt / cu.current_exchange_rate_to_base) as debt, d.comment, d.is_returned, d.is_debtor, dd.name_debtor as creditor_or_debtor, a.name as name_account, a.balance as balance_account, " +
            "a.image, cu.currency_code, cu.currency_name, cu.current_exchange_rate_to_base, COALESCE(SUM(co.contribution / cur.current_exchange_rate_to_base), 0) as returned " +
            "FROM debts d " +
            "JOIN accounts a ON d.account_id = a.id AND a.name like :accountName " +
            "JOIN currencies cu ON a.currency_id = cu.id " +
            "JOIN debtors dd ON d.creditor_or_debtor_id = dd.id AND dd.name_debtor like :nameDebtorOrCreditor " +
            "LEFT JOIN contributions co ON co.debt_id = d.id " +
            "LEFT JOIN accounts ar ON co.account_id = ar.id " +
            "LEFT JOIN currencies cur ON ar.currency_id = cur.id " +
            "WHERE ((start_date BETWEEN :start AND :finish) OR (finish_date BETWEEN :start AND :finish) OR (:start BETWEEN start_date AND finish_date) OR (:finish BETWEEN start_date AND finish_date)) " +
            "AND is_debtor = :isDebtor " +
            "GROUP BY d.id " +
            "ORDER BY (returned / debt) DESC, debt DESC, returned DESC")
    fun getFullDebtDetailsValueDesc(start: Long, finish: Long, isDebtor: Int, nameDebtorOrCreditor: String, accountName: String): Flow<MutableList<FullDebtDetails>>
    @Query("SELECT d.id, d.start_date, d.finish_date, (d.debt / cu.current_exchange_rate_to_base) as debt, d.comment, d.is_returned, d.is_debtor, dd.name_debtor as creditor_or_debtor, a.name as name_account, a.balance as balance_account, " +
            "a.image, cu.currency_code, cu.currency_name, cu.current_exchange_rate_to_base, COALESCE(SUM(co.contribution / cur.current_exchange_rate_to_base), 0) as returned " +
            "FROM debts d " +
            "JOIN accounts a ON d.account_id = a.id AND a.name like :accountName " +
            "JOIN currencies cu ON a.currency_id = cu.id " +
            "JOIN debtors dd ON d.creditor_or_debtor_id = dd.id AND dd.name_debtor like :nameDebtorOrCreditor " +
            "LEFT JOIN contributions co ON co.debt_id = d.id " +
            "LEFT JOIN accounts ar ON co.account_id = ar.id " +
            "LEFT JOIN currencies cur ON ar.currency_id = cur.id " +
            "WHERE ((start_date BETWEEN :start AND :finish) OR (finish_date BETWEEN :start AND :finish) OR (:start BETWEEN start_date AND finish_date) OR (:finish BETWEEN start_date AND finish_date)) " +
            "AND is_debtor = :isDebtor " +
            "GROUP BY d.id " +
            "ORDER BY start_date ASC")
    fun getFullDebtDetailsDateAsc(start: Long, finish: Long, isDebtor: Int, nameDebtorOrCreditor: String, accountName: String): Flow<MutableList<FullDebtDetails>>
    @Query("SELECT d.id, d.start_date, d.finish_date, (d.debt / cu.current_exchange_rate_to_base) as debt, d.comment, d.is_returned, d.is_debtor, dd.name_debtor as creditor_or_debtor, a.name as name_account, a.balance as balance_account, " +
            "a.image, cu.currency_code, cu.currency_name, cu.current_exchange_rate_to_base, COALESCE(SUM(co.contribution / cur.current_exchange_rate_to_base), 0) as returned " +
            "FROM debts d " +
            "JOIN accounts a ON d.account_id = a.id AND a.name like :accountName " +
            "JOIN currencies cu ON a.currency_id = cu.id " +
            "JOIN debtors dd ON d.creditor_or_debtor_id = dd.id AND dd.name_debtor like :nameDebtorOrCreditor " +
            "LEFT JOIN contributions co ON co.debt_id = d.id " +
            "LEFT JOIN accounts ar ON co.account_id = ar.id " +
            "LEFT JOIN currencies cur ON ar.currency_id = cur.id " +
            "WHERE ((start_date BETWEEN :start AND :finish) OR (finish_date BETWEEN :start AND :finish) OR (:start BETWEEN start_date AND finish_date) OR (:finish BETWEEN start_date AND finish_date)) " +
            "AND is_debtor = :isDebtor " +
            "GROUP BY d.id " +
            "ORDER BY finish_date DESC")
    fun getFullDebtDetailsDateDesc(start: Long, finish: Long, isDebtor: Int, nameDebtorOrCreditor: String, accountName: String): Flow<MutableList<FullDebtDetails>>
    @Query("SELECT d.id, d.start_date, d.finish_date, (d.debt / cu.current_exchange_rate_to_base) as debt, d.comment, d.is_returned, d.is_debtor, dd.name_debtor as creditor_or_debtor, a.name as name_account, a.balance as balance_account, " +
            "a.image, cu.currency_code, cu.currency_name, cu.current_exchange_rate_to_base, COALESCE(SUM(co.contribution / cur.current_exchange_rate_to_base), 0) as returned " +
            "FROM debts d " +
            "JOIN accounts a ON d.account_id = a.id AND a.name like :accountName " +
            "JOIN currencies cu ON a.currency_id = cu.id " +
            "JOIN debtors dd ON d.creditor_or_debtor_id = dd.id AND dd.name_debtor like :nameDebtorOrCreditor " +
            "LEFT JOIN contributions co ON co.debt_id = d.id " +
            "LEFT JOIN accounts ar ON co.account_id = ar.id " +
            "LEFT JOIN currencies cur ON ar.currency_id = cur.id " +
            "WHERE ((start_date BETWEEN :start AND :finish) OR (finish_date BETWEEN :start AND :finish) OR (:start BETWEEN start_date AND finish_date) OR (:finish BETWEEN start_date AND finish_date)) " +
            "AND comment like :comment AND is_debtor = :isDebtor " +
            "GROUP BY d.id " +
            "ORDER BY (returned / debt) ASC, debt ASC, returned ASC")
    fun getFullDebtDetailsByComment(start: Long, finish: Long, isDebtor: Int, nameDebtorOrCreditor: String, accountName: String, comment: String): Flow<MutableList<FullDebtDetails>>




    @Query("SELECT d.id, d.start_date, d.finish_date, (d.debt / cu.current_exchange_rate_to_base) as debt, d.comment, d.is_returned, d.is_debtor, dd.name_debtor as creditor_or_debtor, a.name as name_account, a.balance as balance_account, " +
            "a.image, cu.currency_code, cu.currency_name, cu.current_exchange_rate_to_base, COALESCE(SUM(co.contribution / cur.current_exchange_rate_to_base), 0) as returned " +
            "FROM debts d " +
            "JOIN accounts a ON d.account_id = a.id AND a.name like :accountName " +
            "JOIN currencies cu ON a.currency_id = cu.id " +
            "JOIN debtors dd ON d.creditor_or_debtor_id = dd.id " +
            "LEFT JOIN contributions co ON co.debt_id = d.id " +
            "LEFT JOIN accounts ar ON co.account_id = ar.id " +
            "LEFT JOIN currencies cur ON ar.currency_id = cur.id " +
            "WHERE d.is_returned = :isReturned " +
            "GROUP BY d.id " +
            "ORDER BY start_date ASC")
    fun getFullDetails(isReturned: Int = 0, accountName: String = "%"): Flow<MutableList<FullDebtDetails>>


    @Query("SELECT is_debtor, creditor_or_debtor, sum(debt) as debt_sum, sum(returned) as returned_sum " +
            "FROM ( " +
            "SELECT d.is_debtor, dd.name_debtor as creditor_or_debtor, (d.debt / cu.current_exchange_rate_to_base) as debt, COALESCE(SUM(co.contribution / cur.current_exchange_rate_to_base), 0) as returned " +
            "FROM debts d " +
            "JOIN accounts a ON d.account_id = a.id AND a.name like :accountName " +
            "JOIN currencies cu ON a.currency_id = cu.id " +
            "JOIN debtors dd ON d.creditor_or_debtor_id = dd.id " +
            "LEFT JOIN contributions co ON co.debt_id = d.id \n" +
            "LEFT JOIN accounts ar ON co.account_id = ar.id " +
            "LEFT JOIN currencies cur ON ar.currency_id = cur.id " +
            "WHERE ((start_date BETWEEN :start AND :finish) OR (finish_date BETWEEN :start AND :finish) OR (:start BETWEEN start_date AND finish_date) OR (:finish BETWEEN start_date AND finish_date))" +
            "GROUP BY d.id) AS debt_group " +
            "GROUP BY is_debtor, creditor_or_debtor " +
            "ORDER BY is_debtor ASC, creditor_or_debtor ASC")
    fun getShortDetails(start: Long = 0, finish: Long = 3000000000000, accountName: String): Flow<MutableList<ShortDebtDetails>>

    @Query("SELECT count(*) FROM debts WHERE id = :debtId")
    fun getCountById(debtId: Int): Int

    @Query("SELECT * FROM debts WHERE id = :debtId")
    fun getEntity(debtId: Int): DebtEntity
    @Query("SELECT d.id, d.start_date, d.finish_date, (d.debt / cu.current_exchange_rate_to_base) as debt, d.comment, d.is_returned, d.is_debtor, dd.name_debtor as creditor_or_debtor, a.name as name_account, a.balance as balance_account, " +
            "a.image, cu.currency_code, cu.currency_name, cu.current_exchange_rate_to_base, COALESCE(SUM(co.contribution / cur.current_exchange_rate_to_base), 0) as returned " +
            "FROM debts d " +
            "JOIN accounts a ON d.account_id = a.id " +
            "JOIN currencies cu ON a.currency_id = cu.id " +
            "JOIN debtors dd ON d.creditor_or_debtor_id = dd.id " +
            "LEFT JOIN contributions co ON co.debt_id = d.id " +
            "LEFT JOIN accounts ar ON co.account_id = ar.id " +
            "LEFT JOIN currencies cur ON ar.currency_id = cur.id " +
            "WHERE d.id = :debtId")
    fun get(debtId: Int): Flow<FullDebtDetails>

    @Update
    suspend fun update(debt: DebtEntity)

    @Query("UPDATE debts SET is_returned = 1 WHERE id = :debtId")
    suspend fun closeDebt(debtId: Int)
    @Query("UPDATE debts SET is_returned = 0 WHERE id = :debtId")
    suspend fun openDebt(debtId: Int)
}