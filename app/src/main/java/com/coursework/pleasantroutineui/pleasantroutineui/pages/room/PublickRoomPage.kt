package com.coursework.pleasantroutineui.pages.room
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewModelScope

import coil.compose.AsyncImage

import com.coursework.pleasantroutineui.domain.User

import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.ui.layout.ContentScale

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.RoomInfo
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.coursework.pleasantroutineui.R

import com.coursework.pleasantroutineui.domain.NotesPackage
import com.coursework.pleasantroutineui.domain.Param
import com.coursework.pleasantroutineui.pages.profile.buildImageCacheKey
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.repo.prod.IUserRepo
import com.coursework.pleasantroutineui.ui_services.Menue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PublicRoomViewModel @Inject constructor(
    private val repository: IRoomRepo,
    private val repositoryNote: INotesRepo,
    private val userRepo: IUserRepo
) : ViewModel() {

    private val _roomInfo = MutableStateFlow<RoomInfo?>(null)
    val roomInfo: StateFlow<RoomInfo?> = _roomInfo

    private val _notesPackage = MutableStateFlow<NotesPackage?>(null)
    val notesPackage: StateFlow<NotesPackage?> = _notesPackage

    private val _param = MutableStateFlow(
        Param(
            tags = emptyList(),
            owner = emptyList(),
            start = null,
            end = null,
        )
    )
    val param: StateFlow<Param> = _param

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    private var currentRoom: String? = null

    fun init(roomNumber: String) {
        if (currentRoom == roomNumber) return
        currentRoom = roomNumber

        viewModelScope.launch {
            _roomInfo.value = repository.getRoomInfoById(roomNumber)
            if (_currentUserId.value == null) {
                _currentUserId.value = userRepo.getSelfInfo().id
            }
        }

        observeFilter(roomNumber)
    }

    @OptIn(FlowPreview::class)
    private fun observeFilter(roomNumber: String) {
        viewModelScope.launch {
            _param
                .debounce(250)
                .distinctUntilChanged()
                .collectLatest { param ->
                    runCatching {
                        repositoryNote.getAllPublicNotes(param, true, roomNumber)
                    }.onSuccess {
                        _notesPackage.value = it
                    }
                }
        }
    }

    fun refreshSignedUrl(link: String?) {
        viewModelScope.launch {
            if (link != null && link != "") {
                val linkSign: String = repository.signedLink(link)
                _roomInfo.value = _roomInfo.value?.copy(publicSignedPhotoLink = linkSign)
            }
        }
    }

    fun updatePhoto(photo: Uri) {
        viewModelScope.launch {
            val link = repository.setPhoto(photo)
            _roomInfo.value = _roomInfo.value?.copy(publicPhotoLink = link, publicSignedPhotoLink = null)
        }
    }

    fun updatePublicInfo(info: String) {
        viewModelScope.launch {
            repository.setPublicInfo(info)
            _roomInfo.value = _roomInfo.value?.copy(publicInfo = info)
        }
    }

    fun deleteRoom(note: Note) {
        viewModelScope.launch {
            repositoryNote.deleteRoom(note.id)
            refresh()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            runCatching {
                repositoryNote.getAllRoomNotes(_param.value, false)
            }.onSuccess { result ->
                _notesPackage.value = result
            }
        }
    }

    fun updateFilter(transform: Param.() -> Param) {
        _param.update(transform)
    }
}

