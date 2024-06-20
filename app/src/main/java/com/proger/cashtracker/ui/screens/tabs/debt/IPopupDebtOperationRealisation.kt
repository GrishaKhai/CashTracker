package com.proger.cashtracker.ui.screens.tabs.debt

import com.proger.cashtracker.models.Debt

interface IPopupDebtOperationFullRealisation {
    fun edit(debt: Debt)
    fun delete(debt: Debt)
    fun details(debt: Debt)
    fun payDebt(debt: Debt)
}
interface IPopupDebtOperationShortRealisation {
    fun delete(debt: Debt)
    fun details(debt: Debt)
}