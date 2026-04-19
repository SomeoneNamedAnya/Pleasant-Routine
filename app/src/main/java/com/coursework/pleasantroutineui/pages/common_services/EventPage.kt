package com.coursework.pleasantroutineui.pages.common_services

import android.icu.number.IntegerWidth
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope

import androidx.room.util.copy
import coil.compose.AsyncImage
import com.coursework.pleasantroutineui.domain.Comment

import com.coursework.pleasantroutineui.domain.TaskStatus.Companion.fromDisplayName
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo

import com.coursework.pleasantroutineui.ui_services.Menue

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.Event
import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.RoomInfo
import com.coursework.pleasantroutineui.domain.Task
import com.coursework.pleasantroutineui.domain.TaskStatus
import com.coursework.pleasantroutineui.pages.room.ResidentItem
import com.coursework.pleasantroutineui.repo.interfaces.IEventRepo
import com.coursework.pleasantroutineui.repo.interfaces.IPublicRoomInfo
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class EventViewModel(
    private val repository: IEventRepo
) : ViewModel() {

    var event by mutableStateOf<Event?>(null)
        private set

    fun load(eventId: String) {

        viewModelScope.launch {
            event = repository.getEvent(eventId)
        }

    }

    fun join(userId: String) {

        viewModelScope.launch {
            event?.let {
                repository.joinEvent(it.id, userId)
                load(it.id)
            }
        }

    }

    fun leave(userId: String) {

        viewModelScope.launch {
            event?.let {
                repository.leaveEvent(it.id, userId)
                load(it.id)
            }
        }

    }

}

@Composable
fun EventPage(
    userId: String,
    eventId: String,
    navController: NavController,
    viewModel: EventViewModel
) {

    LaunchedEffect(Unit) {
        viewModel.load(eventId)
    }

    val event = viewModel.event ?: return

    val isCreator = event.creators.any { it.id == userId }
    val isParticipant = event.participants.any { it.id == userId }
    Menue("Моя комната", false, navController) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(start = 5.dp, end = 5.dp)
        ) {

            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (event.endDate == null)
                    event.startDate
                else
                    "${event.startDate} - ${event.endDate}"
            )

            Spacer(Modifier.height(16.dp))

            Text("Создатели")

            UsersRow(
                users = event.creators,
                navController = navController,
                onMoreClick = { }
            )

            Spacer(Modifier.height(16.dp))

            Text("Участники")

            UsersRow(
                users = event.participants,
                navController = navController,
                onMoreClick = { }
            )

            Spacer(Modifier.height(16.dp))

            if (event.photos.isNotEmpty()) {

                Text("Фотографии")

                Spacer(Modifier.height(8.dp))

                EventPhotos(event.photos)

            }

            Spacer(Modifier.height(16.dp))

            Text("Описание")

            Text(event.description)

            Spacer(Modifier.height(24.dp))

            if (!isCreator) {

                Button(
                    onClick = {
                        if (isParticipant)
                            viewModel.leave(userId)
                        else
                            viewModel.join(userId)
                    }
                ) {

                    Text(
                        if (isParticipant)
                            "Отказаться"
                        else
                            "Принять участие"
                    )

                }

            } else {

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Button(onClick = { }) {
                        Text("Редактировать")
                    }

                    Button(onClick = { }) {
                        Text("Скрыть / Сделать публичным")
                    }

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Удалить")
                    }

                }

            }

        }
    }

}

@Composable
fun EventPhotos(photos: List<String>) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(photos) { url ->

            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

        }

    }

}

@Composable
fun UsersRow(
    users: List<User>,
    navController: NavController,
    onMoreClick: () -> Unit
) {

    val shown = users.take(4)

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        shown.forEach {
            ResidentItem(it, navController)
        }

        if (users.size > 4) {

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clickable { onMoreClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("...")
            }

        }

    }

}



