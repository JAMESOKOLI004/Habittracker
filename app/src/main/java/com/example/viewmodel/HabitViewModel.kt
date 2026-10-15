package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.StreaksDatabase
import com.example.data.model.DayStatus
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.data.model.HabitWithStats
import com.example.data.repository.HabitRepository
import com.example.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class StatsSummary(
    val totalHabits: Int = 0,
    val completedToday: Int = 0,
    val todayScheduledCount: Int = 0,
    val todayPercent: Int = 0,
    val bestStreak: Int = 0,
    val bestStreakHabitName: String = "",
    val averageCompletionRate: Int = 0,
    val totalCheckins: Int = 0,
    val perfectDaysCount: Int = 0
)

enum class HabitFilter {
    ALL,
    PENDING,
    COMPLETED
}

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HabitRepository
    val selectedFilter = MutableStateFlow(HabitFilter.ALL)
    val selectedHabitId = MutableStateFlow<Long?>(null)

    // User Preferences
    val themeMode = MutableStateFlow("SYSTEM") // "SYSTEM", "LIGHT", "DARK"
    val weekStartDay = MutableStateFlow("MON") // "MON", "SUN"
    val notificationsEnabled = MutableStateFlow(true)
    val soundHapticsEnabled = MutableStateFlow(true)

    init {
        val db = StreaksDatabase.getDatabase(application)
        repository = HabitRepository(db.habitDao(), db.habitLogDao())

        // Automatically seed rich sample habits on first run
        viewModelScope.launch {
            val habitsFlow = db.habitDao().getAllHabits()
            launch {
                habitsFlow.collect { list ->
                    if (list.isEmpty()) {
                        repository.seedDemoDataIfEmpty()
                    }
                }
            }
        }
    }

    val habitsWithStats: StateFlow<List<HabitWithStats>> = combine(
        repository.allHabits,
        repository.allLogs
    ) { habits, logs ->
        val todayIso = DateUtils.getTodayIso()
        val last7DaysIso = DateUtils.getLastNDaysIso(7)
        val logsByHabit = logs.groupBy { it.habitId }

        habits.map { habit ->
            val habitLogs = logsByHabit[habit.id] ?: emptyList()
            val completedDates = habitLogs.filter { it.completed }.map { it.date }.toSet()

            val isCompletedToday = completedDates.contains(todayIso)

            // Calculate current streak
            var currentStreak = 0
            val cal = Calendar.getInstance()
            // If today is completed, start checking from today
            // If today is not completed, start checking from yesterday
            val todayDayKey = DateUtils.getDayOfWeekKey(todayIso)
            val todayScheduled = habit.isScheduledForDay(todayDayKey)

            if (isCompletedToday) {
                currentStreak++
                cal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                cal.add(Calendar.DAY_OF_YEAR, -1)
            }

            // Look back up to 365 days
            for (i in 1..365) {
                val pastIso = DateUtils.formatToIso(cal.time)
                val dayKey = DateUtils.getDayOfWeekKey(pastIso)

                if (habit.isScheduledForDay(dayKey)) {
                    if (completedDates.contains(pastIso)) {
                        currentStreak++
                    } else {
                        break
                    }
                }
                cal.add(Calendar.DAY_OF_YEAR, -1)
            }

            // Calculate longest streak
            var longestStreak = 0
            var tempStreak = 0
            val scanCal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -120)
            }
            for (i in 0..120) {
                val scanIso = DateUtils.formatToIso(scanCal.time)
                val dayKey = DateUtils.getDayOfWeekKey(scanIso)
                if (habit.isScheduledForDay(dayKey)) {
                    if (completedDates.contains(scanIso)) {
                        tempStreak++
                        if (tempStreak > longestStreak) {
                            longestStreak = tempStreak
                        }
                    } else {
                        tempStreak = 0
                    }
                }
                scanCal.add(Calendar.DAY_OF_YEAR, 1)
            }
            if (currentStreak > longestStreak) {
                longestStreak = currentStreak
            }

            // Calculate completion rate (last 30 scheduled days)
            var scheduledDaysCount = 0
            var completedCountIn30 = 0
            val rateCal = Calendar.getInstance()
            for (i in 0 until 30) {
                val iso = DateUtils.formatToIso(rateCal.time)
                val dayKey = DateUtils.getDayOfWeekKey(iso)
                if (habit.isScheduledForDay(dayKey)) {
                    scheduledDaysCount++
                    if (completedDates.contains(iso)) {
                        completedCountIn30++
                    }
                }
                rateCal.add(Calendar.DAY_OF_YEAR, -1)
            }
            val completionRate = if (scheduledDaysCount > 0) {
                (completedCountIn30 * 100) / scheduledDaysCount
            } else 100

            // Build 7-day mini row
            val recent7 = last7DaysIso.map { iso ->
                val dayKey = DateUtils.getDayOfWeekKey(iso)
                val isSched = habit.isScheduledForDay(dayKey)
                DayStatus(
                    dateIso = iso,
                    dayShort = DateUtils.getDayOfWeekShort(iso),
                    isScheduled = isSched,
                    isCompleted = completedDates.contains(iso),
                    isToday = (iso == todayIso)
                )
            }

            HabitWithStats(
                habit = habit,
                isCompletedToday = isCompletedToday,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                totalCompletions = completedDates.size,
                completionRatePercent = completionRate,
                recent7Days = recent7
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val statsSummary: StateFlow<StatsSummary> = habitsWithStats.combine(repository.allLogs) { list, logs ->
        val total = list.size
        val todayIso = DateUtils.getTodayIso()
        val todayKey = DateUtils.getDayOfWeekKey(todayIso)

        val scheduledToday = list.filter { it.habit.isScheduledForDay(todayKey) }
        val completedToday = scheduledToday.count { it.isCompletedToday }
        val todayPct = if (scheduledToday.isNotEmpty()) {
            (completedToday * 100) / scheduledToday.size
        } else 0

        val bestHabit = list.maxByOrNull { it.currentStreak }
        val bestStreak = bestHabit?.currentStreak ?: 0
        val bestName = bestHabit?.habit?.name ?: "None"

        val avgRate = if (list.isNotEmpty()) {
            list.map { it.completionRatePercent }.average().toInt()
        } else 0

        val totalCheckins = logs.count { it.completed }

        // Perfect days in the last 30 days
        var perfectDays = 0
        val pCal = Calendar.getInstance()
        val completedLogsSet = logs.filter { it.completed }.map { "${it.habitId}_${it.date}" }.toSet()

        for (d in 1..30) {
            pCal.add(Calendar.DAY_OF_YEAR, -1)
            val pastDateIso = DateUtils.formatToIso(pCal.time)
            val pastDayKey = DateUtils.getDayOfWeekKey(pastDateIso)
            val habitsForDay = list.filter { it.habit.isScheduledForDay(pastDayKey) }
            if (habitsForDay.isNotEmpty()) {
                val allDone = habitsForDay.all { h ->
                    completedLogsSet.contains("${h.habit.id}_$pastDateIso")
                }
                if (allDone) {
                    perfectDays++
                }
            }
        }

        StatsSummary(
            totalHabits = total,
            completedToday = completedToday,
            todayScheduledCount = scheduledToday.size,
            todayPercent = todayPct,
            bestStreak = bestStreak,
            bestStreakHabitName = bestName,
            averageCompletionRate = avgRate,
            totalCheckins = totalCheckins,
            perfectDaysCount = perfectDays
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsSummary()
    )

    fun toggleTodayCompletion(habitId: Long) {
        viewModelScope.launch {
            val todayIso = DateUtils.getTodayIso()
            repository.toggleHabitCompletion(habitId, todayIso)
        }
    }

    fun toggleDateCompletion(habitId: Long, dateIso: String) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId, dateIso)
        }
    }

    fun saveHabit(habit: Habit) {
        viewModelScope.launch {
            if (habit.id == 0L) {
                repository.insertHabit(habit)
            } else {
                repository.updateHabit(habit)
            }
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            repository.deleteHabit(habitId)
            if (selectedHabitId.value == habitId) {
                selectedHabitId.value = null
            }
        }
    }

    fun getLogsForHabit(habitId: Long) = repository.getLogsForHabit(habitId)

    fun seedDemoData() {
        viewModelScope.launch {
            repository.clearAll()
            repository.seedDemoDataIfEmpty()
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }
}
