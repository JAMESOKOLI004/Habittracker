package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitWithStats
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryDark
import com.example.util.HabitIconUtils

enum class ChartMetric {
    STREAK,
    COMPLETION_RATE
}

@Composable
fun StreaksBarChart(
    habits: List<HabitWithStats>,
    modifier: Modifier = Modifier
) {
    var selectedMetric by remember { mutableStateOf(ChartMetric.STREAK) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header + Metric Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Habit Comparison",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(2.dp)
                ) {
                    val streakActive = selectedMetric == ChartMetric.STREAK
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (streakActive) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { selectedMetric = ChartMetric.STREAK }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Streaks",
                            fontSize = 11.sp,
                            fontWeight = if (streakActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (streakActive) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val rateActive = selectedMetric == ChartMetric.COMPLETION_RATE
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (rateActive) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { selectedMetric = ChartMetric.COMPLETION_RATE }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Rate %",
                            fontSize = 11.sp,
                            fontWeight = if (rateActive) FontWeight.Bold else FontWeight.Medium,
                            color = if (rateActive) GreenSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (habits.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Add habits to see comparison charts",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            } else {
                // Find maximum for normalization
                val maxValue = remember(habits, selectedMetric) {
                    if (selectedMetric == ChartMetric.STREAK) {
                        (habits.maxOfOrNull { it.currentStreak } ?: 1).coerceAtLeast(5)
                    } else {
                        100
                    }
                }

                val scrollState = rememberScrollState()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    habits.forEach { habitItem ->
                        val value = if (selectedMetric == ChartMetric.STREAK) {
                            habitItem.currentStreak
                        } else {
                            habitItem.completionRatePercent
                        }

                        val barHeightFraction = (value.toFloat() / maxValue.toFloat()).coerceIn(0.08f, 1.0f)
                        val animatedFraction by animateFloatAsState(
                            targetValue = barHeightFraction,
                            animationSpec = tween(durationMillis = 600),
                            label = "barHeight"
                        )

                        val habitColor = try {
                            Color(android.graphics.Color.parseColor(habitItem.habit.colorHex))
                        } catch (e: Exception) {
                            if (selectedMetric == ChartMetric.STREAK) OrangePrimary else GreenSuccess
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(62.dp)
                        ) {
                            // Value text label above bar
                            Text(
                                text = if (selectedMetric == ChartMetric.STREAK) "${value}d" else "$value%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selectedMetric == ChartMetric.STREAK) OrangePrimaryDark else GreenSuccess
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Bar track
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(animatedFraction)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    habitColor,
                                                    habitColor.copy(alpha = 0.8f)
                                                )
                                            )
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Habit Icon
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(habitColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = HabitIconUtils.getIconByName(habitItem.habit.iconName),
                                    contentDescription = null,
                                    tint = habitColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Name label
                            Text(
                                text = habitItem.habit.name,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
