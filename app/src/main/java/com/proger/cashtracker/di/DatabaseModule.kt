package com.proger.cashtracker.di

import android.content.Context
import androidx.room.Room
import com.proger.cashtracker.db.AppDatabase
import com.proger.cashtracker.db.dao.AccountDao
import com.proger.cashtracker.db.dao.BudgetDao
import com.proger.cashtracker.db.dao.ContributionDao
import com.proger.cashtracker.db.dao.CurrencyDao
import com.proger.cashtracker.db.dao.DebtDao
import com.proger.cashtracker.db.dao.DebtorDao
import com.proger.cashtracker.db.dao.ExpenseCategoryDao
import com.proger.cashtracker.db.dao.ExpenseDao
import com.proger.cashtracker.db.dao.IncomeCategoryDao
import com.proger.cashtracker.db.dao.IncomeDao
import com.proger.cashtracker.db.dao.TransferDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cash_tracker.db"
        ).createFromAsset("db/cash_tracker.db").build()
        return db
    }

    @Provides
    fun provideExpenseDao(db: AppDatabase): ExpenseDao {
        return db.getExpenseDao()
    }

    @Provides
    fun provideExpenseCategoryDao(db: AppDatabase): ExpenseCategoryDao {
        return db.getExpenseCategoryDao()
    }

    @Provides
    fun provideCurrencyDao(db: AppDatabase): CurrencyDao {
        return db.getCurrencyDao()
    }

    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao {
        return db.getAccountDao()
    }

    @Provides
    fun provideIncomeDao(db: AppDatabase): IncomeDao {
        return db.getIncomeDao()
    }

    @Provides
    fun provideIncomeCategoryDao(db: AppDatabase): IncomeCategoryDao {
        return db.getIncomeCategoryDao()
    }

    @Provides
    fun provideBudgetDao(db: AppDatabase): BudgetDao {
        return db.getBudgetDao()
    }

    @Provides
    fun provideDebtDao(db: AppDatabase): DebtDao {
        return db.getDebtDao()
    }

    @Provides
    fun provideDebtorDao(db: AppDatabase): DebtorDao {
        return db.getDebtorDao()
    }

    @Provides
    fun provideContributionDao(db: AppDatabase): ContributionDao {
        return db.getContributionDao()
    }

    @Provides
    fun provideTransferDao(db: AppDatabase): TransferDao {
        return db.getTransferDao()
    }
}