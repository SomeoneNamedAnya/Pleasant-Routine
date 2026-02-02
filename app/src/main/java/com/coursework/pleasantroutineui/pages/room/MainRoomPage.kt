package com.coursework.pleasantroutineui.pages.room

import RoomUserPreview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.ui_services.DrawerContent
import com.coursework.pleasantroutineui.ui_services.ProfileTopBar
import kotlinx.coroutines.launch

class MainRoomPageViewModel (
    private val repository: IRoomRepo
) : ViewModel() {

    var allRoommates = liveData {
        emit(repository.getAllRoommates("1"))
    }


}

@Composable
fun MainRoomScreen(navController: NavController, vm: MainRoomPageViewModel) {
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
                    title = "Профиль",
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onEditClick = { /* перейти в редактирование */ }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 5.dp, bottom = 20.dp)
                        .background(MaterialTheme.colorScheme.background),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    val roommates by vm.allRoommates.observeAsState()

                    Column (
                        modifier = Modifier.padding(top=15.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "Соседи",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            )
                        Spacer(modifier = Modifier.height(15.dp))
                        Column( modifier = Modifier
                            .height(200.dp)
                            .verticalScroll(rememberScrollState())) {
                            if (roommates != null) {

                                for (roommate in roommates) {
                                    RoomUserPreview(roommate, navController)
                                    Spacer(modifier = Modifier.height(15.dp))
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(top = 15.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground
                        )


                    }

                }

            }
        }

    }
}

