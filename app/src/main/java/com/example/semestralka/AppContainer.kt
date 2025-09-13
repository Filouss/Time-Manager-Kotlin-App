package com.example.semestralka

import android.content.Context
import com.example.semestralka.data.db.AppDatabase

object AppContainer {
    lateinit var appDatabase: AppDatabase
        private set

    fun init(context: Context){
        appDatabase = AppDatabase.getDatabase(context)
    }

}