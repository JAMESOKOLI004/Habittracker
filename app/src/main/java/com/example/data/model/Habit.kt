package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconName: String = "flame",
    val colorHex: String = "#F97316",
    val frequencyType: String = "DAILY", // "DAILY" or "WEEKDAYS"
    val selectedDays: String = "MON,TUE,WED,THU,FRI,SAT,SUN",
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "08:00 AM",
    val createdAt: Long = System.currentTimeMillis(),
    val archived: Boolean = false
) {
    fun isScheduledForDay(dayKey: String): Boolean {
        if (frequencyType == "DAILY") return true
        val days = selectedDays.split(",").map { it.trim().uppercase() }
        return days.contains(dayKey.uppercase())
    }
}
