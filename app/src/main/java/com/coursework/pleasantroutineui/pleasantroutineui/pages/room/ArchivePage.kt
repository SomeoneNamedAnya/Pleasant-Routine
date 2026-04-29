package com.coursework.pleasantroutineui.pages.room

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.NotesPackage
import com.coursework.pleasantroutineui.domain.Param
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo
import com.coursework.pleasantroutineui.ui_services.Menue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject


@HiltViewModel
class ArchivePageViewModel @Inject constructor(
    private val repository: INotesRepo
) : ViewModel() {

    private val _notesPackage = MutableStateFlow<NotesPackage?>(null)
    val notesPackage: StateFlow<NotesPackage?> = _notesPackage

    private val _param = MutableStateFlow(
        Param(
            tags = emptyList(),
            owner = emptyList(),
            start = null,
            end = null
        )
    )
    val param: StateFlow<Param> = _param

    init {
        observeFilter()
    }

    @OptIn(FlowPreview::class)
    private fun observeFilter() {
        viewModelScope.launch {
            _param
                .debounce(250)
                .distinctUntilChanged()
                .collectLatest {
                    runCatching {
                        repository.getAllRoomNotes(it, false)
                    }.onSuccess { result ->
                        _notesPackage.value = result
                    }
                }
        }
    }

    fun makePublic(note: Note) {
        viewModelScope.launch {
            repository.makePublic(note.id)
            refresh()
        }
    }

    fun deleteRoom(note: Note) {
        viewModelScope.launch {
            repository.deleteRoom(note.id)
            refresh()
        }
    }

    fun updateFilter(transform: Param.() -> Param) {
        _param.update(transform)
    }

    fun refresh() {
        viewModelScope.launch {
            runCatching {
                repository.getAllRoomNotes(_param.value, false)
            }.onSuccess { result ->
                _notesPackage.value = result
            }
        }
    }
}
@Composable
fun ArchivePage(
    navController: NavController,
    vm: ArchivePageViewModel
) {

    val notesPackage by vm.notesPackage.collectAsState()
    val param by vm.param.collectAsState()

    Menue("Общие заметки", false, navController) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {

            Spacer(modifier = Modifier.height(10.dp))

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
                    vm.updateFilter {
                        copy(tags = tags.toList())
                    }
                },

                startDate = param.start,
                onStartDateUpdate = { vm.updateFilter { copy(start = it) } },

                endDate = param.end,
                onEndDateUpdate = { vm.updateFilter { copy(end = it) } },
                onCreateNoteClick = {
                    navController.navigate("notePage/r_new")
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn {
                items(notesPackage?.allNotes?.size ?: 0) { index ->
                    notesPackage?.allNotes?.get(index)?.let { note ->
                        NotePreview(
                            note = note,
                            height = 250.dp,
                            onShareClick = {note -> vm.makePublic(note)},
                            onEditClick = { note -> navController.navigate("${Destinations.NOTE_PAGE.title}/r_${note.id}") },
                            onDeleteClick = { note -> vm.deleteRoom(note) },
                            onViewClick = { note -> navController.navigate("${Destinations.NOTE_PAGE.title}/vr_${note.id}") },
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}
