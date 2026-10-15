package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.OrangePrimary
import com.example.util.HabitIconUtils
import com.example.viewmodel.HabitViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditHabitScreen(
    habitId: Long?,
    viewModel: HabitViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditing = habitId != null && habitId != 0L

    var name by remember { mutableStateOf("") }
    var iconName by remember { mutableStateOf("flame") }
    var colorHex by remember { mutableStateOf("#F97316") }
    var frequencyType by remember { mutableStateOf("DAILY") }
    var selectedDays by remember { mutableStateOf("MON,TUE,WED,THU,FRI,SAT,SUN") }
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf("08:00 AM") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    LaunchedEffect(habitId) {
        if (isEditing && habitId != null) {
            val habit = viewModel.habitsWithStats.value.find { it.habit.id == habitId }?.habit
            if (habit != null) {
                name = habit.name
                iconName = habit.iconName
                colorHex = habit.colorHex
                frequencyType = habit.frequencyType
                selectedDays = habit.selectedDays
                reminderEnabled = habit.reminderEnabled
                reminderTime = habit.reminderTime
            }
        }
    }

    val selectedColor = remember(colorHex) {
        try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (e: Exception) {
            OrangePrimary
        }
    }

    val daysList = listOf(
        "MON" to "M",
        "TUE" to "T",
        "WED" to "W",
        "THU" to "T",
        "FRI" to "F",
        "SAT" to "S",
        "SUN" to "S"
    )

    val popularSuggestions = listOf(
        "Drink 2.5L Water",
        "Morning Jog",
        "Read 20 Mins",
        "Meditation",
        "Daily Coding",
        "Eat Clean Food",
        "Workout 45 Mins",
        "Sleep by 11 PM"
    )

    val reminderPresets = listOf("07:00 AM", "08:00 AM", "12:00 PM", "06:30 PM", "08:30 PM", "09:30 PM")

    if (showDeleteConfirm && habitId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Habit?") },
            text = { Text("Are you sure you want to delete '$name'? All past streak history will be permanently deleted.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteHabit(habitId)
                        showDeleteConfirm = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Habit" else "New Habit",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (isEditing && habitId != null) {
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Habit",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Habit Preview Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(selectedColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = HabitIconUtils.getIconByName(iconName),
                            contentDescription = null,
                            tint = selectedColor,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = if (name.isBlank()) "Habit Name" else name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (name.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (frequencyType == "DAILY") "Every Day" else "Specific days: $selectedDays",
                            fontSize = 12.sp,
                            color = OrangePrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Habit Name Input
            Text(
                text = "Habit Name",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                placeholder = { Text("e.g., Drink 2.5L Water") },
                isError = nameError,
                supportingText = if (nameError) {
                    { Text("Habit name cannot be empty") }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("habit_name_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                singleLine = true
            )

            // Suggestions Chips
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                popularSuggestions.forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { name = suggestion }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = suggestion,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Icon Picker
            Text(
                text = "Select Icon",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HabitIconUtils.availableIcons.forEach { iconItem ->
                    val isSelected = iconItem.id == iconName
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) selectedColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) selectedColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { iconName = iconItem.id },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconItem.icon,
                            contentDescription = iconItem.name,
                            tint = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Color Picker
            Text(
                text = "Color Theme",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HabitIconUtils.availableColors.forEach { hex ->
                    val parsedColor = Color(android.graphics.Color.parseColor(hex))
                    val isSelected = colorHex.equals(hex, ignoreCase = true)

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(parsedColor)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { colorHex = hex },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Frequency Picker
            Text(
                text = "Frequency",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(3.dp)
            ) {
                val dailyActive = frequencyType == "DAILY"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (dailyActive) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { frequencyType = "DAILY" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Every Day",
                        fontWeight = if (dailyActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (dailyActive) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                val customActive = frequencyType == "WEEKDAYS"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (customActive) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .clickable { frequencyType = "WEEKDAYS" }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Specific Weekdays",
                        fontWeight = if (customActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (customActive) OrangePrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (frequencyType == "WEEKDAYS") {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val currentDaysSet = selectedDays.split(",").map { it.trim().uppercase() }.toMutableSet()

                    daysList.forEach { (key, label) ->
                        val isDaySelected = currentDaysSet.contains(key)
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isDaySelected) OrangePrimary else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    1.dp,
                                    if (isDaySelected) OrangePrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                    CircleShape
                                )
                                .clickable {
                                    if (isDaySelected) {
                                        if (currentDaysSet.size > 1) {
                                            currentDaysSet.remove(key)
                                        }
                                    } else {
                                        currentDaysSet.add(key)
                                    }
                                    selectedDays = currentDaysSet.joinToString(",")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontWeight = FontWeight.Bold,
                                color = if (isDaySelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reminder Time Toggle
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Daily Reminder",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (reminderEnabled) "Notify at $reminderTime" else "Turn on reminder",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = OrangePrimary)
                        )
                    }

                    if (reminderEnabled) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Choose Reminder Time",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            reminderPresets.forEach { preset ->
                                val isSelected = reminderTime == preset
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isSelected) OrangePrimary else MaterialTheme.colorScheme.surfaceVariant
                                        )
                                        .clickable { reminderTime = preset }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = preset,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Save Habit Button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        val habit = Habit(
                            id = habitId ?: 0L,
                            name = name.trim(),
                            iconName = iconName,
                            colorHex = colorHex,
                            frequencyType = frequencyType,
                            selectedDays = selectedDays,
                            reminderEnabled = reminderEnabled,
                            reminderTime = reminderTime
                        )
                        viewModel.saveHabit(habit)
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("save_habit_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(
                    text = if (isEditing) "Save Changes" else "Save Habit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
