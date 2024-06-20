package com.proger.cashtracker.ui.screens.tabs

interface IUpdateRadarChart {
    fun update(dataList: List<RadarItemData>)
}

data class RadarItemData(
    val name: String,
    val value: Float,
    val full: Float
)