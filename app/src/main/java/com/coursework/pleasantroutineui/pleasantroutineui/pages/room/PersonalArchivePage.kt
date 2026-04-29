package com.coursework.pleasantroutineui.pages.room

import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.ui_services.Menue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ChipColors
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.viewModelScope
import com.coursework.pleasantroutineui.R
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.domain.NotesPackage
import com.coursework.pleasantroutineui.domain.Param
import com.coursework.pleasantroutineui.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.text.orEmpty


@HiltViewModel
class PersonalArchivePageViewModel @Inject constructor(
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
                        repository.getNotes(it)
                    }.onSuccess { result ->
                        _notesPackage.value = result
                    }
                }
        }
    }

    fun toRoomNote(note: Note) {
        viewModelScope.launch {
            repository.fromPersonalToRoom(note.id)
        }
    }

    fun deletePersonal(note: Note) {
        viewModelScope.launch {
            repository.deletePersonal(note.id)
            refresh()
        }
    }
    fun refresh() {
        viewModelScope.launch {
            runCatching {
                repository.getNotes(_param.value)
            }.onSuccess { result ->
                _notesPackage.value = result
            }
        }
    }

    fun updateFilter(transform: Param.() -> Param) {
        _param.update(transform)
    }
}

fun String.toLocalDate(): LocalDate {
    return Instant.parse(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

fun String.toSimpleDate(): String {
    if (isBlank()) return "-"

    val output = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

    return try {

        LocalDateTime.parse(this).format(output)
    } catch (_: Exception) {
        try {

            OffsetDateTime.parse(this).format(output)
        } catch (_: Exception) {
            try {
                Instant.parse(this)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .format(output)
            } catch (_: Exception) {
                this
            }
        }
    }
}

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

@Composable
fun PersonalArchivePage(navController: NavController, vm: PersonalArchivePageViewModel) {

    val notesPackage by vm.notesPackage.collectAsState()
    val param by vm.param.collectAsState()


    Menue("Мои заметки", false, navController) { paddingValues ->

        Column (modifier = Modifier.padding(paddingValues)) {
            Spacer(modifier = Modifier.height(10.dp))

            ComplexFilter(
                allTags = notesPackage?.allTags,
                allUsers = notesPackage?.allUser,

                selectedUserIds = param?.owner
                    ?.mapNotNull { it.id }
                    ?.toSet() ?: emptySet(),

                onSelectedUsersUpdate = { ids ->
                    val users = notesPackage?.allUser.orEmpty()

                    vm.updateFilter {
                        copy(
                            owner = users.filter { it.id in ids }
                        )
                    }
                },

                selectedItems = param?.tags?.toSet() ?: emptySet(),

                onSelectedItemsUpdate = { tags ->
                    vm.updateFilter {
                        copy(tags = tags.toList())
                    }
                },

                startDate = param?.start,
                onStartDateUpdate = { start ->
                    vm.updateFilter { copy(start = start) }
                },

                endDate = param?.end,
                onEndDateUpdate = { end ->
                    vm.updateFilter { copy(end = end) }
                },
                onCreateNoteClick = {
                    navController.navigate("notePage/p_new")
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn{

                items(notesPackage?.allNotes?.size ?: 0) { item ->
                    notesPackage?.allNotes?.get(item)?.let { note ->
                        NotePreview(
                            note = note,
                            height = 250.dp,
                            onShareClick = {note -> vm.toRoomNote(note)},
                            onEditClick = { note -> navController.navigate("${Destinations.NOTE_PAGE.title}/p_${note.id}") },
                            onDeleteClick = { note -> vm.deletePersonal(note) },
                            onViewClick = { note -> navController.navigate("${Destinations.NOTE_PAGE.title}/vp_${note.id}") },
                            refresh = {vm.refresh()}
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplexFilter(
    allTags: List<String>?,
    allUsers: List<User>?,

    selectedUserIds: Set<String>,
    onSelectedUsersUpdate: (Set<String>) -> Unit,

    selectedItems: Set<String>,
    onSelectedItemsUpdate: (Set<String>) -> Unit,

    startDate: Long?,
    onStartDateUpdate: (Long?) -> Unit,

    endDate: Long?,
    onEndDateUpdate: (Long?) -> Unit,

    showCreateNoteButton: Boolean = true,
    onCreateNoteClick: () -> Unit = {}
) {



    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
        ) {


            var showDialog by remember { mutableStateOf(false) }

            Button(
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .height(40.dp)
                    .weight(2f),
                onClick = { showDialog = true }
            ) {
                Text(
                    "Дата",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                modifier = Modifier.weight(2f),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                text =
                    "С: ${
                        startDate?.let {
                            Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .format(formatter)
                        } ?: "-"
                    }"
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                modifier = Modifier.weight(2f),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                text =
                    "По: ${
                        endDate?.let {
                            Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .format(formatter)
                        } ?: "-"
                    }"
            )

            if (showDialog) {
                DateRangePickerModal(
                    onDismiss = { showDialog = false },
                    onConfirm = { startMillis, endMillis ->

                        onStartDateUpdate(startMillis)
                        onEndDateUpdate(endMillis)
                    }
                )

            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        MultiSelectList(
            allTags,
            selectedItems,
            onSelectedItemsUpdate
        )
        Spacer(modifier = Modifier.width(10.dp))


        UserMultiSelectList(
            allUsers = allUsers,
            selectedUserIds = selectedUserIds,
            onSelectedUsersUpdate = onSelectedUsersUpdate
        )
        Spacer(modifier = Modifier.width(10.dp))



        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp),

        ) {
            Button(
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .height(40.dp)
                    .weight(1f),
                onClick = { onSelectedItemsUpdate(emptySet())
                            onSelectedUsersUpdate(emptySet())
                            onStartDateUpdate(null)
                            onEndDateUpdate(null)
                }
            ) {
                Text("Отчистить выбор", color = MaterialTheme.colorScheme.onSurface)
            }

            if (showCreateNoteButton) {
                Spacer(modifier = Modifier.width(5.dp))
                Button(
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    onClick = onCreateNoteClick
                ) {
                    Text("Создать заметку", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }

}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserMultiSelectList(
    allUsers: List<User>?,
    selectedUserIds: Set<String>,
    onSelectedUsersUpdate: (Set<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(2f)
        ) {

            TextField(
                value = "Сосед",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .height(50.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {

                allUsers
                    ?.distinctBy { it.id }
                    ?.forEach { user ->

                        val id = user.id ?: return@forEach
                        val fullName = "${user.name.orEmpty()} ${user.surname.orEmpty()}"

                        if (!selectedUserIds.contains(id)) {
                            DropdownMenuItem(
                                text = { Text(fullName) },
                                onClick = {
                                    onSelectedUsersUpdate(selectedUserIds + id)
                                }
                            )
                        }
                    }
            }
        }

        val scrollState = rememberScrollState()

        Box(
            modifier = Modifier
                .weight(4f)
                .height(80.dp)
        ) {
            FlowRow(
                modifier = Modifier.verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                allUsers?.distinctBy { it.id }?.forEach { user ->
                    val id = user.id ?: return@forEach
                    val fullName = "${user.name.orEmpty()} ${user.surname.orEmpty()}"

                    if (selectedUserIds.contains(id)) {

                        AssistChip(
                            onClick = {
                                onSelectedUsersUpdate(selectedUserIds - id)
                            },
                            label = {
                                Text(
                                    fullName,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            colors = ChipColors(
                                labelColor = MaterialTheme.colorScheme.onSurface,
                                containerColor = MaterialTheme.colorScheme.surface,
                                leadingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                trailingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                disabledContainerColor = MaterialTheme.colorScheme.onSurface,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                                disabledLeadingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                disabledTrailingIconContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDismiss: () -> Unit,
    onConfirm: (Long?, Long?) -> Unit
) {
    val state = rememberDateRangePickerState()

    val pickerColors = DatePickerDefaults.colors(
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        headlineContentColor = Color.White,
        weekdayContentColor = Color(0xFFC8E6C9),
        subheadContentColor = Color(0xFFA5D6A7),
        navigationContentColor = Color(0xFF81C784),

        yearContentColor = Color.White,
        currentYearContentColor = Color(0xFFA5D6A7),
        selectedYearContentColor = Color.Black,
        selectedYearContainerColor = Color(0xFF81C784),

        dayContentColor = Color.White,
        disabledDayContentColor = Color(0xFF757575),
        selectedDayContentColor = Color.Black,
        selectedDayContainerColor = Color(0xFF66BB6A),

        todayContentColor = Color(0xFFA5D6A7),
        todayDateBorderColor = Color(0xFF81C784),

        dividerColor = Color(0xFF2E2E2E),

        dateTextFieldColors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF2A2A2A),
            unfocusedContainerColor = Color(0xFF2A2A2A),
            disabledContainerColor = Color(0xFF2A2A2A),

            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            disabledTextColor = Color.Gray,

            focusedIndicatorColor = Color(0xFF81C784),
            unfocusedIndicatorColor = Color(0xFF555555),

            focusedLabelColor = Color(0xFFA5D6A7),
            unfocusedLabelColor = Color(0xFFA5D6A7),

            cursorColor = Color(0xFF81C784)
        )
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = pickerColors,

        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        state.selectedStartDateMillis,
                        state.selectedEndDateMillis
                    )
                    onDismiss()
                }
            ) {
                Text("OK", color = Color(0xFF81C784))
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = Color.White)
            }
        }
    ) {
        DateRangePicker(
            state = state,
            title = null,
            headline = {
                Text(
                    "Выберите даты",
                    color = Color.White
                )
            },
            colors = pickerColors
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NotePreview(
    note: Note,
    height: Dp,
    onShareClick: ((Note) -> Unit)? = null,
    onEditClick: ((Note) -> Unit)? = null,
    onDeleteClick: ((Note) -> Unit)? = null,
    onViewClick: ((Note) -> Unit)? = null,
    refresh: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .padding(8.dp)
            .height(if (expanded) Dp.Unspecified else height)
            .animateContentSize()
            .then(
                if (onViewClick != null)
                    Modifier.clickable { onViewClick(note) }
                else
                    Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .weight(9f)
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                print(note.createdAt)

                Text("Дата создания: ${note.createdAt.toLocalDate()}", color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(5.dp))
                Text("Дата редактирования: " + note.editedAt.toLocalDate(), color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(5.dp))
                Text(note.title, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(5.dp))
                val users = note.creatorName
                Text("Создатель: $users", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(5.dp))


                FlowRow(
                    maxLines = if (expanded) Int.MAX_VALUE else 1,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    note.tags.forEach { tag ->
                        AssistChip(
                            onClick = {},
                            label = { Text(tag, color = MaterialTheme.colorScheme.onBackground) },
                            colors = ChipColors(
                                labelColor = MaterialTheme.colorScheme.onBackground,
                                containerColor = MaterialTheme.colorScheme.background,
                                leadingIconContentColor = MaterialTheme.colorScheme.onBackground,
                                trailingIconContentColor = MaterialTheme.colorScheme.onBackground,
                                disabledContainerColor = MaterialTheme.colorScheme.onBackground,
                                disabledLabelColor = MaterialTheme.colorScheme.onBackground,
                                disabledLeadingIconContentColor = MaterialTheme.colorScheme.onBackground,
                                disabledTrailingIconContentColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }
                }


//                Spacer(modifier = Modifier.height(8.dp))
//
//                TextButton(onClick = { expanded = !expanded }) {
//                    Text(if (expanded) "Свернуть" else "Развернуть", color = MaterialTheme.colorScheme.onSurface)
//                }


            }
            Column(
                modifier = Modifier.weight(1f)
            ) {

                if (onShareClick != null) {
                    IconButton(
                        onClick = {
                            onShareClick.invoke(note)
                            refresh?.invoke()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.share),
                            contentDescription = "Поделиться",
                            modifier = Modifier
                                .size(20.dp)
                                .background(MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
                if (onEditClick != null) {
                    IconButton(
                        onClick = {
                            onEditClick.invoke(note)
                            refresh?.invoke()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.edit),
                            contentDescription = "Редактировать",
                            modifier = Modifier
                                .size(20.dp)
                                .background(MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }

                if (onDeleteClick != null) {
                    IconButton(
                        onClick = {
                            onDeleteClick.invoke(note)
                            refresh?.invoke()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.delete),
                            contentDescription = "Удалить",
                            modifier = Modifier.size(20.dp)
                                .background(MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }


            }
        }
    }


}


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MultiSelectList(
    allTags: List<String>?,
    selectedItems: Set<String>,
    onSelectedItemsUpdate: (Set<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
    ) {
        println(selectedItems)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.weight(2f)
        ) {

            TextField(
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.onSurface
                ),
                value = "Тэги",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .height(50.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                allTags?.forEach { option ->
                    if (!selectedItems.contains(option)) {
                        DropdownMenuItem(
                            colors = MenuItemColors(
                                textColor = MaterialTheme.colorScheme.onSurface,
                                leadingIconColor = MaterialTheme.colorScheme.onSurface,
                                trailingIconColor = MaterialTheme.colorScheme.onSurface,
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                            ),
                            text = { Text(option) },

                            onClick = {
                                onSelectedItemsUpdate(
                                    if (selectedItems.contains(option)) {

                                        selectedItems - option
                                    } else {
                                        selectedItems + option
                                    }
                                )
                            }
                        )
                    }
                }
            }


        }
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .weight(4f)
                .height(80.dp)
        ) {
            FlowRow(

                modifier = Modifier
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                allTags?.forEach { tag ->
                    if (selectedItems.contains(tag)) {


                        AssistChip(
                            onClick = { onSelectedItemsUpdate(selectedItems - tag)},
                            label = { Text(tag, color = MaterialTheme.colorScheme.onSurface) },
                            colors = ChipColors(
                                labelColor = MaterialTheme.colorScheme.onSurface,
                                containerColor = MaterialTheme.colorScheme.surface,
                                leadingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                trailingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                disabledContainerColor = MaterialTheme.colorScheme.onSurface,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                                disabledLeadingIconContentColor = MaterialTheme.colorScheme.onSurface,
                                disabledTrailingIconContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}

