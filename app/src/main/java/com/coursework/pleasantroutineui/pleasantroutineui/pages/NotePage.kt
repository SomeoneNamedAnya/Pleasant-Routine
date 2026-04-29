package com.coursework.pleasantroutineui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ChipColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo
import com.coursework.pleasantroutineui.ui_services.Menue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NoteMode {
    data object CreatePersonal : NoteMode()
    data object CreateRoom : NoteMode()
    data class EditPersonal(val id: String) : NoteMode()
    data class EditRoom(val id: String) : NoteMode()
    data class ViewPersonal(val id: String) : NoteMode()
    data class ViewRoom(val id: String) : NoteMode()
}

@HiltViewModel
class NotePageViewModel @Inject constructor(
    private val repository: INotesRepo
) : ViewModel() {

    private val _loadedNote = MutableStateFlow<Note?>(null)
    val loadedNote: StateFlow<Note?> = _loadedNote

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    private var mode: NoteMode = NoteMode.CreatePersonal
    private var initialized = false

    fun init(noteRef: String) {
        if (initialized) return
        initialized = true
        mode = when {
            noteRef == "p_new" -> NoteMode.CreatePersonal
            noteRef == "r_new" -> NoteMode.CreateRoom
            noteRef.startsWith("vp_") -> NoteMode.ViewPersonal(noteRef.removePrefix("vp_"))
            noteRef.startsWith("vr_") -> NoteMode.ViewRoom(noteRef.removePrefix("vr_"))
            noteRef.startsWith("p_") -> NoteMode.EditPersonal(noteRef.removePrefix("p_"))
            noteRef.startsWith("r_") -> NoteMode.EditRoom(noteRef.removePrefix("r_"))
            else -> NoteMode.CreatePersonal
        }
        when (val m = mode) {
            is NoteMode.EditPersonal -> viewModelScope.launch {
                runCatching { repository.getPersonalNote(m.id) }.onSuccess { _loadedNote.value = it }
            }
            is NoteMode.EditRoom -> viewModelScope.launch {
                runCatching { repository.getRoomNote(m.id) }.onSuccess { _loadedNote.value = it }
            }
            is NoteMode.ViewPersonal -> viewModelScope.launch {
                runCatching { repository.getPersonalNote(m.id) }.onSuccess { _loadedNote.value = it }
            }
            is NoteMode.ViewRoom -> viewModelScope.launch {
                runCatching { repository.getRoomNote(m.id) }.onSuccess { _loadedNote.value = it }
            }
            else -> {}
        }
    }

    fun save(title: String, content: String, tags: List<String>) {
        viewModelScope.launch {
            val isRoom = mode is NoteMode.CreateRoom || mode is NoteMode.EditRoom
            val note = Note(
                id = "",
                title = title,
                content = content,
                tags = tags,
                isPublic = if (isRoom) false else null,
                roomId = null,
                creatorId = null,
                creatorName = null,
                createdAt = "",
                editedAt = "",
                photoLinks = emptyList()
            )
            runCatching {
                when (val m = mode) {
                    is NoteMode.CreatePersonal -> repository.createPersonal(note)
                    is NoteMode.CreateRoom -> repository.createRoom(note)
                    is NoteMode.EditPersonal -> repository.editPersonal(m.id, note)
                    is NoteMode.EditRoom -> repository.editRoom(m.id, note)
                    is NoteMode.ViewPersonal -> println("ViewPersonal")
                    is NoteMode.ViewRoom -> println("ViewRoom")
                }
            }.onSuccess { _saved.value = true }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NotePage(noteRef: String, navController: NavController, vm: NotePageViewModel) {
    val loadedNote by vm.loadedNote.collectAsState()
    val saved by vm.saved.collectAsState()

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(listOf<String>()) }
    var tagInput by remember { mutableStateOf("") }

    LaunchedEffect(noteRef) { vm.init(noteRef) }
    LaunchedEffect(loadedNote) {
        loadedNote?.let {
            title = it.title
            content = it.content
            tags = it.tags
        }
    }
    LaunchedEffect(saved) { if (saved) navController.popBackStack() }

    val isViewMode = noteRef.startsWith("vp_") || noteRef.startsWith("vr_")
    val isCreate = noteRef == "p_new" || noteRef == "r_new"
    val pageTitle = when {
        isViewMode -> "Заметка"
        isCreate -> "Новая заметка"
        else -> "Редактировать заметку"
    }

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedTextColor = MaterialTheme.colorScheme.onBackground,
        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        cursorColor = MaterialTheme.colorScheme.onBackground,
        focusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.onBackground,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    )

    Menue(pageTitle, false, navController) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (isViewMode) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "Описание",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = content.ifBlank { "—" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (tags.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Теги",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.forEach { tag ->
                            AssistChip(
                                onClick = {},
                                label = { Text(tag, color = MaterialTheme.colorScheme.onSurface) },
                                colors = ChipColors(
                                    labelColor = MaterialTheme.colorScheme.onSurface,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    leadingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                    trailingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                                    disabledLeadingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                    disabledTrailingIconContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            } else {
                Text(
                    "Заголовок",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(6.dp))
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = fieldColors
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Описание",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(6.dp))
                TextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    maxLines = 15,
                    colors = fieldColors
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Теги",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = tagInput,
                        onValueChange = { tagInput = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        placeholder = { Text("Введите тег") },
                        colors = fieldColors
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val trimmed = tagInput.trim()
                            if (trimmed.isNotBlank() && trimmed !in tags) {
                                tags = tags + trimmed
                                tagInput = ""
                            }
                        },
                        modifier = Modifier.height(56.dp)
                    ) {
                        Text("+", style = MaterialTheme.typography.titleLarge)
                    }
                }

                if (tags.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.forEach { tag ->
                            AssistChip(
                                onClick = { tags = tags - tag },
                                label = {
                                    Text("$tag ×", color = MaterialTheme.colorScheme.onSurface)
                                },
                                colors = ChipColors(
                                    labelColor = MaterialTheme.colorScheme.onSurface,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    leadingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                    trailingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                                    disabledLeadingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                    disabledTrailingIconContentColor = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { vm.save(title, content, tags) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}