@Composable
fun PublicRoomPage(
    roomNumber: String,
    navController: NavController,
    vm: PublicRoomViewModel
) {

    val room by vm.roomInfo.collectAsState()
    val notesPackage by vm.notesPackage.collectAsState()
    val param by vm.param.collectAsState()
    val currentUserId by vm.currentUserId.collectAsState()

    val isResident = remember(room, currentUserId) {
        currentUserId != null && room?.residents?.any { it.id == currentUserId } == true
    }

    LaunchedEffect(roomNumber) {
        vm.init(roomNumber)
    }

    Menue("Комната", false, navController) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            item {
                room?.let {
                    RoomHeader(
                        room = it,
                        isResident = isResident,
                        onRefresh = { link -> vm.refreshSignedUrl(link) },
                        onPhotoUpload = { uri -> vm.updatePhoto(uri) }
                    )
                }
            }

            item {
                room?.let {
                    ResidentsList(it.residents, navController, { link -> vm.refreshSignedUrl(link) })
                }
            }

            item {
                room?.let {
                    RoomDescription(
                        room = it,
                        isResident = isResident,
                        onSavePublicInfo = { info -> vm.updatePublicInfo(info) }
                    )
                }
            }

            item {
                ComplexFilter(
                    allTags = notesPackage?.allTags,
                    allUsers = notesPackage?.allUser,

                    selectedUserIds = param.owner
                        ?.mapNotNull { it.id }
                        ?.toSet() ?: emptySet(),

                    onSelectedUsersUpdate = { ids ->
                        val users = notesPackage?.allUser.orEmpty()
                        vm.updateFilter {
                            copy(owner = users.filter { it.id in ids })
                        }
                    },

                    selectedItems = param.tags?.toSet() ?: emptySet(),

                    onSelectedItemsUpdate = { tags ->
                        vm.updateFilter { copy(tags = tags.toList()) }
                    },

                    startDate = param.start,
                    onStartDateUpdate = { vm.updateFilter { copy(start = it) } },

                    endDate = param.end,
                    onEndDateUpdate = { vm.updateFilter { copy(end = it) } },

                    showCreateNoteButton = false
                )
            }

            items(notesPackage?.allNotes ?: emptyList()) { note ->
                NotePreview(
                    note = note,
                    height = 250.dp,
                    onDeleteClick = { note -> vm.deleteRoom(note) },
                    onViewClick = { note -> navController.navigate("${com.coursework.pleasantroutineui.domain.Destinations.NOTE_PAGE.title}/vr_${note.id}") },
                    refresh = {vm.refresh()}
                )
            }
        }
    }
}


@Composable
fun RoomDescription(room: RoomInfo, isResident: Boolean, onSavePublicInfo: (String) -> Unit) {

    var isEditing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(room.publicInfo ?: "") }

    LaunchedEffect(room.publicInfo) {
        text = room.publicInfo ?: ""
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "О комнате",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )

            if (isResident) {
                IconButton(
                    onClick = {
                        if (isEditing) {
                            onSavePublicInfo(text)
                        }
                        isEditing = !isEditing
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            if (isEditing) R.drawable.baseline_done_24 else R.drawable.edit
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        if (isEditing) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
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
            room.publicInfo?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun RoomHeader(
    room: RoomInfo,
    isResident: Boolean,
    onRefresh: (link: String?) -> Unit,
    onPhotoUpload: (Uri) -> Unit
) {
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> uri?.let { onPhotoUpload(it) } }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 12.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(room.publicSignedPhotoLink ?: "")
                    .diskCacheKey(buildImageCacheKey("room" + room.id, room.publicPhotoLink))
                    .memoryCacheKey(buildImageCacheKey("room" + room.id, room.publicPhotoLink))
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
                error = painterResource(R.drawable.no_photo),
                placeholder = painterResource(R.drawable.no_photo),
                onError = { onRefresh(room.publicPhotoLink) }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.7f))
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 60.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(room.publicSignedPhotoLink ?: "")
                        .diskCacheKey(buildImageCacheKey("room" + room.id, room.publicPhotoLink))
                        .memoryCacheKey(buildImageCacheKey("room" + room.id, room.publicPhotoLink))
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .networkCachePolicy(CachePolicy.ENABLED)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape),
                    error = painterResource(R.drawable.no_photo),
                    placeholder = painterResource(R.drawable.no_photo),
                    onError = { onRefresh(room.publicPhotoLink) }
                )
            }

            if (isResident) {
                IconButton(
                    onClick = {
                        picker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.sharp_add_a_photo_24),
                        contentDescription = "Изменить фото",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(70.dp))

        Text(
            text = "Комната ${room.number}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        room.dormitoryId?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResidentsList(
    residents: List<User>?,
    navController: NavController,
    refresh: (link: String?) -> Unit
) {

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = "Жильцы",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            residents?.forEach {
                ResidentItem(it, navController, refresh)
            }

        }
    }
}

@Composable
fun ResidentItem(
    user: User,
    navController: NavController,
    refresh: (link: String?) -> Unit
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
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.signedLink ?: "")
                .diskCacheKey(buildImageCacheKey(user.id, user.photoLink))
                .memoryCacheKey(buildImageCacheKey(user.id, user.photoLink))
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .networkCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            error = painterResource(R.drawable.no_photo),
            placeholder = painterResource(R.drawable.no_photo),
            onError = {
                refresh(user.photoLink)
            }
        )

        Text(
            text = user.name ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
