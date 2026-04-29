package com.coursework.pleasantroutineui.pages.common_services

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.discovery.CreateSharingRequest
import com.coursework.pleasantroutineui.domain.discovery.RoomSearchResult
import com.coursework.pleasantroutineui.domain.discovery.UserSearchResult
import com.coursework.pleasantroutineui.domain.sharing.SharingCard
import com.coursework.pleasantroutineui.repo.prod.DiscoveryRepository
import com.coursework.pleasantroutineui.ui_services.Menue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class DiscoveryTab { People, Rooms, News, Sharing }
enum class SharingFilter { MyCreated, MyClaimed, AllActive }

private const val TAG = "DiscoveryVM"

@HiltViewModel
class DiscoveryViewModel @Inject constructor(
    private val repo: DiscoveryRepository
) : ViewModel() {


    private val _tab = MutableStateFlow(DiscoveryTab.News)
    val tab: StateFlow<DiscoveryTab> = _tab
    fun selectTab(t: DiscoveryTab) { _tab.value = t }

    private val _peopleQuery = MutableStateFlow("")
    val peopleQuery: StateFlow<String> = _peopleQuery

    private val _peopleIdQuery = MutableStateFlow("")
    val peopleIdQuery: StateFlow<String> = _peopleIdQuery

    private val _people = MutableStateFlow<List<UserSearchResult>>(emptyList())
    val people: StateFlow<List<UserSearchResult>> = _people

    private val _peopleLoading = MutableStateFlow(false)
    val peopleLoading: StateFlow<Boolean> = _peopleLoading

    private val _peopleError = MutableStateFlow<String?>(null)
    val peopleError: StateFlow<String?> = _peopleError

    fun updatePeopleQuery(q: String) { _peopleQuery.value = q }
    fun updatePeopleIdQuery(q: String) { _peopleIdQuery.value = q }

    fun searchPeople() {
        viewModelScope.launch {
            _peopleLoading.value = true
            _peopleError.value = null
            try {
                val idLong = _peopleIdQuery.value.toLongOrNull()
                val name = _peopleQuery.value.takeIf { it.isNotBlank() }
                val result = repo.searchPeople(idLong, name, 0, 30)
                _people.value = result.content
            } catch (e: Exception) {
                Log.e(TAG, "searchPeople failed", e)
                _peopleError.value = "Ошибка поиска: ${e.localizedMessage}"
                _people.value = emptyList()
            } finally {
                _peopleLoading.value = false
            }
        }
    }

    private val _roomQuery = MutableStateFlow("")
    val roomQuery: StateFlow<String> = _roomQuery

    private val _roomIdQuery = MutableStateFlow("")
    val roomIdQuery: StateFlow<String> = _roomIdQuery

    private val _rooms = MutableStateFlow<List<RoomSearchResult>>(emptyList())
    val rooms: StateFlow<List<RoomSearchResult>> = _rooms

    private val _roomsLoading = MutableStateFlow(false)
    val roomsLoading: StateFlow<Boolean> = _roomsLoading

    private val _roomsError = MutableStateFlow<String?>(null)
    val roomsError: StateFlow<String?> = _roomsError

    fun updateRoomQuery(q: String) { _roomQuery.value = q }
    fun updateRoomIdQuery(q: String) { _roomIdQuery.value = q }

    fun searchRooms() {
        viewModelScope.launch {
            _roomsLoading.value = true
            _roomsError.value = null
            try {
                val idLong = _roomIdQuery.value.toLongOrNull()
                val number = _roomQuery.value.takeIf { it.isNotBlank() }
                val result = repo.searchRooms(idLong, number, 0, 30)
                _rooms.value = result.content
            } catch (e: Exception) {
                Log.e(TAG, "searchRooms failed", e)
                _roomsError.value = "Ошибка поиска: ${e.localizedMessage}"
                _rooms.value = emptyList()
            } finally {
                _roomsLoading.value = false
            }
        }
    }

    private val _news = MutableStateFlow<List<Note>>(emptyList())
    val news: StateFlow<List<Note>> = _news

    private val _newsLoading = MutableStateFlow(false)
    val newsLoading: StateFlow<Boolean> = _newsLoading

    private val _newsError = MutableStateFlow<String?>(null)
    val newsError: StateFlow<String?> = _newsError

    private var newsPage = 0
    private var isLastNewsPage = false

    private val _isNewsRequesting = MutableStateFlow(false)

    fun loadFirstNews() {
        newsPage = 0
        isLastNewsPage = false
        _news.value = emptyList()
        _newsError.value = null
        _isNewsRequesting.value = false
        loadMoreNews()
    }

    fun loadMoreNews() {
        if (_isNewsRequesting.value || isLastNewsPage) return
        _isNewsRequesting.value = true
        _newsLoading.value = true
        viewModelScope.launch {
            try {
                val paged = repo.getNews(newsPage, 10)
                _news.value = _news.value + paged.content
                isLastNewsPage = paged.last
                newsPage++
                _newsError.value = null
            } catch (e: Exception) {
                Log.e(TAG, "loadMoreNews failed", e)
                _newsError.value = "Ошибка загрузки: ${e.localizedMessage}"
            } finally {
                _isNewsRequesting.value = false
                _newsLoading.value = false
            }
        }
    }


    private val _sharingFilter = MutableStateFlow(SharingFilter.AllActive)
    val sharingFilter: StateFlow<SharingFilter> = _sharingFilter

    private val _sharingCards = MutableStateFlow<List<SharingCard>>(emptyList())
    val sharingCards: StateFlow<List<SharingCard>> = _sharingCards

    private val _sharingLoading = MutableStateFlow(false)
    val sharingLoading: StateFlow<Boolean> = _sharingLoading

    private val _sharingError = MutableStateFlow<String?>(null)
    val sharingError: StateFlow<String?> = _sharingError

    private var sharingPage = 0
    private var isLastSharingPage = false
    private val _isSharingRequesting = MutableStateFlow(false)

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog

    fun toggleCreateDialog(show: Boolean) { _showCreateDialog.value = show }


    fun selectSharingFilter(f: SharingFilter) {
        _sharingFilter.value = f
    }

    fun refreshSharing() {
        sharingPage = 0
        isLastSharingPage = false
        _sharingCards.value = emptyList()
        _sharingError.value = null
        _isSharingRequesting.value = false
        loadMoreSharing()
    }

    fun loadMoreSharing() {
        if (_isSharingRequesting.value || isLastSharingPage) return
        _isSharingRequesting.value = true
        _sharingLoading.value = true
        viewModelScope.launch {
            try {
                val fetcher = when (_sharingFilter.value) {
                    SharingFilter.MyCreated -> repo::myCreated
                    SharingFilter.MyClaimed -> repo::myClaimed
                    SharingFilter.AllActive -> repo::allActive
                }
                val paged = fetcher(sharingPage, 10)
                _sharingCards.value = _sharingCards.value + paged.content
                isLastSharingPage = paged.last
                sharingPage++
                _sharingError.value = null
            } catch (e: Exception) {
                Log.e(TAG, "loadMoreSharing failed", e)
                _sharingError.value = "Ошибка загрузки: ${e.localizedMessage}"
            } finally {
                _isSharingRequesting.value = false
                _sharingLoading.value = false
            }
        }
    }

    fun createSharing(title: String, description: String, photoLink: String?) {
        viewModelScope.launch {
            try {
                repo.createSharing(CreateSharingRequest(title, description, photoLink))
                _showCreateDialog.value = false
                refreshSharing()
            } catch (e: Exception) {
                Log.e(TAG, "createSharing failed", e)
                _sharingError.value = "Не удалось создать: ${e.localizedMessage}"
            }
        }
    }

    fun claimSharing(cardId: Long) {
        viewModelScope.launch {
            try {
                repo.claimSharing(cardId)
                refreshSharing()
            } catch (e: Exception) {
                Log.e(TAG, "claimSharing failed", e)
            }
        }
    }

    fun deleteSharing(cardId: Long) {
        viewModelScope.launch {
            try {
                repo.deleteSharing(cardId)
                refreshSharing()
            } catch (e: Exception) {
                Log.e(TAG, "deleteSharing failed", e)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryPage(
    navController: NavController,
    vm: DiscoveryViewModel
) {
    val selectedTab by vm.tab.collectAsState()

    Menue("Обзор", false, navController) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = DiscoveryTab.entries.indexOf(selectedTab),
                edgePadding = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                DiscoveryTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { vm.selectTab(tab) },
                        text = {
                            Text(
                                text = when (tab) {
                                    DiscoveryTab.People  -> "Люди"
                                    DiscoveryTab.Rooms   -> "Комнаты"
                                    DiscoveryTab.News    -> "Новости"
                                    DiscoveryTab.Sharing -> "Шеринг"
                                },
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                DiscoveryTab.People  -> PeopleTab(vm, navController)
                DiscoveryTab.Rooms   -> RoomsTab(vm, navController)
                DiscoveryTab.News    -> NewsTab(vm, navController)
                DiscoveryTab.Sharing -> SharingTab(vm, navController)
            }
        }
    }
}


@Composable
fun PeopleTab(vm: DiscoveryViewModel, navController: NavController) {

    val query by vm.peopleQuery.collectAsState()
    val idQuery by vm.peopleIdQuery.collectAsState()
    val people by vm.people.collectAsState()
    val loading by vm.peopleLoading.collectAsState()
    val error by vm.peopleError.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = idQuery,
                onValueChange = vm::updatePeopleIdQuery,
                label = { Text("ID") },
                modifier = Modifier.width(100.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = vm::updatePeopleQuery,
                label = { Text("ФИО") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { vm.searchPeople() },
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Найти", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(Modifier.height(4.dp))
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(people.size) { index ->
                val user = people[index]
                PersonItem(user = user, onClick = {
                    navController.navigate(
                        Destinations.ALL_USER_INFO_PAGE.title + "/${user.id}"
                    )
                })
            }
        }
    }
}

@Composable
fun PersonItem(user: UserSearchResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = user.photoLink ?: "",
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                user.roomNumber?.let {
                    Text(
                        text = "Комната $it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}


@Composable
fun RoomsTab(vm: DiscoveryViewModel, navController: NavController) {

    val idQuery by vm.roomIdQuery.collectAsState()
    val query by vm.roomQuery.collectAsState()
    val rooms by vm.rooms.collectAsState()
    val loading by vm.roomsLoading.collectAsState()
    val error by vm.roomsError.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = idQuery,
                onValueChange = vm::updateRoomIdQuery,
                label = { Text("ID") },
                modifier = Modifier.width(100.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(Modifier.width(8.dp))
            OutlinedTextField(
                value = query,
                onValueChange = vm::updateRoomQuery,
                label = { Text("Номер") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { vm.searchRooms() },
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Найти", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(Modifier.height(4.dp))
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(rooms.size) { index ->
                val room = rooms[index]
                RoomItem(room = room, onClick = {
                    navController.navigate(
                        Destinations.PUBLIC_ROOM_PAGE.title + "/${room.id}"
                    )
                })
            }
        }
    }
}

@Composable
fun RoomItem(room: RoomSearchResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = room.publicPhotoLink ?: "",
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = "Комната ${room.number}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                room.dormitoryName?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
fun NewsTab(vm: DiscoveryViewModel, navController: NavController) {

    val news by vm.news.collectAsState()
    val loading by vm.newsLoading.collectAsState()
    val error by vm.newsError.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        if (news.isEmpty()) vm.loadFirstNews()
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= news.size - 3 && news.isNotEmpty()
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) vm.loadMoreNews()
    }

    when {

        news.isEmpty() && loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        news.isEmpty() && error != null -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = error ?: "Неизвестная ошибка",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { vm.loadFirstNews() }) {
                        Text("Повторить")
                    }
                }
            }
        }

        news.isEmpty() && !loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Новостей пока нет",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        else -> {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(news) { _, note ->
                    NewsNoteCard(note = note, navController = navController)
                }


                if (loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsNoteCard(note: Note, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 4
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = note.creatorName ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = note.createdAt,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharingTab(vm: DiscoveryViewModel, navController: NavController) {

    val filter by vm.sharingFilter.collectAsState()
    val cards by vm.sharingCards.collectAsState()
    val loading by vm.sharingLoading.collectAsState()
    val error by vm.sharingError.collectAsState()
    val showDialog by vm.showCreateDialog.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(filter) { vm.refreshSharing() }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= cards.size - 3 && cards.isNotEmpty()
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) vm.loadMoreSharing()
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SingleChoiceSegmentedButtonRow {
                SharingFilter.entries.forEachIndexed { idx, f ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = idx,
                            count = SharingFilter.entries.size
                        ),
                        selected = filter == f,
                        onClick = { vm.selectSharingFilter(f) }
                    ) {
                        Text(
                            when (f) {
                                SharingFilter.MyCreated -> "Мои"
                                SharingFilter.MyClaimed -> "Участник"
                                SharingFilter.AllActive -> "Все"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
            IconButton(onClick = { vm.toggleCreateDialog(true) }) {
                Icon(Icons.Default.Add, contentDescription = "Создать")
            }
        }

        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }

        when {
            cards.isEmpty() && loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            cards.isEmpty() && !loading && error == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Пусто",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            else -> {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(cards) { _, card ->
                        SharingCardItem(
                            card = card,
                            onClaim = { vm.claimSharing(card.id) },
                            onDelete = { vm.deleteSharing(card.id) },
                            onCreatorClick = {
                                card.creatorId?.let {
                                    navController.navigate(
                                        Destinations.ALL_USER_INFO_PAGE.title + "/$it"
                                    )
                                }
                            },
                            onRoomClick = {
                                card.roomId?.let {
                                    navController.navigate(
                                        Destinations.PUBLIC_ROOM_PAGE.title + "/$it"
                                    )
                                }
                            }
                        )
                    }
                    if (loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        CreateSharingDialog(
            onDismiss = { vm.toggleCreateDialog(false) },
            onCreate = { title, desc, photo ->
                vm.createSharing(title, desc, photo)
            }
        )
    }
}

@Composable
fun SharingCardItem(
    card: SharingCard,
    onClaim: () -> Unit,
    onDelete: () -> Unit,
    onCreatorClick: () -> Unit,
    onRoomClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (card.isActive)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = card.title ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            card.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
            }
            Row {
                Text("Создатель: ", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = card.creatorName ?: "—",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = onCreatorClick)
                )
            }
            card.roomNumber?.let {
                Row {
                    Text("Комната: ", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = onRoomClick)
                    )
                }
            }
            Text(
                text = card.createdAt ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(8.dp))
            if (card.isActive) {
                Button(onClick = onClaim, modifier = Modifier.fillMaxWidth()) {
                    Text("Забрать")
                }
            } else {
                Text(
                    text = "Забрано: ${card.claimedByName ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun CreateSharingDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, description: String, photoLink: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoLink by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(title, description, photoLink.takeIf { it.isNotBlank() })
                    }
                }
            ) { Text("Создать") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        },
        title = { Text("Новая карточка шеринга", color = MaterialTheme.colorScheme.onBackground) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground
                    )
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }
    )
}
