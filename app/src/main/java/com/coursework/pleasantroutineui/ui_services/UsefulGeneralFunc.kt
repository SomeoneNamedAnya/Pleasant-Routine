package com.coursework.pleasantroutineui.ui_services

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
    isEditButton: Boolean,
    onMenuClick: () -> Unit,
    onEditClick: () -> Unit
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
        },
        actions = {
            if (isEditButton) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактировать"
                    )
                }
            }

        }
    )
}

@Composable
fun DrawerContent(
    navController: NavController,
    onItemClick: () -> Unit
) {

    ModalDrawerSheet (
        modifier = Modifier.padding(top = 50.dp),
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
        MenuList(navController, Destinations.PERSONAL_ARCHIVE_PAGE.title + "/0", "Личный архив")
        MenuList(navController, Destinations.COMMON_ARCHIVE_PAGE.title + "/0", "Общий архив")
        MenuList(navController, Destinations.DESK_PAGE.title + "/0", "Доска задач")
        MenuList(navController, Destinations.ROOM_CHAT_PAGE.title + "/1/0", "Чат комнаты")
        MenuList(navController, Destinations.PUBLIC_ROOM_PAGE.title + "/C081/0", "Страница комнаты")

        MenuList(navController, Destinations.LOGIN_PAGE.title, "Вход")

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
