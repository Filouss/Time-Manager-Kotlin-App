package com.example.semestralka.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semestralka.R
import com.example.semestralka.data.local.Activity
import com.example.semestralka.ui.alarm.AlarmUtils.cancelAlarm
import com.example.semestralka.ui.alarm.AlarmUtils.scheduleAlarm
import com.example.semestralka.ui.components.MainBottomNavigation
import com.example.semestralka.ui.navigation.BottomNavItem
import com.example.semestralka.ui.viewmodels.ActivityEditorEvent
import com.example.semestralka.ui.viewmodels.ActivityEditorViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddActivityScreen(
    mainBottomNavItems: List<BottomNavItem>,
    currentDestination: String?,
    navigateBack: () -> Unit,
    editorViewModel: ActivityEditorViewModel = viewModel(),
){
    //get the activity from viewmodel
    val activity by editorViewModel.activity.collectAsStateWithLifecycle()

    val isEditMode = activity.id != 0L

    Scaffold (
        topBar = { AddTopBar(isEditMode,navigateBack) },
        bottomBar = {MainBottomNavigation(mainBottomNavItems,currentDestination)},
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        AddContent(modifier = Modifier.padding(innerPadding),activity,editorViewModel,navigateBack,isEditMode)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopBar(
    isEditMode: Boolean,
    navigateBack: () -> Unit
){
    TopAppBar(
        modifier = Modifier.padding(top = 5.dp),
        title = {
            if(!isEditMode){
                Text("Add Activity", fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
            } else {
                Text("Edit Activity", fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
            }
        },
        navigationIcon = {
            IconButton(
                onClick = navigateBack,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.top_back_arrow_foreground),
                    contentDescription = "Navigate to previous screen"
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContent(
    modifier: Modifier,
    activity: Activity,
    editorViewModel: ActivityEditorViewModel,
    navigateBack: () -> Unit,
    isEditMode: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    val showDatePicker = remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(activity.date) }
    val context = LocalContext.current


    val reminderOptions = listOf(
        "30 minutes",
        "1 hour",
        "1 day",
        "3 days",
        "No reminder"
    )
    var selectedReminder by remember { mutableStateOf(reminderOptions.last()) }
    var selectedStartTime by remember {
        mutableStateOf(activity.start)
    }
    var selectedEndTime by remember {
        mutableStateOf(activity.end)
    }

    var showTimePickers by remember { mutableStateOf(false) }

    //set the time picker to activity starrt and end if editing
    var timePickerStateStart by remember {
        mutableStateOf(
            TimePickerState(
                initialHour = activity.start.hour,
                initialMinute = activity.start.minute,
                is24Hour = true
            )
        )
    }

    var timePickerStateEnd by remember {
        mutableStateOf(
            TimePickerState(
                initialHour = activity.end.hour,
                initialMinute = activity.end.minute,
                is24Hour = true
            )
        )
    }

    //set the date picker to activity date if editing
    val datePickerState = remember {
        mutableStateOf(
            DatePickerState(
                initialSelectedDateMillis = activity.date.atTime(12,0)
                    .atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli(),
                initialDisplayMode = DisplayMode.Input,
                locale = Locale.getDefault()
            )
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = activity.name,
            onValueChange = {
                editorViewModel.onEvent(ActivityEditorEvent.NameChanged(it))
            },
            label = { Text("Activity Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedButton(
            onClick = {
                showDatePicker.value = true
                datePickerState.value = DatePickerState(
                    initialSelectedDateMillis = activity.date.atTime(12,0)
                        .atZone(ZoneId.systemDefault())
                        .toInstant().toEpochMilli(),
                    initialDisplayMode = DisplayMode.Input,
                    locale = Locale.getDefault()
                )
                      selectedDate = activity.date
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Text("Selected date: ${activity.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}")
        }

        if (showDatePicker.value) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker.value = false
                        selectedDate = Instant.ofEpochMilli(datePickerState.value.selectedDateMillis!!)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        // Update the activity date in the editorViewModel
                        editorViewModel.onEvent(ActivityEditorEvent.DateChanged(selectedDate))
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker.value = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState.value)
            }
        }

        OutlinedButton(
            onClick = {
                // Update the time pickers before showing them
                timePickerStateStart = TimePickerState(
                    initialHour = activity.start.hour,
                    initialMinute = activity.start.minute,
                    is24Hour = true
                )
                timePickerStateEnd = TimePickerState(
                    initialHour = activity.end.hour,
                    initialMinute = activity.end.minute,
                    is24Hour = true
                )
                showTimePickers = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Selected time: ${activity.start.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${activity.end.format(DateTimeFormatter.ofPattern("HH:mm"))}")
        }

        if (showTimePickers) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Start Time")
                TimeInput(state = timePickerStateStart)

                Text(text = "End Time")
                TimeInput(state = timePickerStateEnd)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        selectedStartTime = LocalTime.of(timePickerStateStart.hour, timePickerStateStart.minute)
                        selectedEndTime = LocalTime.of(timePickerStateEnd.hour, timePickerStateEnd.minute)
                        editorViewModel.onEvent(ActivityEditorEvent.StartTimeChanged(selectedStartTime))
                        editorViewModel.onEvent(ActivityEditorEvent.EndTimeChanged(selectedEndTime))
                        showTimePickers = false
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Confirm Time")
                }
            }
        }

        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Set a reminder")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                reminderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedReminder = option
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = activity.notes ?: "",
            onValueChange = { editorViewModel.onEvent(ActivityEditorEvent.NotesChanged(it))},
            label = { Text("notes") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5
        )

        val coroutineScope = rememberCoroutineScope()
        Button(
            onClick = {
                if (activity.name.isNotBlank()) {
                    coroutineScope.launch {
                        val newActivity = editorViewModel.addActivity(
                            name = activity.name,
                            start = activity.start,
                            end = activity.end,
                            date = activity.date,
                            reminder = selectedReminder,
                            notes = activity.notes.toString(),
                        )
                        if (!isEditMode){
                            if (selectedReminder != "No reminder"){
                                scheduleAlarm(context, newActivity, selectedReminder)
                            }
                        } else {
                            if(selectedReminder == "No reminder"){
                                //cancel
                                cancelAlarm(context, newActivity)
                            } else {
//                              reschedule
                                cancelAlarm(context, newActivity)
                                scheduleAlarm(context,newActivity,selectedReminder)
                            }
                        }
                        navigateBack()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Confirm")
        }

        }

}
