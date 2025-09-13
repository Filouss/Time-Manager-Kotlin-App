package com.example.semestralka

import android.app.Application

class TimeManagerApp: Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any global resources or libraries here

        AppContainer.init(this)
        //clear all created activities
//        GlobalScope.launch {
//            AppContainer.appDatabase.ActivityDao().clearAll()
//        }
    }
}