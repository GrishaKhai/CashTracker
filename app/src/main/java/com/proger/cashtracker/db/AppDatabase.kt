package com.proger.cashtracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.proger.cashtracker.db.dao.*
import com.proger.cashtracker.db.table.*

@Database(
    entities = [
        AccountEntity::class,
        BudgetEntity::class,
        CurrencyEntity::class,
        DebtEntity::class,
        DebtorEntity::class,
        ExpenseEntity::class,
        ExpenseCategoryEntity::class,
        IncomeEntity::class,
        IncomeCategoryEntity::class,
        TransferEntity::class,
        ContributionEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAccountDao(): AccountDao
    abstract fun getBudgetDao(): BudgetDao
    abstract fun getCurrencyDao(): CurrencyDao
    abstract fun getDebtDao(): DebtDao
    abstract fun getDebtorDao(): DebtorDao
    abstract fun getExpenseDao(): ExpenseDao
    abstract fun getExpenseCategoryDao(): ExpenseCategoryDao
    abstract fun getIncomeDao(): IncomeDao
    abstract fun getIncomeCategoryDao(): IncomeCategoryDao
    abstract fun getTransferDao(): TransferDao
    abstract fun getContributionDao(): ContributionDao
}