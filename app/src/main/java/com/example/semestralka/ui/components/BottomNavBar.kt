package com.example.semestralka.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.semestralka.ui.navigation.BottomNavItem


@Composable
fun MainBottomNavigation(
    mainBottomNavItems: List<BottomNavItem>,
    currentRoute: String?,
){
    NavigationBar (
        modifier = Modifier.height(100.dp)
    ) {
        mainBottomNavItems.forEach{item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(item.iconId),
                        contentDescription = item.contentDescription,
                        modifier = Modifier.size(50.dp)
                    )
                },
                label = { Text(item.label) },
                //Add activity is a class not an object so the first if doesn't cover it
                selected = (item.route == currentRoute) || (currentRoute?.contains("AddActivity") == true && item.route.contains("AddActivity") ),
                onClick = item.onClick,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}