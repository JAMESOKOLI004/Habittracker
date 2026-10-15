package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitWithStats
import com.example.ui.components.HabitCard
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.OrangePrimaryDark
import com.example.util.DateUtils
import com.example.viewmodel.HabitFilter
import com.example.viewmodel.HabitViewModel

@Composable
fun HomeScreen(
    viewModel: HabitViewModel,
    onNavigateToAddHabit: () -> Unit,
    onNavigateToHabitDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val habitsWithStats by viewModel.habitsWithStats.collectAsState()
    val statsSummary by viewModel.statsSummary.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    val todayHeader = remember { DateUtils.getTodayDisplay() }
    val todayIso = remember { DateUtils.getTodayIso() }
    val todayDayKey = remember { DateUtils.getDayOfWeekKey(todayIso) }

    val filteredHabits = remember(habitsWithStats, selectedFilter) {
        when (selectedFilter) {
            HabitFilter.ALL -> habitsWithStats
            HabitFilter.PENDING -> habitsWithStats.filter {
                it.habit.isScheduledForDay(todayDayKey) && !it.isCompletedToday
            }
            HabitFilter.COMPLETED -> habitsWithStats.filter {
                it.habit.isScheduledForDay(todayDayKey) && it.isCompletedToday
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddHabit,
                containerColor = OrangePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .testTag("add_habit_fab")
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Habit",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Section: Today's Date & Greeting
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = todayHeader.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Streaks+",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        // Top Streak Banner
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(OrangePrimary.copy(alpha = 0.12f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${statsSummary.bestStreak}d best",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = OrangePrimaryDark
                            )
                        }
                    }
                }
            }

            // Daily Motivation & Progress Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (statsSummary.todayPercent == 100 && statsSummary.todayScheduledCount > 0) {
                                    "All habits crushed today! 🎉"
                                } else if (statsSummary.todayPercent >= 50) {
                                    "Great momentum! Keep it up 🔥"
                                } else {
                                    "Build your daily streak today"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "${statsSummary.completedToday} of ${statsSummary.todayScheduledCount} completed • ${statsSummary.todayPercent}%",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Circular Progress Indicator
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(54.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = {
                                    if (statsSummary.todayScheduledCount > 0) {
                                        statsSummary.completedToday.toFloat() / statsSummary.todayScheduledCount.toFloat()
                                    } else 0f
                                },
                                strokeWidth = 5.dp,
                                color = if (statsSummary.todayPercent == 100) GreenSuccess else OrangePrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxSize()
                            )
                            Text(
                                text = "${statsSummary.todayPercent}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Filter Chips Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HabitFilterChip(
                        label = "All (${habitsWithStats.size})",
                        selected = selectedFilter == HabitFilter.ALL,
                        onClick = { viewModel.selectedFilter.value = HabitFilter.ALL }
                    )

                    val pendingCount = habitsWithStats.count {
                        it.habit.isScheduledForDay(todayDayKey) && !it.isCompletedToday
                    }
                    HabitFilterChip(
                        label = "Pending ($pendingCount)",
                        selected = selectedFilter == HabitFilter.PENDING,
                        onClick = { viewModel.selectedFilter.value = HabitFilter.PENDING }
                    )

                    val completedCount = habitsWithStats.count {
                        it.habit.isScheduledForDay(todayDayKey) && it.isCompletedToday
                    }
                    HabitFilterChip(
                        label = "Done ($completedCount)",
                        selected = selectedFilter == HabitFilter.COMPLETED,
                        onClick = { viewModel.selectedFilter.value = HabitFilter.COMPLETED }
                    )
                }
            }

            // Empty State if no habits
            if (filteredHabits.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (selectedFilter == HabitFilter.COMPLETED) {
                                    Icons.Default.Celebration
                                } else {
                                    Icons.Default.SelfImprovement
                                },
                                contentDescription = null,
                                tint = OrangePrimary.copy(alpha = 0.6f),
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = when (selectedFilter) {
                                    HabitFilter.ALL -> "No habits added yet"
                                    HabitFilter.PENDING -> "No pending habits for today!"
                                    HabitFilter.COMPLETED -> "No habits completed today yet"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = when (selectedFilter) {
                                    HabitFilter.ALL -> "Tap + below to create your first streak habit"
                                    HabitFilter.PENDING -> "You're all caught up for today! 🔥"
                                    HabitFilter.COMPLETED -> "Tap the checkmark circle on any habit to complete it"
                                },
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // List of Habit Cards
                items(
                    items = filteredHabits,
                    key = { it.habit.id }
                ) { habitWithStats ->
                    HabitCard(
                        habitWithStats = habitWithStats,
                        onToggleToday = {
                            viewModel.toggleTodayCompletion(habitWithStats.habit.id)
                        },
                        onClickCard = {
                            onNavigateToHabitDetail(habitWithStats.habit.id)
                        }
                    )
                }
            }

            // Bottom Spacer for FAB
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun HabitFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) OrangePrimary else MaterialTheme.colorScheme.surface
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
        )
    }
}
