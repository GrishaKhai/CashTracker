package com.proger.cashtracker.db

import com.proger.cashtracker.db.dao.BudgetDao
import com.proger.cashtracker.db.dao.ExpenseCategoryDao
import com.proger.cashtracker.db.query.fromBudgetFullInfoToBudget
import com.proger.cashtracker.db.table.BudgetEntity
import com.proger.cashtracker.utils.DateHelper
import com.proger.cashtracker.models.Budget
import com.proger.cashtracker.models.Category
import com.proger.cashtracker.models.ConflictBudgetException
import com.proger.cashtracker.models.EditedNoteNotExistInDbException
import com.proger.cashtracker.models.NoteNotExistInDbException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository
@Inject constructor(
    private var budgetDao: BudgetDao,
    private var expenseCategoryDao: ExpenseCategoryDao,
) {

//    fun getBudgets(startDate: Long, finishDate: Long): Flow<MutableList<Budget>> {
//        return budgetDao.getByDateInterval(startDate, finishDate).map { list ->
//            list.map {
//                Budget(
//                    dateStart = it.date_start,
//                    dateFinish = it.date_finish,
//                    totalCash = it.total_cash,
//                    expenseCategory = Category(
//                        nameCategory = it.name_category,
//                        image = it.image
//                    )
//                )
//            }.toMutableList()
//        }
//    }

    fun getTotalBudgetsByCategories(startDate: Long, finishDate: Long): Flow<MutableList<Budget>> {
        return budgetDao.getTotalByCategories(startDate, finishDate).map { list ->
            list.map {
                Budget(
                    dateStart = it.date_start,
                    dateFinish = it.date_finish,
                    budget = it.budget,
                    spendCash = it.spend_cash,
                    expenseCategory = Category(
                        nameCategory = it.name_category,
                        image = it.image
                    )
                )
            }.toMutableList()
        }
    }


    suspend fun insertBudget(budget: Budget) {
        withContext(Dispatchers.IO) {
            budget.validate()//поверхностная проверка полей
            val conflict = budgetDao.getConflictingBudgets(
                nameCategory = budget.expenseCategory!!.nameCategory,
                start = budget.dateStart!!,
                finish = budget.dateFinish!!
            ).first()
            if(conflict.size > 0) {
                throw ConflictBudgetException("(${conflict.size}) - " +
                        DateHelper.makeRangeFormat(Date(conflict[0].date_start), Date(conflict[0].date_finish)))
            }

            val id = expenseCategoryDao.getId(budget.expenseCategory!!.nameCategory)
                ?: throw NoteNotExistInDbException(budget.expenseCategory!!.nameCategory)

            budgetDao.insert(
                BudgetEntity(
                    id = null,
                    dateStart = budget.dateStart!!,
                    dateFinish = budget.dateFinish!!,
                    totalCash = budget.budget!!,
                    expenseCategoryId = id,
                )
            )
        }
    }
    suspend fun updateBudget(oldBudgetId: Int, budget: Budget) {
        withContext(Dispatchers.IO) {
            budget.validate()//поверхностная проверка полей
            val conflict = budgetDao.getConflictingBudgets(
                    nameCategory = budget.expenseCategory!!.nameCategory,
                    start = budget.dateStart!!,
                    finish = budget.dateFinish!!).first()
            if(budgetDao.getCountById(oldBudgetId) == 0) throw EditedNoteNotExistInDbException()
            if(conflict.size > 1) {
                throw ConflictBudgetException("(${conflict.size}) - " +
                        DateHelper.makeRangeFormat(Date(conflict[0].date_start), Date(conflict[0].date_finish)))
            }
            if(conflict.size == 1 && conflict[0].id.toInt() != oldBudgetId) {
                throw ConflictBudgetException("(${conflict.size}) - " +
                        DateHelper.makeRangeFormat(Date(conflict[0].date_start), Date(conflict[0].date_finish)))
            }
            val id = expenseCategoryDao.getId(budget.expenseCategory!!.nameCategory)
                ?: throw NoteNotExistInDbException(budget.expenseCategory!!.nameCategory)

            budgetDao.update(
                BudgetEntity(
                    id = oldBudgetId,
                    dateStart = budget.dateStart!!,
                    dateFinish = budget.dateFinish!!,
                    totalCash = budget.budget!!,
                    expenseCategoryId = id,
                )
            )
        }
    }
    suspend fun deleteBudget(budget: Budget) {
        withContext(Dispatchers.IO){
            val expenseCategoryId = expenseCategoryDao.getId(budget.expenseCategory!!.nameCategory) ?: return@withContext
            budgetDao.delete(budget.dateStart!!, budget.dateFinish!!, expenseCategoryId)
        }
    }

    fun getFullBudgetDetailsValueAsc(start: Long, finish: Long, categoryName: String): Flow<MutableList<Budget>> {
        return budgetDao.getFullBudgetDetailsValueAsc(start, finish, categoryName).map { list ->
            list.map { fromBudgetFullInfoToBudget(it) }
            .toMutableList()
        }
    }

    fun getFullBudgetDetailsValueDesc(start: Long, finish: Long, categoryName: String): Flow<MutableList<Budget>> {
        return budgetDao.getFullBudgetDetailsValueDesc(start, finish, categoryName).map { list ->
            list.map { fromBudgetFullInfoToBudget(it) }.toMutableList()
        }
    }

    fun getFullBudgetDetailsDateAsc(start: Long, finish: Long, categoryName: String): Flow<MutableList<Budget>> {
        return budgetDao.getFullBudgetDetailsDateAsc(start, finish, categoryName).map { list ->
            list.map { fromBudgetFullInfoToBudget(it) }.toMutableList()
        }
    }

    fun getFullBudgetDetailsDateDesc(start: Long, finish: Long, categoryName: String): Flow<MutableList<Budget>> {
        return budgetDao.getFullBudgetDetailsDateDesc(start, finish, categoryName).map { list ->
            list.map { fromBudgetFullInfoToBudget(it) }.toMutableList()
        }
    }
}