package com.example.semestralka.data.db;

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter;
import java.time.LocalDate;
import java.time.LocalDateTime
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RequiresApi(Build.VERSION_CODES.O)
class Converters {


    @TypeConverter
    fun fromLocalDate(localDate: LocalDate?): String? {
        return localDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    // String to LocalDate
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }
    }

    // LocalTime to String
    @TypeConverter
    fun fromLocalTime(localTime: LocalTime?): String? {
        return localTime?.toString()
    }

    // String to LocalTime
    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime?): String? {
        return localDateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    // String to LocalDateTime
    @TypeConverter
    fun toLocalDateTime(dateTimeString: String?): LocalDateTime? {
        return dateTimeString?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
    }
}
