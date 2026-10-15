package com.example.data.repository

import com.example.data.dao.HabitDao
import com.example.data.dao.HabitLogDao
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.util.DateUtils
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao
) {
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()
    val allLogs: Flow<List<HabitLog>> = habitLogDao.getAllLogs()

    fun getLogsForHabit(habitId: Long): Flow<List<HabitLog>> =
        habitLogDao.getLogsForHabit(habitId)

    suspend fun getHabitById(id: Long): Habit? =
        habitDao.getHabitById(id)

    suspend fun insertHabit(habit: Habit): Long =
        habitDao.insertHabit(habit)

    suspend fun updateHabit(habit: Habit) =
        habitDao.updateHabit(habit)

    suspend fun deleteHabit(habitId: Long) {
        habitLogDao.deleteLogsForHabit(habitId)
        habitDao.deleteHabitById(habitId)
    }

    suspend fun toggleHabitCompletion(habitId: Long, dateIso: String): Boolean {
        val existing = habitLogDao.getLog(habitId, dateIso)
        return if (existing != null && existing.completed) {
            habitLogDao.deleteLog(habitId, dateIso)
            false
        } else {
            habitLogDao.insertLog(
                HabitLog(
                    habitId = habitId,
                    date = dateIso,
                    completed = true
                )
            )
            true
        }
    }

    suspend fun setHabitCompletion(habitId: Long, dateIso: String, completed: Boolean) {
        if (completed) {
            habitLogDao.insertLog(
                HabitLog(
                    habitId = habitId,
                    date = dateIso,
                    completed = true
                )
            )
        } else {
            habitLogDao.deleteLog(habitId, dateIso)
        }
    }

    suspend fun clearAll() {
        habitLogDao.clearAll()
        habitDao.clearAll()
    }

    suspend fun seedDemoDataIfEmpty() {
        // Only seed if empty
        val sampleHabits = listOf(
            Habit(
                name = "Drink 2.5L Water",
                iconName = "water_drop",
                colorHex = "#3B82F6", // Blue
                frequencyType = "DAILY",
                selectedDays = "MON,TUE,WED,THU,FRI,SAT,SUN",
                reminderEnabled = true,
                reminderTime = "09:00 AM"
            ),
            Habit(
                name = "Morning Jog & Stretch",
                iconName = "directions_run",
                colorHex = "#F97316", // Energetic Orange
                frequencyType = "WEEKDAYS",
                selectedDays = "MON,WED,FRI,SAT",
                reminderEnabled = true,
                reminderTime = "06:30 AM"
            ),
            Habit(
                name = "Read 20 Mins",
                iconName = "menu_book",
                colorHex = "#8B5CF6", // Purple
                frequencyType = "DAILY",
                selectedDays = "MON,TUE,WED,THU,FRI,SAT,SUN",
                reminderEnabled = true,
                reminderTime = "09:30 PM"
            ),
            Habit(
                name = "Meditation & Breathing",
                iconName = "self_improvement",
                colorHex = "#22C55E", // Green
                frequencyType = "DAILY",
                selectedDays = "MON,TUE,WED,THU,FRI,SAT,SUN",
                reminderEnabled = false,
                reminderTime = "08:00 AM"
            ),
            Habit(
                name = "Code & Learn Kotlin",
                iconName = "code",
                colorHex = "#EC4899", // Rose
                frequencyType = "WEEKDAYS",
                selectedDays = "MON,TUE,WED,THU,FRI",
                reminderEnabled = true,
                reminderTime = "07:00 PM"
            )
        )

        val cal = Calendar.getInstance()
        val todayIso = DateUtils.getTodayIso()

        // Insert habits and generate realistic past streak logs (past 60 days)
        sampleHabits.forEachIndexed { index, habit ->
            val habitId = habitDao.insertHabit(habit)

            // Seed completion logs:
            // index 0 (Water): 18-day active streak (including today)
            // index 1 (Jog): 6-day streak on scheduled days, pending today
            // index 2 (Read): 24-day active streak (including today)
            // index 3 (Meditation): 8-day active streak, pending today
            // index 4 (Code): 12-day streak (including today)

            val streakLength = when (index) {
                0 -> 18
                1 -> 9
                2 -> 24
                3 -> 8
                4 -> 12
                else -> 7
            }
            val completedToday = (index == 0 || index == 2 || index == 4)

            for (dayOffset in 0..60) {
                val pastCal = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -dayOffset)
                }
                val dateIso = DateUtils.formatToIso(pastCal.time)
                val dayKey = DateUtils.getDayOfWeekKey(dateIso)

                if (!habit.isScheduledForDay(dayKey)) continue

                var shouldComplete = false
                if (dayOffset == 0) {
                    shouldComplete = completedToday
                } else if (dayOffset <= streakLength) {
                    // Part of current streak
                    shouldComplete = true
                } else {
                    // Older historical patterns (e.g. 75% probability with occasional gaps)
                    val pseudoRandom = (habitId * 37 + dayOffset * 17) % 10
                    shouldComplete = pseudoRandom > 2
                }

                if (shouldComplete) {
                    habitLogDao.insertLog(
                        HabitLog(
                            habitId = habitId,
                            date = dateIso,
                            completed = true
                        )
                    )
                }
            }
        }
    }
}
