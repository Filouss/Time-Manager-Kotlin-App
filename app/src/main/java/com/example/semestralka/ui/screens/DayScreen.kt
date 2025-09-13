package com.example.semestralka.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semestralka.R
import com.example.semestralka.data.local.Activity
import com.example.semestralka.ui.components.MainBottomNavigation
import com.example.semestralka.ui.navigation.BottomNavItem
import com.example.semestralka.ui.theme.SemestralkaTheme
import com.example.semestralka.ui.viewmodels.ActivityViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayScreen(
    mainBottomNavItems: List<BottomNavItem>,
    stringDate : String,
    viewModel: ActivityViewModel = viewModel(),
    onActivityClick: (Long) -> Unit,
    navigateBack: () -> Unit
){
    val date = LocalDate.parse(stringDate)
    //get the activities for the day
    val activities by viewModel.getActivitiesByDate(date).collectAsState(initial = emptyList())

    Scaffold (
        topBar = { DayTopBar(navigateBack) },
        bottomBar = {MainBottomNavigation(mainBottomNavItems,"")},
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        DayContent(modifier = Modifier.padding(innerPadding),date, activities, onActivityClick = onActivityClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayTopBar(
    navigateBack: () -> Unit
){
    TopAppBar(
        modifier = Modifier.padding(top = 5.dp),
        title = {
            Text("Your activities", fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
        },
        navigationIcon = {
            IconButton(
                onClick = navigateBack,
                modifier = Modifier.size(60.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.top_back_arrow_foreground),
                    contentDescription = "Navigate to previous screen"
                )
            }
        }
    )
}

@SuppressLint("NewApi")
@Composable
fun DayContent(
    modifier: Modifier,
    date: LocalDate,
    activities: List<Activity>,
    onActivityClick: (Long) -> Unit
){
    Column(modifier.fillMaxSize()) {
        Text(
            text = "${date.dayOfMonth}.${date.monthValue}.${date.year}",
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (activities.isEmpty()) {
                item {
                    Text("No activities found for this day.")
                }
            } else {
                items(activities.sortedBy { it.start }) { activity ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp),
                        onClick = { onActivityClick(activity.id) }
                    ) {
                        Column(modifier = Modifier.background(MaterialTheme.colorScheme.primary).fillMaxWidth().padding(16.dp)) {
                            Text(text = activity.name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.tertiary)
                            Text(text = "${activity.start} - ${activity.end}", color = MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun DayPreview(){
    SemestralkaTheme {
        DayScreen(
            mainBottomNavItems = listOf(
                BottomNavItem(
                    route = "calendar",
                    label = "Calendar",
                    iconId = R.drawable.calendar_foreground,
                    contentDescription = "Calendar icon",
                    onClick = {}
                ),
                BottomNavItem(
                    route = "add_activity",
                    label = "Add Activity",
                    iconId = R.drawable.plus_foreground,
                    contentDescription = "Add Activity icon",
                    onClick = {}
                ),
                BottomNavItem(
                    route = "search",
                    label = "Search",
                    iconId = R.drawable.search_foreground,
                    contentDescription = "Search icon",
                    onClick = {}
                ),
            ),
            LocalDate.now().toString(),
            onActivityClick = {},
            navigateBack = {}
        )
    }
}