package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StreaksBarChart
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryDark
import com.example.viewmodel.HabitViewModel

data class HabitBadge(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean,
    val progressText: String
)

@Composable
fun StatsScreen(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    val habitsWithStats by viewModel.habitsWithStats.collectAsState()
    val statsSummary by viewModel.statsSummary.collectAsState()

    val badges = listOf(
        HabitBadge(
            title = "First Spark",
            description = "Complete any habit for 1 day",
            icon = Icons.Default.LocalFireDepartment,
            isUnlocked = statsSummary.bestStreak >= 1,
            progressText = if (statsSummary.bestStreak >= 1) "Unlocked" else "0/1 day"
        ),
        HabitBadge(
            title = "7-Day Warrior",
            description = "Maintain a 7-day streak",
            icon = Icons.Default.Stars,
            isUnlocked = statsSummary.bestStreak >= 7,
            progressText = if (statsSummary.bestStreak >= 7) "Unlocked" else "${statsSummary.bestStreak}/7 days"
        ),
        HabitBadge(
            title = "Fortnight Force",
            description = "Reach a 14-day streak",
            icon = Icons.Default.EmojiEvents,
            isUnlocked = statsSummary.bestStreak >= 14,
            progressText = if (statsSummary.bestStreak >= 14) "Unlocked" else "${statsSummary.bestStreak}/14 days"
        ),
        HabitBadge(
            title = "Monthly Master",
            description = "Reach a 30-day streak",
            icon = Icons.Default.EmojiEvents,
            isUnlocked = statsSummary.bestStreak >= 30,
            progressText = if (statsSummary.bestStreak >= 30) "Unlocked" else "${statsSummary.bestStreak}/30 days"
        ),
        HabitBadge(
            title = "Check-in Champion",
            description = "Log 50 total habit completions",
            icon = Icons.Default.FactCheck,
            isUnlocked = statsSummary.totalCheckins >= 50,
            progressText = if (statsSummary.totalCheckins >= 50) "Unlocked" else "${statsSummary.totalCheckins}/50 done"
        ),
        HabitBadge(
            title = "Consistency Pro",
            description = "Achieve 80%+ average completion rate",
            icon = Icons.Default.CheckCircle,
            isUnlocked = statsSummary.averageCompletionRate >= 80,
            progressText = if (statsSummary.averageCompletionRate >= 80) "Unlocked" else "${statsSummary.averageCompletionRate}% / 80%"
        )
    )

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Screen Header
            Column {
                Text(
                    text = "PERFORMANCE & INSIGHTS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Habit Stats",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Top Overview Cards (2x2 Grid)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Longest Streak",
                        value = "${statsSummary.bestStreak} Days",
                        subtitle = if (statsSummary.bestStreakHabitName.isNotBlank()) "on ${statsSummary.bestStreakHabitName}" else "No active streak",
                        icon = Icons.Default.LocalFireDepartment,
                        accentColor = OrangePrimary,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Avg Completion",
                        value = "${statsSummary.averageCompletionRate}%",
                        subtitle = "across all habits",
                        icon = Icons.Default.Percent,
                        accentColor = GreenSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total Habits",
                        value = "${statsSummary.totalHabits}",
                        subtitle = "Active routines",
                        icon = Icons.Default.FitnessCenter,
                        accentColor = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Check-ins Done",
                        value = "${statsSummary.totalCheckins}",
                        subtitle = "${statsSummary.perfectDaysCount} perfect days",
                        icon = Icons.Default.CheckCircle,
                        accentColor = Color(0xFF8B5CF6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Interactive Habits Comparison Bar Chart
            StreaksBarChart(habits = habitsWithStats)

            // Streak Milestones & Badges Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Streak Milestones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    badges.forEach { badge ->
                        BadgeRowItem(badge = badge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BadgeRowItem(badge: HabitBadge) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 1.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) OrangePrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = null,
                    tint = if (badge.isUnlocked) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = badge.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = badge.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (badge.isUnlocked) GreenSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badge.progressText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isUnlocked) GreenSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
