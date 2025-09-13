package com.example.semestralka.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.semestralka.data.datasource.ActivityDbDataSource
import com.example.semestralka.data.datasource.ActivityRemoteDatasource
import com.example.semestralka.data.local.Activity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class ActivityRepository(
    private val activityDbDataSource: ActivityDbDataSource,
    private val activityRemoteDatasource: ActivityRemoteDatasource,
) {

    fun getActivitiesForDate(date: LocalDate): Flow<List<Activity>> {
        return activityDbDataSource.getActivitiesForDate(date)
    }

    suspend fun getActivityById(id: Long): Activity? {
        return activityDbDataSource.getActivityById(id)
    }

    fun getActivitiesForString(string: String): Flow<List<Activity>> {
        return activityDbDataSource.getActivitiesForString(string)
    }

    suspend fun insert(activity: Activity) : Long{
        return activityDbDataSource.insertActivity(activity)
    }


    suspend fun insertActivities(activities: List<Activity>) {
        for (activity in activities){
            activityDbDataSource.insertActivity(activity)
        }
    }

    suspend fun delete(id: Long) = activityDbDataSource.deleteActivity(id)

    suspend fun clearAllActivities() {
        activityDbDataSource.clearAll()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchHolidays() : ApiCallResult<List<Activity>>{
        return activityRemoteDatasource.fetchAndStoreHolidaysFromWeb()
    }
}