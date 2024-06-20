package com.proger.cashtracker.db.query

import com.proger.cashtracker.models.Budget
import com.proger.cashtracker.models.Category

data class BudgetFullInfo(
    var id: Long,
    var date_start: Long,
    var date_finish: Long,
    var budget: Double,
    var spend_cash: Double,
    var name_category: String,
    var image: String
)

data class BudgetShortInfo(
    var date_start: Long,
    var date_finish: Long,
    var budget: Double,
    var spend_cash: Double,
    var name_category: String,
    var image: String
)

fun fromBudgetFullInfoToBudget(bfi: BudgetFullInfo): Budget {
    return Budget(
        id = bfi.id,
        dateStart = bfi.date_start,
        dateFinish = bfi.date_finish,
        budget = bfi.budget,
        spendCash = bfi.spend_cash,
        expenseCategory = Category(nameCategory = bfi.name_category, image = bfi.image)
    )
}