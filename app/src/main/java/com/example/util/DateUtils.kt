package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayHeaderFormat = SimpleDateFormat("EEEE, MMMM d", Locale.US)
    private val shortMonthDayFormat = SimpleDateFormat("MMM d", Locale.US)
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.US)

    fun getTodayIso(): String {
        return isoFormat.format(Date())
    }

    fun getTodayDisplay(): String {
        return displayHeaderFormat.format(Date())
    }

    fun formatToIso(date: Date): String {
        return isoFormat.format(date)
    }

    fun parseIso(isoString: String): Date? {
        return try {
            isoFormat.parse(isoString)
        } catch (e: Exception) {
            null
        }
    }

    fun formatShortMonthDay(isoString: String): String {
        val date = parseIso(isoString) ?: return isoString
        return shortMonthDayFormat.format(date)
    }

    fun formatMonthYear(date: Date): String {
        return monthYearFormat.format(date)
    }

    /**
     * Returns the list of last N days ISO strings ending with today.
     * e.g. for count=7: [today-6, today-5, ..., today]
     */
    fun getLastNDaysIso(count: Int): List<String> {
        val result = mutableListOf<String>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -(count - 1))
        for (i in 0 until count) {
            result.add(isoFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return result
    }

    /**
     * Returns single character or short abbreviation for day of week from ISO date.
     * e.g. "M", "T", "W", "T", "F", "S", "S"
     */
    fun getDayOfWeekShort(isoString: String): String {
        val date = parseIso(isoString) ?: return ""
        val cal = Calendar.getInstance()
        cal.time = date
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "M"
            Calendar.TUESDAY -> "T"
            Calendar.WEDNESDAY -> "W"
            Calendar.THURSDAY -> "T"
            Calendar.FRIDAY -> "F"
            Calendar.SATURDAY -> "S"
            Calendar.SUNDAY -> "S"
            else -> ""
        }
    }

    /**
     * Day of week key matching MON, TUE, WED, THU, FRI, SAT, SUN
     */
    fun getDayOfWeekKey(isoString: String): String {
        val date = parseIso(isoString) ?: return ""
        val cal = Calendar.getInstance()
        cal.time = date
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MON"
            Calendar.TUESDAY -> "TUE"
            Calendar.WEDNESDAY -> "WED"
            Calendar.THURSDAY -> "THU"
            Calendar.FRIDAY -> "FRI"
            Calendar.SATURDAY -> "SAT"
            Calendar.SUNDAY -> "SUN"
            else -> ""
        }
    }

    /**
     * Generate date grid for current month or specified month offset
     */
    fun getMonthDays(monthOffset: Int = 0): List<DateCell> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.add(Calendar.MONTH, monthOffset)
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        // Find starting day of week (Monday=1, Sunday=7)
        val startDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday = 0, Sunday = 6

        val cells = mutableListOf<DateCell>()
        // Add empty slots before the 1st
        for (i in 0 until startDayOfWeek) {
            cells.add(DateCell(dayNumber = 0, isoDate = "", isCurrentMonth = false))
        }

        for (day in 1..daysInMonth) {
            val cellCal = Calendar.getInstance().apply {
                set(Calendar.YEAR, currentYear)
                set(Calendar.MONTH, currentMonth)
                set(Calendar.DAY_OF_MONTH, day)
            }
            val iso = isoFormat.format(cellCal.time)
            cells.add(DateCell(dayNumber = day, isoDate = iso, isCurrentMonth = true))
        }

        return cells
    }

    /**
     * Generate last 12 weeks of ISO dates formatted for GitHub-style heatmap (7 rows x 12 cols)
     */
    fun getHeatmapGridDays(weeks: Int = 12): List<String> {
        val cal = Calendar.getInstance()
        // Adjust to ending on current week Sunday or Saturday
        val currentDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7 // Monday=0..Sunday=6
        val daysNeeded = weeks * 7
        cal.add(Calendar.DAY_OF_YEAR, -daysNeeded + (6 - currentDayOfWeek) + 1)

        val dates = mutableListOf<String>()
        for (i in 0 until daysNeeded) {
            dates.add(isoFormat.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return dates
    }
}

data class DateCell(
    val dayNumber: Int,
    val isoDate: String,
    val isCurrentMonth: Boolean
)
