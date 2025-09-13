package com.example.semestralka.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    mainBottomNavItems: List<BottomNavItem>,
    onItemClick: (Long) -> Unit,
    navigateBack: () -> Unit,
    viewModel: ActivityViewModel = viewModel(),
    currentDestination: String?,
){
    Scaffold (
        topBar = { SearchTopBar(navigateBack) },
        bottomBar = {MainBottomNavigation(mainBottomNavItems,currentDestination)},
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        SearchContent(modifier = Modifier.padding(innerPadding),viewModel,onItemClick)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    navigateBack: () -> Unit
){
    TopAppBar(
        modifier = Modifier.padding(top = 5.dp),
        title = {
            Text("Search", fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
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
fun SearchContent(
    modifier: Modifier,
    viewModel: ActivityViewModel,
    onItemClick: (Long) -> Unit
){
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Activity>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row {
            TextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Activity Name") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        Row {
            OutlinedButton (
                onClick = {
                    coroutineScope.launch {
                        viewModel.getActivitiesByString(query).collect { results ->
                            searchResults = results
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Search")
            }
        }
        Row {
            Text(text = "results")
        }
        Row {

            LazyColumn {
                items(searchResults){ activity ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        onClick = {
                            onItemClick(activity.id)
                        }
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
fun SearchPreview(){
    SemestralkaTheme {
        SearchScreen(
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
            navigateBack = {},
            currentDestination = ""
        )
    }
}