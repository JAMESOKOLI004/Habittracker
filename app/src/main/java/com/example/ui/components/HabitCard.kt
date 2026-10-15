package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitWithStats
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.OrangePrimary
import com.example.util.HabitIconUtils

@Composable
fun HabitCard(
    habitWithStats: HabitWithStats,
    onToggleToday: () -> Unit,
    onClickCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val habit = habitWithStats.habit
    val isDone = habitWithStats.isCompletedToday

    // Parse custom habit color safely
    val habitColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (e: Exception) {
        OrangePrimary
    }

    // Animation for checkmark button
    val checkmarkScale by animateFloatAsState(
        targetValue = if (isDone) 1.0f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "checkScale"
    )

    val checkmarkBgColor by animateColorAsState(
        targetValue = if (isDone) GreenSuccess else Color.Transparent,
        label = "checkBgColor"
    )

    val checkmarkBorderColor by animateColorAsState(
        targetValue = if (isDone) GreenSuccess else MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
        label = "checkBorderColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_card_${habit.id}")
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClickCard),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Habit Icon + Name + Streak Badge + Checkmark Circle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Habit Icon with soft tinted background
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(habitColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = HabitIconUtils.getIconByName(habit.iconName),
                        contentDescription = habit.name,
                        tint = habitColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and streak row
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FlameStreakBadge(streakCount = habitWithStats.currentStreak)

                        if (habit.frequencyType == "WEEKDAYS") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• Scheduled",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Large Tappable Checkmark Circle (min touch target >= 48dp)
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("check_habit_${habit.id}")
                        .scale(checkmarkScale)
                        .clip(CircleShape)
                        .background(checkmarkBgColor)
                        .border(2.dp, checkmarkBorderColor, CircleShape)
                        .clickable(onClick = onToggleToday),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed Today",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        // Empty ring indicator enticing user to tap
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(OrangePrimary.copy(alpha = 0.25f))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Divider / Subtle line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Mini 7-day dot row
            MiniWeekDots(
                days = habitWithStats.recent7Days,
                habitColor = habitColor
            )
        }
    }
}
