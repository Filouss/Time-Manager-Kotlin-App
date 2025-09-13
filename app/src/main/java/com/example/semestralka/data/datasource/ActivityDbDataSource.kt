package com.example.semestralka.data.datasource

import com.example.semestralka.data.db.ActivityDao
import com.example.semestralka.data.db.ActivityEntity
import com.example.semestralka.data.local.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class ActivityDbDataSource (
    private val  activityDao: ActivityDao
){

    fun getActivitiesForDate(date: LocalDate): Flow<List<Activity>> {
        return activityDao.getActivitiesForDate(date).map { activityEntities ->
            activityEntities.map { it.toActivity() }
        }
    }

    suspend fun getActivityById(id: Long): Activity? {
        return activityDao.getActivityById(id)?.toActivity()
    }

    fun getActivitiesForString(string: String): Flow<List<Activity>> {
        return activityDao.getActivitiesByName(string).map { activityEntities ->
            activityEntities.map { it.toActivity() }
        }
    }

    suspend fun insertActivity(activity: Activity) : Long{
        return activityDao.insert(activity.toActivityEntity())
    }

    suspend fun clearAll() {
        activityDao.clearAll()
    }

    suspend fun deleteActivity(id: Long) {
        val entity = activityDao.getActivityById(id)
        if (entity != null) {
            activityDao.delete(entity)
        }
    }


    fun ActivityEntity.toActivity(): Activity {
        return Activity(
            id = id,
            name = name,
            start = start,
            end = end,
            date = date,
            reminder = reminder,
            notes = notes
        )
    }

    fun Activity.toActivityEntity(): ActivityEntity {
        return ActivityEntity(
            id = id,
            name = name,
            start = start,
            end = end,
            date = date,
            reminder = reminder,
            notes = notes
        )
    }
}