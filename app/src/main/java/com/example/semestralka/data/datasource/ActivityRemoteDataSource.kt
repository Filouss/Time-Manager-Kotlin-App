package com.example.semestralka.data.datasource

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.semestralka.data.local.Activity
import com.example.semestralka.data.remote.ActivityWebApi
import com.example.semestralka.data.remote.Holiday
import com.example.semestralka.data.repository.ApiCallResult
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
class ActivityRemoteDatasource(
    private val activityWebApi: ActivityWebApi,
) {
    suspend fun fetchAndStoreHolidaysFromWeb(): ApiCallResult<List<Activity>> {
        val response = activityWebApi.getHolidays()
        return if (response.isSuccessful) {
            response.body()?.let { holidays ->
                val activityList = holidays.map { holiday ->
                    holiday.toActivity()
                }
                ApiCallResult.Success(activityList)
            } ?: ApiCallResult.Error(IOException("Feature collection is null"))
        } else {
            ApiCallResult.Error(IOException("Failed to fetch holidays from web"))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Holiday.toActivity(): Activity {
    return Activity(
        id = date.hashCode().toLong(),
        name = name,
        start = LocalTime.of(0,0),
        end = LocalTime.of(23,59),
        date = LocalDate.parse(date),
        reminder = null,
        notes = localName,
    )
}