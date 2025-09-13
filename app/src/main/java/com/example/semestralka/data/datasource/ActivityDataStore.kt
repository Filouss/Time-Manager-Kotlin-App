package com.example.semestralka.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first


private val Context.dataStore by preferencesDataStore(name = "settings")

object ActivityDataStore {
    private val HOLIDAYS_FETCHED_KEY = booleanPreferencesKey("holidays_fetched")

    suspend fun hasFetchedHolidays(context: Context): Boolean {
        //check if holidays activites were fetched and created
        val preferences = context.dataStore.data.first()
        return preferences[HOLIDAYS_FETCHED_KEY] ?: false
    }

}