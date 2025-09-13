package com.example.semestralka.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semestralka.R
import com.example.semestralka.ui.components.MainBottomNavigation
import com.example.semestralka.ui.navigation.BottomNavItem
import com.example.semestralka.ui.theme.SemestralkaTheme
import com.example.semestralka.ui.viewmodels.ActivityViewModel
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    mainBottomNavItems: List<BottomNavItem>,
    onItemClick: (Any?) -> Unit,
    currentDestination: String?,
    activityViewModel: ActivityViewModel = viewModel()
){
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        activityViewModel.fetchHolidaysAndStore(context)
    }
    Scaffold (
        topBar = { CalendarTopBar() },
        bottomBar = {MainBottomNavigation(mainBottomNavItems,currentDestination)},
        modifier = Modifier.fillMaxSize(),
    ) {
        innerPadding ->
        CalendarContent(modifier = Modifier.padding(innerPadding), onItemClick)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarContent(
    modifier: Modifier,
    onItemClick: (Any?) -> Unit
){
    val selectedDate = remember { mutableStateOf<LocalDate?>(null) }

    val today = remember { LocalDate.now() }
    var currentYear by remember { mutableStateOf(today.year) }
    var currentMonth by remember { mutableStateOf(today.monthValue) }

    val daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth()

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding( start = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${Month.of(currentMonth)} ${currentYear}",
            fontSize = 25.sp,
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
        )
        Box(
            modifier = Modifier.weight(1f)
        ){
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 60.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(daysInMonth) { day ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .height(70.dp)
                            .padding(5.dp)
                            .clip(shape = RoundedCornerShape(30.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .aspectRatio(1f)
                            .clickable {
                                //day in lambda is starting from 0
                                selectedDate.value = LocalDate.of(currentYear, currentMonth, day+1)
                                onItemClick(selectedDate.value)
                            }
                    ) {
                        Text("${day+1}", color = MaterialTheme.colorScheme.tertiary)
                    }
                }
            }
        }
        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
        ){
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                IconButton(
                    onClick = {
                        if (currentMonth == 1) {
                            currentMonth = 12
                            currentYear -= 1
                        } else {
                            currentMonth -= 1
                        }
                    },
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.back_arrow_foreground),
                        contentDescription = "Choose previous month"
                    )
                }
                IconButton(
                    onClick = {
                        if (currentMonth == 12) {
                            currentMonth = 1
                            currentYear += 1
                        } else {
                            currentMonth += 1
                        }
                    },
                    modifier = Modifier.size(60.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "Choose next month"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTopBar(){
    TopAppBar(
        modifier = Modifier.padding(top = 5.dp),
        title = {
            Text("Calendar", fontSize = 30.sp, fontWeight = FontWeight.SemiBold)
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun CalendarPreview(){
    SemestralkaTheme {
        CalendarScreen(
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
            onItemClick = {},
            ""
        )
    }
}