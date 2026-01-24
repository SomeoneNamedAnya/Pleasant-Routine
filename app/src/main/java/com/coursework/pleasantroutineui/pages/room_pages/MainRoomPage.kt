package com.coursework.pleasantroutineui.pages.room_pages

import android.content.ClipData
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.coursework.pleasantroutineui.R
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.pages.AccountInfoViewModel
import com.coursework.pleasantroutineui.pages.InfoRow
import com.coursework.pleasantroutineui.repo.IAccountRepo
import com.coursework.pleasantroutineui.repo.IRoomRepo
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
                                    RoomUserPreview(roommate)
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

@Composable
fun RoomUserPreview(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        ) {
        println(user.photoLink)
        AsyncImage(
            model = user.photoLink,
            onError = {
                Log.e(
                    "AsyncImage",
                    "Failed to load image: ${it.result.throwable}"
                )
            },
            contentDescription = "Example Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            placeholder = painterResource(id = R.drawable.no_photo), // Replace with your placeholder drawable
            error = painterResource(id = R.drawable.no_photo)  // Replace with your error drawable
        )

        Spacer(modifier = Modifier.width(15.dp))

        Text(
            text = user.selfInfo,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surface
                )
                .padding(8.dp),

        )
    }
}


