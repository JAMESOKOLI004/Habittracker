package com.example.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

data class HabitIconItem(
    val id: String,
    val name: String,
    val icon: ImageVector
)

object HabitIconUtils {
    val availableIcons: List<HabitIconItem> = listOf(
        HabitIconItem("flame", "Flame", Icons.Default.LocalFireDepartment),
        HabitIconItem("water_drop", "Water", Icons.Default.WaterDrop),
        HabitIconItem("fitness_center", "Workout", Icons.Default.FitnessCenter),
        HabitIconItem("directions_run", "Running", Icons.Default.DirectionsRun),
        HabitIconItem("menu_book", "Reading", Icons.Default.MenuBook),
        HabitIconItem("code", "Coding", Icons.Default.Code),
        HabitIconItem("self_improvement", "Meditation", Icons.Default.SelfImprovement),
        HabitIconItem("bedtime", "Sleep", Icons.Default.Bedtime),
        HabitIconItem("restaurant", "Healthy Diet", Icons.Default.Restaurant),
        HabitIconItem("music_note", "Music", Icons.Default.MusicNote),
        HabitIconItem("edit_note", "Journal", Icons.Default.EditNote),
        HabitIconItem("directions_bike", "Cycling", Icons.Default.DirectionsBike),
        HabitIconItem("psychology", "Mindfulness", Icons.Default.Psychology),
        HabitIconItem("spa", "Self Care", Icons.Default.Spa),
        HabitIconItem("palette", "Creative", Icons.Default.Brush),
        HabitIconItem("savings", "Save Money", Icons.Default.Savings),
        HabitIconItem("favorite", "Health", Icons.Default.Favorite),
        HabitIconItem("star", "Goal", Icons.Default.Star)
    )

    fun getIconByName(name: String): ImageVector {
        return availableIcons.firstOrNull { it.id == name }?.icon ?: Icons.Default.LocalFireDepartment
    }

    val availableColors: List<String> = listOf(
        "#F97316", // Energetic Orange (brand)
        "#22C55E", // Success Green (brand)
        "#3B82F6", // Royal Blue
        "#8B5CF6", // Purple
        "#EC4899", // Pink
        "#EAB308", // Sun Yellow
        "#06B6D4", // Cyan
        "#EF4444", // Crimson Red
        "#14B8A6"  // Teal
    )
}
