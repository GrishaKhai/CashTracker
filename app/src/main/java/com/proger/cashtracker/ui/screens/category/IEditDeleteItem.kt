package com.proger.cashtracker.ui.screens.category

import com.proger.cashtracker.models.Category

interface IEditDeleteItem {
    fun edit(category: Category)
    fun delete(category: Category)
}