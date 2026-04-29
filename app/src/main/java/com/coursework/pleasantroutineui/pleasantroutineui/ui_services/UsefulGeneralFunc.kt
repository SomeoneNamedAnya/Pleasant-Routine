package com.coursework.pleasantroutineui.ui_services

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.domain.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(
    title: String,
    onMenuClick: () -> Unit,
) {
    TopAppBar(
        colors = TopAppBarColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            scrolledContainerColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            subtitleContentColor =MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Открыть меню"
                )
            }
        }
    )
}

@Composable
fun DrawerContent(
    navController: NavController,
) {

    ModalDrawerSheet (
        drawerContainerColor = MaterialTheme.colorScheme.onPrimary,
        drawerContentColor = MaterialTheme.colorScheme.onSurface
    ) {

        Text(
            text = "Меню",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )


        MenuList(navController, Destinations.USER_ACCOUNT_PAGE.title,"Профиль")
        MenuList(navController, Destinations.USER_ROOM_PAGE.title, "Моя комната")
        MenuList(navController, Destinations.DISCOVERY_PAGE.title, "Новости")
        MenuList(navController, Destinations.PERSONAL_ARCHIVE_PAGE.title, "Личный архив")
        MenuList(navController, Destinations.PUBLIC_ROOM_PAGE.title, "Парадная")
        MenuList(navController, Destinations.VOLUNTARY_CHANGE_PASSWORD_PAGE.title, "Сменить пароль")
        MenuList(navController, Destinations.LOGOUT_PAGE.title, "Выход")

    }
}

@Composable
fun MenuList(navController: NavController,
             destination: String,
             listName: String) {
    NavigationDrawerItem(
        label = { Text(listName) },
        selected = (navController.currentDestination?.route == destination),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.surface,
            selectedTextColor = MaterialTheme.colorScheme.onSurface,
            selectedIconColor = MaterialTheme.colorScheme.onSurface,

            unselectedContainerColor = MaterialTheme.colorScheme.onPrimary,
            unselectedTextColor = MaterialTheme.colorScheme.onSurface,
            unselectedIconColor = MaterialTheme.colorScheme.onSurface,

        ),
        onClick = { navController.navigate(destination) }
    )
}
