package com.proger.cashtracker.utils

import androidx.core.util.Pair

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class DateHelper {
    companion object{

        fun makeDayMonthYearFormat(date: Date): String = SimpleDateFormat("dd MMMM yyyy").format(date)
        fun makeMonthYearFormat(date: Date): String = SimpleDateFormat("MMMM yyyy").format(date)
        fun makeYearFormat(date: Date): String = SimpleDateFormat("yyyy").format(date)
        fun makeWeekFormat(dayOfWeek: Date): String {
            val sdf = SimpleDateFormat("dd MMMM yyyy")
            val calendar = Calendar.getInstance()
            calendar.setTime(dayOfWeek)
            calendar[Calendar.DAY_OF_WEEK] = calendar.firstDayOfWeek + 1
            val firstDayOfWeek = sdf.format(calendar.time)
            calendar.add(Calendar.DATE, 6)
            val lastDayOfWeek = sdf.format(calendar.time)
            return "$firstDayOfWeek - $lastDayOfWeek"
        }
        fun makeRangeFormat(dayStart: Date, dayFinish: Date): String {
            val f = SimpleDateFormat("dd MMMM yyyy")
            return f.format(dayStart) + " - " + f.format(dayFinish);
        }
        fun makeDayTimeFormat(date: Date) = SimpleDateFormat("hh:MM:ss DD.mm.YYYYY").format(date)
        fun makeShortDayMonthYearFormat(date: Date) = SimpleDateFormat("dd.MM.yyyy").format(date)

        fun makeYearRange(day: Date): Pair<Date, Date> {
            val calendar = Calendar.getInstance()
            calendar.setTime(day)
            calendar[Calendar.DAY_OF_YEAR] = 1
            val first = calendar.time
            calendar[Calendar.DAY_OF_YEAR] = calendar.getActualMaximum(Calendar.DAY_OF_YEAR)
            val second = calendar.time
            return Pair(getStartDay(first), getFinishDay(second))
//            return Pair(first, second)
        }
        fun makeWeekRange(dayOfWeek: Date): Pair<Date, Date> {
            val calendar = Calendar.getInstance()
            calendar.setTime(dayOfWeek)
            calendar[Calendar.DAY_OF_WEEK] = calendar.firstDayOfWeek + 1
            val first = calendar.time
            calendar.add(Calendar.DATE, 6)
            val second = calendar.time
            return Pair(getStartDay(first), getFinishDay(second))
//            return Pair(first, second)
        }
        fun makeMonthRange(day: Date): Pair<Date, Date> {
            val calendar = Calendar.getInstance()
            calendar.setTime(day)
            calendar[Calendar.DAY_OF_MONTH] = 1
            val first = calendar.time
            calendar[Calendar.DAY_OF_MONTH] = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val second = calendar.time
            return Pair(getStartDay(first), getFinishDay(second))
//            return Pair(first, second)
        }
        fun makeDayRange(day: Date): Pair<Date, Date> {
            return Pair(getStartDay(day), getFinishDay(day))
        }

        fun getStartNextDay(): Date {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.time
        }

        fun getStartDay(day: Date) : Date{
            return Calendar.getInstance().apply {
                time = day
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }
        fun getFinishDay(day: Date) : Date{
            return Calendar.getInstance().apply {
                time = day
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time
        }

        fun isToday(day: Date): Boolean {
            val today = makeDayRange(Date())
            return today.first <= day && day <= today.second
        }
    }
}