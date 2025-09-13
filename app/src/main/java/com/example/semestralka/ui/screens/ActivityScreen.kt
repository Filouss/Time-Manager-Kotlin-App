package com.example.semestralka.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semestralka.R
import com.example.semestralka.data.local.Activity
import com.example.semestralka.ui.alarm.AlarmUtils
import com.example.semestralka.ui.components.MainBottomNavigation
import com.example.semestralka.ui.navigation.BottomNavItem
import com.example.semestralka.ui.viewmodels.ActivityEditorViewModel
import com.example.semestralka.ui.viewmodels.ActivityViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityScreen(
    mainBottomNavItems: List<BottomNavItem>,
    onStartEdit: (Long) -> Unit,
    viewModel: ActivityViewModel = viewModel(),
    editorViewModel: ActivityEditorViewModel = viewModel(),
    navigateBack: () -> Unit
){
    val activity by editorViewModel.activity.collectAsStateWithLifecycle()
    println(activity.name)
    Scaffold (
        topBar = { ActivityTopBar(onStartEdit, activity, navigateBack) },
        bottomBar = {MainBottomNavigation(mainBottomNavItems,"")},
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        ActivityContent(modifier = Modifier.padding(innerPadding), activity,viewModel,navigateBack)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityTopBar(
    onStartEdit: (Long) -> Unit,
    activity: Activity,
    navigateBack: () -> Unit
){
    TopAppBar(
        modifier = Modifier.padding(top = 5.dp),
        title = {
            Text("Activity Description", fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
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
        },
        actions = {
            IconButton(
                onClick = { onStartEdit(activity.id) },
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.edit_foreground),
                    contentDescription = "Edit activity"
                )
            }
        }
    )
}

@SuppressLint("NewApi")
@Composable
fun ActivityContent(
    modifier: Modifier,
    activity: Activity,
    viewModel: ActivityViewModel,
    navigateBack: () -> Unit
){
    val activityFieldModifier = Modifier
        .clip(RoundedCornerShape(30.dp))
        .background(MaterialTheme.colorScheme.primary)
        .fillMaxWidth()
        .padding(10.dp)

    val activityHeaderModifier = Modifier.padding(start = 10.dp)
    val context = LocalContext.current
    var openAlertDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text( text ="Activity Name", modifier = activityHeaderModifier)
        Text(
            text = activity.name,
            modifier = activityFieldModifier,
            color = MaterialTheme.colorScheme.tertiary
        )
        Text( text ="Activity Date", modifier = activityHeaderModifier)
        Text(
            text = "${activity.date.dayOfMonth}. ${activity.date.monthValue}. ${activity.date.year} ",
            modifier = activityFieldModifier,
            color = MaterialTheme.colorScheme.tertiary
        )
        Text( text ="Activity Time", modifier = activityHeaderModifier)
        Text(
            text = "${activity.start} - ${activity.end}",
            modifier = activityFieldModifier,
            color = MaterialTheme.colorScheme.tertiary
        )
        Text( text ="Reminder", modifier = activityHeaderModifier)
        Text(
            text = activity.reminder?.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")) ?: "Not set",
            modifier = activityFieldModifier,
            color = MaterialTheme.colorScheme.tertiary
        )
        Text( text ="Notes", modifier = activityHeaderModifier)
        Text(
            text = activity.notes ?: "None",
            modifier = Modifier
                .clip(shape = RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.primary)
                .fillMaxWidth()
                .padding(15.dp),
            color = MaterialTheme.colorScheme.tertiary
        )
        OutlinedButton(
            onClick = {
                openAlertDialog = true
            },
            modifier = Modifier.fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Text("Delete activity")
        }
        if (openAlertDialog){
            AlertDialogExample(
                onDismissRequest = { openAlertDialog = false },
                onConfirmation = {
                    openAlertDialog = false
                    viewModel.delete(activity)
                    AlarmUtils.cancelAlarm(context,activity)
                },
                dialogTitle = "Delete activity?",
                dialogText = "After clicking confirm your activity will be deleted",
                navigateBack
            )
        }
    }
}

@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    navigateBack: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                    navigateBack()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}
