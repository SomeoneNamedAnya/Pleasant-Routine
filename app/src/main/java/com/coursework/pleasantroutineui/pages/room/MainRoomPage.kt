package com.coursework.pleasantroutineui.pages.room

import RoomUserPreview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.ui_services.Menue
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewModelScope
import com.coursework.pleasantroutineui.R
import com.coursework.pleasantroutineui.domain.Button
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.RoomInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainRoomPageViewModel @Inject constructor(
    private val roomRepository: IRoomRepo
) : ViewModel() {

    private val _roomInfo = MutableStateFlow<RoomInfo?>(null)
    val roomInfo: StateFlow<RoomInfo?> = _roomInfo



    fun loadRoom() {
        viewModelScope.launch {
            _roomInfo.value = roomRepository.getRoomInfo()
        }
    }

    fun refreshSignedUrl(link: String?, userId: String?) {
        viewModelScope.launch {
            if (link != null && link != "") {

                val linkSign: String = roomRepository.signedLink(link)
                _roomInfo.value = _roomInfo.value?.copy(
                    residents = _roomInfo.value?.residents?.map { user ->
                        if (user.id == userId) {
                            user.copy(signedLink = linkSign)
                        } else {
                            user
                        }
                    } ?: emptyList()
                )
            }

        }
    }
}

@Composable
fun MainRoomScreen(navController: NavController, vm: MainRoomPageViewModel) {

    LaunchedEffect(Unit) {
        vm.loadRoom()
    }
    val info by vm.roomInfo.collectAsState()

    Menue("Моя комната", false, navController){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(0.8f)

            ) {

                Text(
                    text = "Важное о " + info?.number,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp, start = 5.dp, end = 5.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    if (info?.privateInfo == null || info?.privateInfo?.isEmpty() == true) {
                        Text(
                            text = "Пока здесь ничего нет",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall
                        )
                    } else {
                        Text(
                            text = info!!.privateInfo!!,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }

                }
                Spacer(modifier = Modifier.height(15.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )

            }

            Column(
                modifier = Modifier
                    .weight(0.9f)

            ) {
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = "Жильцы",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 5.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {


                    Spacer(modifier = Modifier.height(15.dp))

                    Column( modifier = Modifier
                        .height(200.dp)
                        .verticalScroll(rememberScrollState())) {
                        val residents = info?.residents ?: emptyList()
                        for (roommate in residents) {
                            RoomUserPreview(roommate,
                                navController,
                                {
                                    vm.refreshSignedUrl(roommate.photoLink, roommate.id)

                                }
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }

                }
                Spacer(modifier = Modifier.height(15.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            val roomNav = listOf<Button>(
                Button(
                    "Парадная",
                    R.drawable.public_page,
                    Destinations.PUBLIC_ROOM_PAGE.title
                ),
                Button(
                    "Доска задач",
                    R.drawable.todo_list,
                    Destinations.TASK_PAGE.title
                ),
                Button(
                    "Чат комнаты",
                    R.drawable.mail,
                    Destinations.ROOM_CHAT_PAGE.title
                ),

                Button(
                    "Архив",
                    R.drawable.archive_common,
                    Destinations.COMMON_ARCHIVE_PAGE.title + "/0"
                ),

                Button(
                    "Личные заметки",
                    R.drawable.archive_personal,
                    Destinations.PERSONAL_ARCHIVE_PAGE.title + "/0"
                ),
                Button(
                    "Личные заметки",
                    R.drawable.archive_personal,
                    Destinations.PERSONAL_ARCHIVE_PAGE.title + "/0"
                )
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Основные сервисы",
                    modifier = Modifier.padding(top = 10.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )
                Column(
                    modifier = Modifier
                        .padding(top = 5.dp, start = 5.dp, end = 5.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    for (i in 0..<roomNav.size step 2) {
                        val buttonLeft: Button = roomNav[i]
                        if (i + 1 == roomNav.size) {
                            TwoColumnButton(
                                navController,
                                false,
                                buttonLeft,
                                buttonLeft)

                        } else {
                            val buttonRight: Button = roomNav[i + 1]
                            TwoColumnButton(
                                navController,
                                true,
                                buttonLeft,
                                buttonRight)
                        }
                    }

                }

            }

        }
    }


}

@Composable
fun TwoColumnButton(navController: NavController,
                    isRight: Boolean,
                    buttonLeft: Button,
                    buttonRight: Button) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(60.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(16.dp)
                )

                .clickable {
                    navController.navigate(buttonLeft.navigateDestiny)
                }
                .padding(horizontal = 16.dp),

            ) {
            Text(
                text = buttonLeft.text,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.Center)
            )

            Icon(
                painter = painterResource(buttonLeft.idPicture),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        Spacer(modifier = Modifier.width(5.dp))
        if (isRight) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable {
                        navController.navigate(buttonRight.navigateDestiny)
                    }
                    .padding(horizontal = 16.dp),

                ) {
                Text(
                    text = buttonRight.text,
                    style = MaterialTheme.typography.titleSmall,
                    color =  MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.Center)
                )

                Icon(
                    painter = painterResource(buttonRight.idPicture),
                    contentDescription = null,
                    tint =  MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                )
        }

    }
    Spacer(modifier = Modifier.height(5.dp))
}

