package com.example.semestralka.ui.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.semestralka.AppContainer
import com.example.semestralka.data.datasource.ActivityDbDataSource
import com.example.semestralka.data.datasource.ActivityRemoteDatasource
import com.example.semestralka.data.local.Activity
import com.example.semestralka.data.remote.ActivityWebApi
import com.example.semestralka.data.repository.ActivityRepository
import com.example.semestralka.ui.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class ActivityEditorEvent{
    data class NameChanged(val newName: String): ActivityEditorEvent()
    data class NotesChanged(val newNotes: String): ActivityEditorEvent()
    data class ReminderChanged(val newReminder: LocalDateTime?): ActivityEditorEvent()
    data class DateChanged(val newDate: LocalDate): ActivityEditorEvent()
    data class StartTimeChanged(val newStart: LocalTime): ActivityEditorEvent()
    data class EndTimeChanged(val newEnd: LocalTime): ActivityEditorEvent()
}

@RequiresApi(Build.VERSION_CODES.O)
class ActivityEditorViewModel (
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){
    private val route: Routes.AddActivity = savedStateHandle.toRoute()
    private val id = route.activityId

    private val repository = ActivityRepository(
        activityDbDataSource = ActivityDbDataSource(
            activityDao = AppContainer.appDatabase.ActivityDao()
        ),
        activityRemoteDatasource = ActivityRemoteDatasource(
            activityWebApi = ActivityWebApi.getHolidayApiService()
        )
    )
    //activity placeholder
    private val _activity = MutableStateFlow(
        Activity(
            id = 0L,
            name = "",
            start = LocalTime.of(9, 0),
            end = LocalTime.of(10, 0),
            date = LocalDate.now(),
            reminder = null,
            notes = ""
        )
    )
    val activity = _activity.asStateFlow()

    init {
        viewModelScope.launch {
            id?.let {
                val loadedActivity = repository.getActivityById(it)
                if (loadedActivity != null) {
                    _activity.value = loadedActivity
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateReminder(textVal: String, start: LocalTime,date: LocalDate) : LocalDateTime?{
        val activityDateTime = LocalDateTime.of(date, start)

        return when (textVal) {
            "30 minutes" -> activityDateTime.minusMinutes(30)
            "1 hour" -> activityDateTime.minusHours(1)
            "1 day" -> activityDateTime.minusDays(1)
            "3 days" -> activityDateTime.minusDays(3)
            "No reminder" -> null
            else -> null
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun addActivity(name: String, start: LocalTime, end: LocalTime, date: LocalDate, reminder: String, notes: String) : Activity{

            val current = _activity.value
            val newActivity = Activity(
                id = current.id,
                name = name,
                start = start,
                end = end,
                date = date,
                reminder = calculateReminder(reminder,start,date),
                notes = notes,
            )

            val activityId = repository.insert(newActivity)

            newActivity.id = activityId
            //return the id to be able to pass it for alarms
            return newActivity
    }


    fun onEvent(event: ActivityEditorEvent) {
        _activity.update { current ->
            when (event) {
                is ActivityEditorEvent.NameChanged -> current.copy(name = event.newName)
                is ActivityEditorEvent.NotesChanged -> current.copy(notes = event.newNotes)
                is ActivityEditorEvent.ReminderChanged -> current.copy(reminder = event.newReminder)
                is ActivityEditorEvent.DateChanged -> current.copy(date = event.newDate)
                is ActivityEditorEvent.StartTimeChanged -> current.copy(start = event.newStart)
                is ActivityEditorEvent.EndTimeChanged -> current.copy(end = event.newEnd)
            }
        }
    }

}