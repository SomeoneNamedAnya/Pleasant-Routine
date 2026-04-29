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
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewModelScope
import com.coursework.pleasantroutineui.R
import com.coursework.pleasantroutineui.domain.Button
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.RoomInfo
import com.coursework.pleasantroutineui.repo.prod.IUserRepo
import com.coursework.pleasantroutineui.ui_services.InfoRow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainRoomPageViewModel @Inject constructor(
    private val roomRepository: IRoomRepo,
    private val userRepo: IUserRepo
) : ViewModel() {

    private val _roomInfo = MutableStateFlow<RoomInfo?>(null)
    val roomInfo: StateFlow<RoomInfo?> = _roomInfo

    private val _userId = MutableStateFlow<Long?>(null)
    val userId: StateFlow<Long?> = _userId



    fun loadRoom() {
        viewModelScope.launch {
            _roomInfo.value = roomRepository.getRoomInfo()
            _userId.value = userRepo.getSelfInfo().id?.toLong()
        }
    }

    fun updatePrivateInfo(about: String) {
        viewModelScope.launch {
            roomRepository.setAbout(about)
            _roomInfo.value = _roomInfo.value?.copy(privateInfo = about)
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
    val userId by vm.userId.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    Menue("Моя комната", false, navController){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(0.8f)
                    .padding(start = 15.dp, end = 15.dp)

            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Важное о ${info?.number}",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            if (isEditing) {
                                vm.updatePrivateInfo(text)
                            }
                            isEditing = !isEditing
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isEditing)
                                    R.drawable.baseline_done_24
                                else
                                    R.drawable.edit
                            ),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp, start = 5.dp, end = 5.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val commonTextStyle = MaterialTheme.typography.titleSmall.copy(
                        textAlign = TextAlign.Start
                    )
                    if (isEditing) {
                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = commonTextStyle,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,

                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,

                                cursorColor = MaterialTheme.colorScheme.onBackground,

                                focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    } else {
                        Text(
                            text = if (info?.privateInfo.isNullOrEmpty())
                                "Пока здесь ничего нет"
                            else
                                info!!.privateInfo!!,
                            modifier = Modifier.fillMaxWidth(),
                            style = commonTextStyle,
                            color = MaterialTheme.colorScheme.onBackground
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
                    .padding(start = 15.dp, end = 15.dp)

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
                    Destinations.DESK_PAGE.title
                ),
                Button(
                    "Чат комнаты",
                    R.drawable.mail,
                    Destinations.ROOM_CHAT_PAGE.title + "/$userId"
                ),

                Button(
                    "Архив",
                    R.drawable.archive_common,
                    Destinations.COMMON_ARCHIVE_PAGE.title
                ),

                Button(
                    "Мои заметки",
                    R.drawable.archive_personal,
                    Destinations.PERSONAL_ARCHIVE_PAGE.title
                ),
                Button(
                    "Новости",
                    R.drawable.news,
                    Destinations.DISCOVERY_PAGE.title
                )
            )

            Column(
                modifier = Modifier.weight(1f)
                    .padding(start = 15.dp, end = 15.dp)
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

