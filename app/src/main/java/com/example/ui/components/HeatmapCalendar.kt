package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.data.model.HabitLog
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.OrangePrimary
import com.example.util.DateUtils
import java.util.Calendar

@Composable
fun HeatmapCalendar(
    habit: Habit,
    logs: List<HabitLog>,
    onToggleDate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedView by remember { mutableIntStateOf(0) } // 0: Contribution Grid, 1: Monthly View
    val completedDates = remember(logs) {
        logs.filter { it.completed }.map { it.date }.toSet()
    }

    val habitColor = try {
        Color(android.graphics.Color.parseColor(habit.colorHex))
    } catch (e: Exception) {
        GreenSuccess
    }

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
            // Header with Tab Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Activity Heatmap",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Compact Tab Selector
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(2.dp)
                ) {
                    val gridSelected = selectedView == 0
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (gridSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { selectedView = 0 }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Grid",
                            fontSize = 12.sp,
                            fontWeight = if (gridSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (gridSelected) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val monthSelected = selectedView == 1
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (monthSelected) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .clickable { selectedView = 1 }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Month",
                            fontSize = 12.sp,
                            fontWeight = if (monthSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (monthSelected) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedView == 0) {
                // GitHub-style contribution grid
                ContributionGridView(
                    habit = habit,
                    completedDates = completedDates,
                    habitColor = habitColor,
                    onToggleDate = onToggleDate
                )
            } else {
                // Monthly Calendar View
                MonthlyCalendarView(
                    habit = habit,
                    completedDates = completedDates,
                    habitColor = habitColor,
                    onToggleDate = onToggleDate
                )
            }
        }
    }
}

@Composable
private fun ContributionGridView(
    habit: Habit,
    completedDates: Set<String>,
    habitColor: Color,
    onToggleDate: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val totalWeeks = 14
    val daysList = remember { DateUtils.getHeatmapGridDays(weeks = totalWeeks) }
    val todayIso = remember { DateUtils.getTodayIso() }

    // Scroll to end (current week) on initial display
    androidx.compose.runtime.LaunchedEffect(Unit) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Day of week labels on left (M, W, F)
            Column(
                modifier = Modifier.padding(end = 6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEachIndexed { index, day ->
                    Box(
                        modifier = Modifier.size(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index == 0 || index == 2 || index == 4) {
                            Text(
                                text = day,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 14 columns of weeks, each with 7 rows
            for (week in 0 until totalWeeks) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 2.dp)
                ) {
                    for (dayOfWeek in 0..6) {
                        val index = week * 7 + dayOfWeek
                        if (index < daysList.size) {
                            val isoDate = daysList[index]
                            val isCompleted = completedDates.contains(isoDate)
                            val dayKey = DateUtils.getDayOfWeekKey(isoDate)
                            val isScheduled = habit.isScheduledForDay(dayKey)
                            val isToday = isoDate == todayIso

                            val cellColor = when {
                                isCompleted -> GreenSuccess
                                isScheduled -> MaterialTheme.colorScheme.surfaceVariant
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            }

                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(cellColor)
                                    .then(
                                        if (isToday) Modifier.border(
                                            1.5.dp,
                                            OrangePrimary,
                                            RoundedCornerShape(4.dp)
                                        ) else Modifier
                                    )
                                    .clickable {
                                        onToggleDate(isoDate)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Heatmap Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Less",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(GreenSuccess.copy(alpha = 0.5f))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(GreenSuccess)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Done",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MonthlyCalendarView(
    habit: Habit,
    completedDates: Set<String>,
    habitColor: Color,
    onToggleDate: (String) -> Unit
) {
    val monthCells = remember { DateUtils.getMonthDays(0) }
    val todayIso = remember { DateUtils.getTodayIso() }
    val monthHeader = remember { DateUtils.formatMonthYear(java.util.Date()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = monthHeader,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Day of week headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { header ->
                Text(
                    text = header,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid rows of 7 days
        val rows = monthCells.chunked(7)
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (cell in row) {
                    if (cell.dayNumber > 0) {
                        val isCompleted = completedDates.contains(cell.isoDate)
                        val isToday = cell.isoDate == todayIso
                        val dayKey = DateUtils.getDayOfWeekKey(cell.isoDate)
                        val isScheduled = habit.isScheduledForDay(dayKey)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isCompleted -> GreenSuccess
                                        isScheduled -> MaterialTheme.colorScheme.surfaceVariant
                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                    }
                                )
                                .then(
                                    if (isToday) Modifier.border(
                                        2.dp,
                                        OrangePrimary,
                                        RoundedCornerShape(8.dp)
                                    ) else Modifier
                                )
                                .clickable {
                                    onToggleDate(cell.isoDate)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = cell.dayNumber.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        // Empty spacer cell before the 1st
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                // Fill remainder if row has less than 7 cells
                for (i in row.size until 7) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
