package com.example.semestralka.ui.viewmodels

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralka.AppContainer
import com.example.semestralka.data.datasource.ActivityDataStore
import com.example.semestralka.data.datasource.ActivityDbDataSource
import com.example.semestralka.data.datasource.ActivityRemoteDatasource
import com.example.semestralka.data.local.Activity
import com.example.semestralka.data.remote.ActivityWebApi
import com.example.semestralka.data.repository.ActivityRepository
import com.example.semestralka.data.repository.ApiCallResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate


@RequiresApi(Build.VERSION_CODES.O)
class ActivityViewModel(
    private val _state: MutableState<ApiCallResult<List<Activity>>> = mutableStateOf(ApiCallResult.Loading),
    val state: State<ApiCallResult<List<Activity>>> = _state
) : ViewModel() {

    private val repository = ActivityRepository(
        activityDbDataSource = ActivityDbDataSource(
            activityDao = AppContainer.appDatabase.ActivityDao()
        ),
        activityRemoteDatasource = ActivityRemoteDatasource(
            activityWebApi = ActivityWebApi.getHolidayApiService()
        )
    )

    fun getActivitiesByDate(date: LocalDate): Flow<List<Activity>> {
        return repository.getActivitiesForDate(date)
    }

    fun getActivitiesByString(string: String): Flow<List<Activity>> {
        return repository.getActivitiesForString(string)
    }

    suspend fun getActivityById(id : Long): Activity?{
        return repository.getActivityById(id)
    }

    suspend fun deleteActivity(activity: Activity) {
        repository.delete(activity.id)
    }

    fun delete(activity: Activity) {
        viewModelScope.launch {
            deleteActivity(activity)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchHolidaysAndStore(context: Context) {
        viewModelScope.launch {
            _state.value = ApiCallResult.Loading
            if (!ActivityDataStore.hasFetchedHolidays(context)) {
                when (val result = repository.fetchHolidays()) {
                    is ApiCallResult.Success -> {
                        repository.insertActivities(result.data)
                        _state.value = result
                    }
                    is ApiCallResult.Error -> {
                        _state.value = result
                    }
                    else -> {}
                }
            }
        }
    }
}