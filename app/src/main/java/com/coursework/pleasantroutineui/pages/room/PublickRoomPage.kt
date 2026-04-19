package com.coursework.pleasantroutineui.pages.room
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
import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.RoomInfo
import com.coursework.pleasantroutineui.domain.Task
import com.coursework.pleasantroutineui.domain.TaskStatus
import com.coursework.pleasantroutineui.repo.interfaces.IPublicRoomInfo
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PublicRoomViewModel(
    private val repository: IPublicRoomInfo
) : ViewModel() {

    var roomInfo by mutableStateOf<RoomInfo?>(null)
        private set

    var notes by mutableStateOf<List<Note>>(emptyList())
        private set

    fun load(roomNumber: String) {

        viewModelScope.launch {

            roomInfo = repository.getRoomInfo(roomNumber)
            notes = repository.getNotes(roomNumber)

        }
    }

}
@Composable
fun PublicRoomPage(
    roomNumber: String,
    currentUserId: String,
    navController: NavController,
    viewModel: PublicRoomViewModel
) {

//    LaunchedEffect(Unit) {
//        viewModel.load(roomNumber)
//    }
//
//    val room = viewModel.roomInfo ?: return
//    val notes = viewModel.notes
//
//    val isResident = room.residents.any { it.id == currentUserId }
//    Menue("Моя комната", false, navController) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .background(MaterialTheme.colorScheme.background)
//        ) {
//
//            item { RoomHeader(room) }
//
//            item { ResidentsList(room.residents, navController) }
//
//            item { RoomDescription(room) }
//
//            if (isResident) {
//                item { AddNoteButton() }
//            }
//
//            items(notes) {
//                NoteCard(it, navController)
//            }
//
//        }
//    }
}
@Composable
fun RoomDescription(room: RoomInfo) {

//    Column(
//        modifier = Modifier
//            .padding(16.dp)
//            .background(MaterialTheme.colorScheme.background)
//    ) {
//
//        Text(
//            text = "About room",
//            style = MaterialTheme.typography.titleMedium,
//            color = MaterialTheme.colorScheme.onBackground
//        )
//
//        Spacer(Modifier.height(6.dp))
//
//        Text(
//            text = room.publicDescription,
//            style = MaterialTheme.typography.bodyMedium,
//            color = MaterialTheme.colorScheme.onBackground
//        )
//    }
}
@Composable
fun RoomHeader(room: RoomInfo) {

//    Column(
//        modifier = Modifier
//            .background(MaterialTheme.colorScheme.surface)
//            .padding(bottom = 12.dp)
//    ) {
//
//        AsyncImage(
//            model = room.photoLinks.firstOrNull(),
//            contentDescription = null,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(220.dp),
//            contentScale = ContentScale.Crop
//        )
//
//        Spacer(Modifier.height(8.dp))
//
//        Text(
//            text = "Room ${room.roomNumber}",
//            style = MaterialTheme.typography.headlineMedium,
//            color = MaterialTheme.colorScheme.onSurface,
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//
//        Text(
//            text = room.dormitoryName,
//            style = MaterialTheme.typography.bodyLarge,
//            color = MaterialTheme.colorScheme.secondary,
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResidentsList(
    residents: List<User>,
    navController: NavController
) {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = "Residents",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            residents.forEach {
                ResidentItem(it, navController)
            }

        }
    }
}
@Composable
fun ResidentItem(
    user: User,
    navController: NavController
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                navController.navigate(
                    Destinations.ALL_USER_INFO_PAGE.title + "/${user.id}"
                )
            }
    ) {

        AsyncImage(
            model = user.photoLink,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )

        Text(
            text = user.firstName ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun AddNoteButton() {

    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text("Add note")

    }
}
@Composable
fun NoteCard(
    note: Note,
    navController: NavController
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(4.dp))

            Text(
                note.text,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            if (note.photoLinks.isNotEmpty()) {

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(note.photoLinks) { photo ->

                        AsyncImage(
                            model = photo,
                            contentDescription = null,
                            modifier = Modifier
                                .height(200.dp)
                                .width(300.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                    }

                }

            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Editors:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Row {

                note.owner.forEach { user ->

                    Text(
                        text = "${user.firstName} ",
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.clickable {
                            navController.navigate(
                                Destinations.ALL_USER_INFO_PAGE.title + "/${user.id}"
                            )
                        }
                    )

                }

            }

            Text(
                text = note.createTime,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

        }

    }
}