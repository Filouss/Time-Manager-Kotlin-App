package com.example.semestralka.data.local

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Activity(
    var id: Long = 0,
    val name: String = "",
    val start: LocalTime,
    val end: LocalTime,
    val date: LocalDate,
    val reminder: LocalDateTime?,
    val notes: String? = null,
)
