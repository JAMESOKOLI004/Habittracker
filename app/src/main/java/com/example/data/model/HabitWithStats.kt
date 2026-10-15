package com.example.data.model

data class DayStatus(
    val dateIso: String,
    val dayShort: String,
    val isScheduled: Boolean,
    val isCompleted: Boolean,
    val isToday: Boolean
)

data class HabitWithStats(
    val habit: Habit,
    val isCompletedToday: Boolean,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalCompletions: Int,
    val completionRatePercent: Int,
    val recent7Days: List<DayStatus>
)
