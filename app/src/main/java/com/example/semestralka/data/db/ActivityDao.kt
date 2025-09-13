package com.example.semestralka.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity ORDER BY id ASC ")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activity WHERE id= :id LIMIT 1")
    suspend fun getActivityById(id: Long): ActivityEntity?

    @Query("SELECT * FROM activity WHERE date = :date")
    fun getActivitiesForDate(date: LocalDate): Flow<List<ActivityEntity>>

    //return the id so that the alarm can be canceled
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: ActivityEntity): Long

    @Delete
    suspend fun delete(activity: ActivityEntity)

    @Query("DELETE FROM activity")
    suspend fun clearAll()

    @Query("SELECT * FROM activity WHERE name LIKE '%' || :string || '%'")
    fun getActivitiesByName(string: String): Flow<List<ActivityEntity>>


}