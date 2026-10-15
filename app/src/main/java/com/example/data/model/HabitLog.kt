package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_logs",
    indices = [
        Index(value = ["habitId", "date"], unique = true),
        Index(value = ["date"])
    ]
)
data class HabitLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val date: String, // ISO format: yyyy-MM-dd
    val completed: Boolean = true,
    val completedAt: Long = System.currentTimeMillis()
)
