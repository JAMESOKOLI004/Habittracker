package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Habit
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Streaks+", appName)
    }

    @Test
    fun `habit schedule check verifies frequency`() {
        val dailyHabit = Habit(
            name = "Drink Water",
            frequencyType = "DAILY"
        )
        assertTrue(dailyHabit.isScheduledForDay("MON"))
        assertTrue(dailyHabit.isScheduledForDay("SUN"))

        val weekdayHabit = Habit(
            name = "Morning Jog",
            frequencyType = "WEEKDAYS",
            selectedDays = "MON,WED,FRI"
        )
        assertTrue(weekdayHabit.isScheduledForDay("MON"))
        assertTrue(weekdayHabit.isScheduledForDay("WED"))
        assertTrue(weekdayHabit.isScheduledForDay("FRI"))
        assertFalse(weekdayHabit.isScheduledForDay("TUE"))
        assertFalse(weekdayHabit.isScheduledForDay("SUN"))
    }

    @Test
    fun `date utils returns valid formatted strings`() {
        val todayIso = DateUtils.getTodayIso()
        assertTrue(todayIso.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))

        val last7Days = DateUtils.getLastNDaysIso(7)
        assertEquals(7, last7Days.size)
        assertEquals(todayIso, last7Days.last())
    }
}
