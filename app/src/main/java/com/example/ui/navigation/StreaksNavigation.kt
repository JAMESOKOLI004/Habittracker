package com.example.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.screens.AddEditHabitScreen
import com.example.ui.screens.HabitDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.OrangePrimary
import com.example.viewmodel.HabitViewModel

enum class MainTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Habits", Icons.Filled.LocalFireDepartment, Icons.Outlined.LocalFireDepartment),
    STATS("Stats", Icons.Filled.BarChart, Icons.Outlined.BarChart),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
}

enum class ScreenState {
    MAIN_TABS,
    ADD_EDIT,
    DETAIL
}

@Composable
fun StreaksApp(
    viewModel: HabitViewModel,
    modifier: Modifier = Modifier
) {
    var currentScreenState by rememberSaveable { mutableStateOf(ScreenState.MAIN_TABS) }
    var currentTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    var activeHabitId by rememberSaveable { mutableLongStateOf(0L) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (currentScreenState == ScreenState.MAIN_TABS) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    MainTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = OrangePrimary,
                                selectedTextColor = OrangePrimary,
                                indicatorColor = OrangePrimary.copy(alpha = 0.15f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreenState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screenTransition"
            ) { screen ->
                when (screen) {
                    ScreenState.MAIN_TABS -> {
                        when (currentTab) {
                            MainTab.HOME -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToAddHabit = {
                                        activeHabitId = 0L
                                        currentScreenState = ScreenState.ADD_EDIT
                                    },
                                    onNavigateToHabitDetail = { id ->
                                        activeHabitId = id
                                        currentScreenState = ScreenState.DETAIL
                                    }
                                )
                            }
                            MainTab.STATS -> {
                                StatsScreen(viewModel = viewModel)
                            }
                            MainTab.SETTINGS -> {
                                SettingsScreen(viewModel = viewModel)
                            }
                        }
                    }
                    ScreenState.ADD_EDIT -> {
                        AddEditHabitScreen(
                            habitId = if (activeHabitId > 0L) activeHabitId else null,
                            viewModel = viewModel,
                            onNavigateBack = {
                                currentScreenState = ScreenState.MAIN_TABS
                            }
                        )
                    }
                    ScreenState.DETAIL -> {
                        HabitDetailScreen(
                            habitId = activeHabitId,
                            viewModel = viewModel,
                            onNavigateBack = {
                                currentScreenState = ScreenState.MAIN_TABS
                            },
                            onNavigateToEdit = { id ->
                                activeHabitId = id
                                currentScreenState = ScreenState.ADD_EDIT
                            }
                        )
                    }
                }
            }
        }
    }
}
