package com.coursework.pleasantroutineui.ui_services

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun Menue(title: String, isEditButton: Boolean, navController: NavController, innerContent: @Composable (PaddingValues) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                navController,
                onItemClick = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.padding(top = 50.dp),
            topBar = {
                ProfileTopBar(
                    title        = title,
                    isEditButton = isEditButton,
                    onMenuClick  = { scope.launch { drawerState.open() } },
                    onEditClick  = { /* перейти в редактирование */ }
                )
            }
        ) { paddingValues ->
            innerContent(paddingValues)
        }
    }
}