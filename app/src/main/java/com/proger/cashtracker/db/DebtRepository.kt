package com.proger.cashtracker.db

import com.proger.cashtracker.db.dao.AccountDao
import com.proger.cashtracker.db.dao.ContributionDao
import com.proger.cashtracker.db.dao.DebtDao
import com.proger.cashtracker.db.dao.DebtorDao
import com.proger.cashtracker.db.query.ContributionFullDetails
import com.proger.cashtracker.db.query.FullDebtDetails
import com.proger.cashtracker.db.query.ShortDebtDetails
import com.proger.cashtracker.db.query.fromTransactionInfoFullToTransaction
import com.proger.cashtracker.db.table.ContributionEntity
import com.proger.cashtracker.db.table.DebtEntity
import com.proger.cashtracker.db.table.DebtorEntity
import com.proger.cashtracker.models.Account
import com.proger.cashtracker.models.Contribution
import com.proger.cashtracker.models.Currency
import com.proger.cashtracker.models.Debt
import com.proger.cashtracker.models.DebtRole
import com.proger.cashtracker.models.Debtor
import com.proger.cashtracker.models.EditedNoteNotExistInDbException
import com.proger.cashtracker.models.NoteNotExistInDbException
import com.proger.cashtracker.models.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtRepository @Inject constructor(
    private val debtDao: DebtDao,
    private val debtorDao: DebtorDao,
    private val contributionDao: ContributionDao,
    private val accountDao: AccountDao,
    private val db: AppDatabase
) {
    //========================================== DEBTORS ===========================================

    fun getDebtors(): Flow<MutableList<Debtor>>  {
        return debtorDao.getAll().map { list ->
            list.map {
                Debtor(nameDebtor = it.nameDebtor)
            }.toMutableList()
        }
    }
//    suspend fun getDebtorFirst(): Debtor  {
//        return Debtor(nameDebtor = debtorDao.getFirst().first().nameDebtor)
//    }

    //============================================ DEBT ============================================
//    fun getDebt(idDebt: Int): Flow<Debt> = debtDao.get(idDebt).map { it.toDebt() }
    suspend fun getDebt(idDebt: Int): Debt? = debtDao.get(idDebt).map { it.toDebt() }.firstOrNull()
    fun getDebts(isReturned: Boolean, accountName: String): Flow<MutableList<Debt>> {
        return debtDao.getFullDetails(isReturned = if(isReturned) 1 else 0, accountName = accountName).map { list ->
            list.map {
                it.toDebt()
            }.toMutableList()
        }
    }
    fun shortDetailsForDebtsGroup(start: Long, finish: Long, accountName: String): Flow<MutableList<ShortDebtDetails>> {
        return debtDao.getShortDetails(start = start, finish = finish, accountName = accountName)
    }

    fun getFullDebtDetailsValueAsc(start: Long, finish: Long, isDebtor: Boolean, nameDebtorOrCreditor: String, accountName: String): Flow<MutableList<Debt>> {
        return debtDao.getFullDebtDetailsValueAsc(start, finish, if(isDebtor) 1 else 0, nameDebtorOrCreditor, accountName).map { list ->
            list.map { it.toDebt() }.toMutableList()}
    }
    fun getFullDebtDetailsValueDesc(start: Long, finish: Long, isDebtor: Boolean, nameDebtorOrCreditor: String, accountName: String): Flow<MutableList<Debt>> {
        return debtDao.getFullDebtDetailsValueDesc(start, finish, if(isDebtor) 1 else 0, nameDebtorOrCreditor, accountName).map { list ->
            list.map { it.toDebt() }.toMutableList()}
    }
    fun getFullDebtDetailsDateAsc(start: Long, finish: Long, isDebtor: Boolean, nameDebtorOrCreditor: String, accountName: String): Flow<MutableList<Debt>> {
        return debtDao.getFullDebtDetailsDateAsc(start, finish, if(isDebtor) 1 else 0, nameDebtorOrCreditor, accountName).map { list ->
            list.map { it.toDebt() }.toMutableList()}
    }
    fun getFullDebtDetailsDateDesc(start: Long, finish: Long, isDebtor: Boolean, nameDebtorOrCreditor: String, accountName: String): Flow<MutableList<Debt>> {
        return debtDao.getFullDebtDetailsDateDesc(start, finish, if(isDebtor) 1 else 0, nameDebtorOrCreditor, accountName).map { list ->
            list.map { it.toDebt() }.toMutableList()}
    }
    fun getFullDebtDetailsByComment(start: Long, finish: Long, isDebtor: Boolean, nameDebtorOrCreditor: String, accountName: String, comment: String): Flow<MutableList<Debt>> {
        return debtDao.getFullDebtDetailsByComment(start, finish, if(isDebtor) 1 else 0, nameDebtorOrCreditor, accountName, comment).map { list ->
            list.map { it.toDebt() }.toMutableList()}
    }

    suspend fun updateDebt(oldDebtId: Int, debt: Debt, debtRole: DebtRole) {
        withContext(Dispatchers.IO) {
            debt.validate()
            if (accountDao.getCountByName(debt.account!!.name) == 0)
                throw NoteNotExistInDbException(debt.account!!.name)
            if (debtDao.getCountById(oldDebtId) == 0) throw EditedNoteNotExistInDbException()//редактируемой записи нет

            val oldDebt = debtDao.get(oldDebtId).first()

            val creditorOrDebtorId = debtorDao.get(debt.creditorOrDebtor!!.nameDebtor).first().id
            val account = accountDao.get(debt.account!!.name)
            val accountId = account.id
            val debtId = debt.id!!.toInt()

            val debtEntity = DebtEntity(
                id = oldDebtId,
                debt = debt.debt!!,
                startDate = debt.startDate!!,
                finishDate = debt.finishDate!!,
                comment = debt.comment!!,
                isReturned = debt.isReturned,
                creditorOrDebtorId = creditorOrDebtorId!!,
                isDebtor = debt.isDebtor,
                accountId = accountId!!,
            )

            //если пользователь уменьшил сумму долга, а внесенный ранее долг перекрывает новое значение долга, то этот код вернет остаток на счет, а долг закроет
            val returned = contributionDao.getReturnedValueByDebtId(debtId)
            if(returned > debt.debt!!){
                var remainder = returned - debt.debt!!

                val contributionList = contributionDao.getByDebtId(oldDebtId).first()
//                db.runInTransaction{ }
                for(i in contributionList.size downTo 0){
                    if(contributionList[i].contribution > remainder){
                        remainder = contributionList[i].contribution - remainder
                        contributionDao.update(contributionList[i].id!!, remainder)
                        break
                    } else{
                        remainder -= contributionList[i].contribution
                        contributionDao.delete(contributionList[i])
                    }
                }

                debtEntity.isReturned = true
                accountDao.updateAccountBalance(//обновляет баланс
                    accountId,
                    if (debtRole == DebtRole.Debtor) (debt.debt!! - oldDebt.debt + (returned - debt.debt!!))
                    else (oldDebt.debt - debt.debt!! + (debt.debt!! - returned))
                )
            } else {
                debtEntity.isReturned = returned == debt.debt!!
                accountDao.updateAccountBalance(//обновляет баланс, а внесенные долги уже учтены
                    accountId,
                    if (debtRole == DebtRole.Debtor) (debt.debt!! - oldDebt.debt)
                    else (oldDebt.debt - debt.debt!!)
                )
            }
            debtDao.update(debtEntity)
        }
    }
    suspend fun insertDebt(debt: Debt, debtRole: DebtRole) {
        withContext(Dispatchers.IO) {
            debt.validate()
            if (accountDao.getCountByName(debt.account!!.name) == 0)
                throw NoteNotExistInDbException(debt.account!!.name)

            //создание новых должников
            if (debtorDao.getCountByName(debt.creditorOrDebtor!!.nameDebtor) == 0) {
                debtorDao.insert(DebtorEntity(null, debt.creditorOrDebtor!!.nameDebtor))
            }

            val creditorOrDebtorId = debtorDao.get(debt.creditorOrDebtor!!.nameDebtor).first().id
            val account = accountDao.get(debt.account!!.name)
            val accountId = account.id

            debtDao.insert(
                DebtEntity(
                    id = null,
                    debt = debt.debt!!,
                    startDate = debt.startDate!!,
                    finishDate = debt.finishDate!!,
                    comment = debt.comment!!,
                    isReturned = debt.isReturned,
                    creditorOrDebtorId = creditorOrDebtorId!!,
                    isDebtor = debt.isDebtor,
                    accountId = accountId!!,
                )
            )

            var value = debt.debt!!
            value = if (debtRole == DebtRole.Debtor) value else -value
            accountDao.updateAccountBalance(accountId, value)
        }
    }
    suspend fun deleteDebt(debt: Debt, debtRole: DebtRole) {
        withContext(Dispatchers.IO) {
            val creditorOrDebtorId = debtorDao.get(debt.creditorOrDebtor!!.nameDebtor).first().id
            val account = accountDao.get(debt.account!!.name)
            val accountId = account.id
            val debtId = debt.id!!.toInt()

            debtDao.delete(
                DebtEntity(
                    id = debtId,
                    debt = debt.debt!!,
                    startDate = debt.startDate!!,
                    finishDate = debt.finishDate!!,
                    comment = debt.comment!!,
                    isReturned = debt.isReturned,
                    creditorOrDebtorId = creditorOrDebtorId!!,
                    isDebtor = debt.isDebtor,
                    accountId = accountId!!,
                )
            )

            var returned = contributionDao.getReturnedValueByDebtId(debtId)
            returned =
                if (debtRole == DebtRole.Debtor) -(debt.debt!! - returned) else (debt.debt!! - returned)

            accountDao.updateAccountBalance(accountId, returned)
            contributionDao.deleteByDebtId(debtId)
        }
    }
    /*suspend fun moveDebt(debt: Debt, debtRole: DebtRole){
        withContext(Dispatchers.IO){
            val debtorId = debtorDao.get(debt.debtor!!.nameDebtor).first().id
            val creditorId = debtorDao.get(debt.creditor!!.nameDebtor).first().id
            val account = accountDao.get(debt.account!!.name)
            val accountId = account.id

            debtDao.delete(
                DebtEntity(
                    id = debt.id!!.toInt(),
                    debt = debt.debt!!,
                    startDate = debt.startDate!!,
                    finishDate = debt.finishDate!!,
                    comment = debt.comment!!,
                    isReturned = debt.isReturned,
                    creditorId = creditorId!!,
                    debtorId = debtorId!!,
                    accountId = accountId!!,
                )
            )

            var value = contributionDao.getReturned(debt.id!!.toInt())
            value = if(debtRole == DebtRole.Debtor) -(debt.debt!! - value) else (debt.debt!! - value)

            accountDao.updateAccountBalance(accountId, value)
        }
    }*/


    //======================================= CONTRIBUTION =========================================
    /*fun getContribution(debtId: Int) : Flow<List<Contribution>>{
        return contributionDao.getByDebtId(debtId).map { list ->
            list.map {
                Contribution(
                    date = it.date,
                    contribution = it.contribution,
                    account = ,
                )
            }.toList()
        }
    }*/
    fun getContributions(debtId: Int) : Flow<List<Contribution>>{
        return contributionDao.getFullByDebtId(debtId).map { list ->
            list.map { it.toContribution() }.toList()
        }
    }
    suspend fun updateContribution(contribution: Contribution, debtId: Int, contributionId: Int, debtRole: DebtRole){
        withContext(Dispatchers.IO){
            contribution.validate()
            if(contributionDao.getCountById(contributionId) == 0){
                throw EditedNoteNotExistInDbException()//редактируемой записи нет
            }
            if(debtDao.getCountById(debtId) == 0) throw EditedNoteNotExistInDbException()//редактируемой записи нет

            val accountId = accountDao.get(contribution.account!!.name).id!!
            val debt = debtDao.get(debtId).first()

            val oldContribution = contributionDao.get(contributionId).first()

            val debtValue = debt.debt // / debt.current_exchange_rate_to_base
            val currencyNewContribution = contribution.account.currency!!.rateToBase
            var newContributionValue = contribution.contribution!! / currencyNewContribution
            val oldContributionValue = oldContribution.contribution / oldContribution.rate_to_base

            val returned = debt.returned - oldContributionValue//то же самое но без лишнего скрипта: contributionDao.getReturnedValueByDebtId(debtId) - oldContributionValue
            if((returned + newContributionValue) >= debtValue){//вносимая сумма с внесенной сумой ранее превышает или равна долгу
                newContributionValue = debtValue - returned - oldContributionValue//сумма которой нехватает что бы на счету было записано значение измененного пополнения
                debtDao.closeDebt(debtId)
            } else {
                newContributionValue -= oldContributionValue//сумма которой нехватает что бы на счету было записано значение измененного пополнения
            }
            newContributionValue *= currencyNewContribution//возврат значения к курсу на котором был он изначально
            if(debtRole == DebtRole.Debtor) accountDao.updateAccountBalance(accountId, -newContributionValue)
            else accountDao.updateAccountBalance(accountId, newContributionValue)

            contributionDao.update(ContributionEntity(
                id = contributionId,
                date = contribution.date,
                contribution = newContributionValue + (oldContributionValue * currencyNewContribution),// потому что на newContributionValue было значение которого не хватало для установки измененного значения взноса
                debtId = debtId,
                accountId = accountId,
            ))
        }
    }
    suspend fun insertContribution(contribution: Contribution, debtId: Int, debtRole: DebtRole){
        withContext(Dispatchers.IO){
            contribution.validate()
            if(debtDao.getCountById(debtId) == 0) throw EditedNoteNotExistInDbException()//редактируемой записи нет

            val accountId = accountDao.get(contribution.account!!.name).id!!
            val debt = debtDao.get(debtId).first()

            val currencyNewContribution = contribution.account.currency!!.rateToBase
            var newContributionValue = contribution.contribution!! / currencyNewContribution

            if((debt.returned + newContributionValue) >= debt.debt){//вносимая сумма с внесенной сумой ранее превышает или равна долгу
                newContributionValue = debt.debt - debt.returned//сумма которой нехватает что бы на счету был закрыт долг
                debtDao.closeDebt(debtId)
            }
            newContributionValue *= currencyNewContribution//возврат значения к курсу на котором был он изначально
            if(debtRole == DebtRole.Debtor) accountDao.updateAccountBalance(accountId, -newContributionValue)
            else accountDao.updateAccountBalance(accountId, newContributionValue)

            contributionDao.insert(ContributionEntity(
                id = null,
                date = contribution.date,
                contribution = newContributionValue,
                debtId = debtId,
                accountId = accountId,
            ))
        }
    }
    suspend fun deleteContribution(contribution: Contribution, debtRole: DebtRole){
        withContext(Dispatchers.IO){
            val contributionId = contribution.contributionId!!
            if(contributionDao.getCountById(contributionId.toInt()) == 0) return@withContext

            val accountId = accountDao.get(contribution.account!!.name).id!!
            val pay = contribution.contribution
            val debt = debtDao.get(contribution.debtId!!.toInt()).first()

            if(debtRole == DebtRole.Debtor) accountDao.updateAccountBalance(accountId, pay!!)
            else accountDao.updateAccountBalance(accountId, -pay!!)
            contributionDao.delete(contributionId.toInt())
            if(debt.is_returned) debtDao.openDebt(debt.id.toInt())
        }
    }







    private fun ContributionFullDetails.toContribution(): Contribution {
        return Contribution(
            contributionId = this.id,
            debtId = this.debt_id,
            date = this.date,
            contribution = this.contribution,
            account = Account(
                name = this.account_name,
                balance = this.balance,
                image = this.image,
                currency = Currency(
                    currencyCode = this.currency_code,
                    currencyName = this.currency_name,
                    rateToBase = this.rate_to_base,
                )
            )
        )
    }
    private fun FullDebtDetails.toDebt(): Debt {
        return Debt(
            id = this.id,
            debt = this.debt,
            returned = this.returned,
            startDate = this.start_date,
            finishDate = this.finish_date,
            comment = this.comment,
            isReturned = this.is_returned,
            creditorOrDebtor = Debtor(this.creditor_or_debtor),
            isDebtor = this.is_debtor,
            account = Account(
                name = this.name_account,
                balance = this.balance_account,
                image = this.image,
                currency = Currency(
                    currencyCode = this.currency_code,
                    currencyName = this.currency_name,
                    rateToBase = this.current_exchange_rate_to_base
                )
            )
        )
    }
}