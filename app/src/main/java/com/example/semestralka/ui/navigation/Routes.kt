package com.example.semestralka.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes (val route: String){
    @Serializable
    data class ActivityScreen(val activityId: Long? = null) : Routes("ActivityScreen")
    @Serializable
    data object Calendar : Routes("Calendar")
    @Serializable
    data class DayView(val date: String) : Routes("DayView")
    @Serializable
    data object Search : Routes("Search")
    @Serializable
    data class AddActivity(val activityId: Long? = null) : Routes("AddActivity")
}