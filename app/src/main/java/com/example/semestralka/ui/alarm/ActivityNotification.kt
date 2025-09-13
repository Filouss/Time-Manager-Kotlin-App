package com.example.semestralka.ui.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.semestralka.MainActivity
import com.example.semestralka.R

class ActivityNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        //get the name and time to display in the notification
        val activityName = intent.getStringExtra("activityName") ?: "Reminder"
        val remainingTime = intent.getStringExtra("reminderDate") ?: "a bit"


        val notificationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        //open the app on clicking the notification
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, "activity_app_main_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Reminder for an activity")
            .setContentText("$activityName is starting in $remainingTime")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

}