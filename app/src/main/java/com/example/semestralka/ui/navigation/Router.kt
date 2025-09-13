package com.example.semestralka.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.semestralka.R
import com.example.semestralka.ui.screens.ActivityScreen
import com.example.semestralka.ui.screens.AddActivityScreen
import com.example.semestralka.ui.screens.CalendarScreen
import com.example.semestralka.ui.screens.DayScreen
import com.example.semestralka.ui.screens.SearchScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Router() {
    val navController = rememberNavController()

    MainAppRouter(
        navController
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainAppRouter(navController: NavHostController){
    val currentBackStackEntry = navController.currentBackStackEntryAsState()

    val mainBottomNavItems = remember {
        listOf(
            BottomNavItem(
                route = Routes.Calendar::class.qualifiedName.toString(),
                label = "Calendar",
                iconId = R.drawable.calendar_foreground,
                contentDescription = "calendar bottom nav bar item",
                onClick = {
                    navigateTo(navController, Routes.Calendar)
                }
            ),
            BottomNavItem(
                route = Routes.AddActivity::class.qualifiedName.toString(),
                label = "Add Activity",
                iconId = R.drawable.plus_foreground,
                contentDescription = "Add activity bottom nav bar item",
                onClick = {
                    //when adding an activity no id is passed
                    navigateTo(navController, Routes.AddActivity(activityId = null))
                }
            ),
            BottomNavItem(
                route = Routes.Search::class.qualifiedName.toString(),
                label = "Search",
                iconId = R.drawable.search_foreground,
                contentDescription = "Search icon",
                onClick = {
                    navigateTo(navController, Routes.Search)
                }
            ),
        )
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Calendar,
    ){
        composable<Routes.AddActivity>() {
            AddActivityScreen(
                mainBottomNavItems,
                currentBackStackEntry.value?.destination?.route,
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<Routes.Calendar>() {
            CalendarScreen(
                mainBottomNavItems,
                onItemClick = { selectedDate ->
                    navController.navigate(Routes.DayView(selectedDate.toString()))
                },
                currentBackStackEntry.value?.destination?.route,
            )
        }
        composable<Routes.DayView>() {backStackEntry ->
            val route = backStackEntry.toRoute<Routes.DayView>()
            DayScreen(
                mainBottomNavItems = mainBottomNavItems,
                stringDate = route.date,
                onActivityClick = {
                    navController.navigate(Routes.ActivityScreen(it))
                },
                navigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable<Routes.ActivityScreen>() {backStackEntry ->
            val route = backStackEntry.toRoute<Routes.ActivityScreen>()
            val id = route.activityId
            if (id != null) {
                ActivityScreen(
                    mainBottomNavItems = mainBottomNavItems,
                    onStartEdit = {
                        navController.navigate(Routes.AddActivity(it))
                    },
                    navigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        composable<Routes.Search>() {
            SearchScreen(
                mainBottomNavItems = mainBottomNavItems,
                onItemClick = {
                    navController.navigate(Routes.ActivityScreen(it))
                },
                navigateBack = {
                    navController.popBackStack()
                },
                currentDestination = currentBackStackEntry.value?.destination?.route,
            )
        }
    }
}

fun navigateTo(navController: NavHostController, route: Routes){
    //because of save state, handle the navigation from dayscreen and activity screen to calendar
    if (route.route == "Calendar"){
        val startDestination = navController.graph.startDestinationRoute
        if (startDestination != null) {
            navController.navigate(startDestination)
        }
    }
    navController.navigate(route) {
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}