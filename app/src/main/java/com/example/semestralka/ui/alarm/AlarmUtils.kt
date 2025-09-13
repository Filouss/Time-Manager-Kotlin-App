package com.example.semestralka.ui.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.example.semestralka.data.local.Activity
import java.time.ZoneId

object AlarmUtils {
    fun cancelAlarm(context: Context, newActivity: Activity) {
        val intent = Intent(context, ActivityNotification::class.java).apply {
            putExtra("activityName", newActivity.name)
            putExtra("reminderDate", "No reminder")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            newActivity.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleAlarm(context: Context, newActivity: Activity, reminder: String) {
        //get the time to schedule the alarm
        val reminderTime = newActivity.reminder ?: return
        val intent = Intent(context, ActivityNotification::class.java).apply {
            putExtra("activityName", newActivity.name)
            putExtra("reminderDate", reminder)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            newActivity.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val timeToTrigger = reminderTime
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

        //check for permissions
        val canSchedule = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else{
            true
        }

        //schedule the alarm
        if (canSchedule){
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, timeToTrigger,pendingIntent
            )
        } else {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
        }

    }
}