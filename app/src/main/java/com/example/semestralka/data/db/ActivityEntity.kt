package com.example.semestralka.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(tableName = "activity")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = "",
    val start: LocalTime,
    val end: LocalTime,
    val date: LocalDate,
    val reminder: LocalDateTime? = null,
    val notes: String? = null,
)