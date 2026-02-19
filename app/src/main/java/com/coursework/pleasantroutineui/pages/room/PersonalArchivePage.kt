package com.coursework.pleasantroutineui.pages.room

import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


import RoomUserPreview
import android.content.ClipData
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.ui_services.DrawerContent
import com.coursework.pleasantroutineui.ui_services.Menue
import com.coursework.pleasantroutineui.ui_services.ProfileTopBar
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ChipColors
import androidx.compose.material3.DatePickerColors
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults.primaryChipColors
import com.coursework.pleasantroutineui.R
import com.coursework.pleasantroutineui.domain.Button
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.Note
import com.coursework.pleasantroutineui.pages.ExpandableContainer
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PersonalArchivePageViewModel (
    private val repository: INotesRepo
) : ViewModel() {

    private val accountId = MutableLiveData<String>()
    var allNotes = accountId.switchMap { id ->
        liveData {
            emit(repository.getAllNotes(id))
        }
    }
    var allTags = accountId.switchMap { id ->
        liveData {
            emit(repository.getAllTags(id))
        }
    }


    fun loadAccountId(id: String) {
        accountId.value = id
    }


}


@Composable
fun PersonalArchivePage(id: String, navController: NavController, vm: PersonalArchivePageViewModel) {

    vm.loadAccountId(id)
    val allNotes by vm.allNotes.observeAsState()


    Menue("Мои заметки", false, navController) { paddingValues ->
        var selectedDateRange by remember {
            mutableStateOf<Pair<Long?, Long?>?>(null)
        }

        var showModal by remember {
            mutableStateOf(false)
        }
        var selectedItems by remember { mutableStateOf(setOf<String>()) }
        var startDate by remember { mutableStateOf<Long?>(null) }
        var endDate by remember { mutableStateOf<Long?>(null) }
        Column (modifier = Modifier.padding(paddingValues)) {
            Spacer(modifier = Modifier.height(10.dp))

            ComplexFilter(
                vm,
                selectedItems,
                startDate,
                endDate,
                onStartDateUpdate = {
                    newStartDate ->
                    startDate = newStartDate
                },
                onEndDateUpdate = {
                        newEndDate ->
                    endDate = newEndDate
                },
                onUpdate = { newSet ->
                    selectedItems = newSet
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn{


                if (allNotes != null) {
                    items(allNotes!!.size) { item ->
                        var flagTag = false
                        if (selectedItems.isEmpty()) {
                            flagTag = true
                        } else {

                            for (tag in allNotes!![item].tags) {
                                if (selectedItems.contains(tag)) {
                                    flagTag = true
                                    break
                                }
                            }



                        }
                        var flagDate = false
                        val noteDate =  LocalDate
                            .parse(allNotes!![item].createTime)
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli()
                        if (startDate != null && endDate != null) {
                            if (noteDate >= startDate!! && noteDate <= endDate!!) {
                                flagDate = true
                            }
                        } else if (startDate != null) {
                            if (noteDate >= startDate!!) {
                                flagDate = true
                            }
                        } else if (endDate != null) {
                            if (noteDate <= endDate!!) {
                                flagDate = true
                            }
                        } else {
                            flagDate = true
                        }
                        if (flagTag && flagDate) {
                            NotePreview(allNotes!![item], 200.dp, navController)
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                    }
                }

            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplexFilter(vm: PersonalArchivePageViewModel,
                  selectedItems: Set<String>,
                  startDate: Long?,
                  endDate: Long?,
                  onStartDateUpdate: (Long?) -> Unit,
                  onEndDateUpdate: (Long?) -> Unit,

                  onUpdate: (Set<String>) -> Unit) {
    val allTags by vm.allTags.observeAsState()
    var expanded by remember { mutableStateOf(false) }

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
        ) {


            var showDialog by remember { mutableStateOf(false) }

            Button(
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(40.dp).weight(2f),
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
                    onConfirm = { start, end ->
                        onStartDateUpdate(start)
                        onEndDateUpdate(end)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
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
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.onSurface
                    ),
                    value = "Tags",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier.menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    ).height(50.dp)
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
//
                                onClick = {
                                    onUpdate(
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
                                onClick = { onUpdate(selectedItems - tag)},
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
        Spacer(modifier = Modifier.width(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp),

        ) {
            Button(
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(40.dp).weight(1f),
                onClick = { onUpdate(emptySet())
                            onStartDateUpdate(null)
                            onEndDateUpdate(null)
                }
            ) {
                Text("Отчистить выбор", color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.width(5.dp))

            Button(
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(40.dp).weight(1f),
                onClick = { }
            ) {
                Text("Создать заметку", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }





    //}
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDismiss: () -> Unit,
    onConfirm: (Long?, Long?) -> Unit
) {
    val state = rememberDateRangePickerState()

    DatePickerDialog(
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,

            titleContentColor = MaterialTheme.colorScheme.onBackground,
            headlineContentColor = MaterialTheme.colorScheme.onBackground,

            weekdayContentColor = MaterialTheme.colorScheme.onBackground,
            subheadContentColor = MaterialTheme.colorScheme.onBackground,

            yearContentColor = MaterialTheme.colorScheme.onBackground,
            currentYearContentColor = MaterialTheme.colorScheme.onBackground,
            selectedYearContentColor = MaterialTheme.colorScheme.onBackground,

            dayContentColor = MaterialTheme.colorScheme.onBackground,
            selectedDayContentColor = MaterialTheme.colorScheme.onBackground,
            todayContentColor = MaterialTheme.colorScheme.onBackground,

            todayDateBorderColor = MaterialTheme.colorScheme.onPrimary,

            // Чтобы убрать цветные кружки выбора
            selectedDayContainerColor = MaterialTheme.colorScheme.background,
            selectedYearContainerColor = MaterialTheme.colorScheme.background,
        ),
        onDismissRequest = onDismiss,
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
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
    ) {
        MaterialTheme(
            typography = MaterialTheme.typography.copy(
                headlineSmall = MaterialTheme.typography.bodyMedium,
                headlineLarge = MaterialTheme.typography.bodyMedium
            )
        ) {
            DateRangePicker(
                title = null,
                headline = {Text("Выбирите дату")},
                state = state,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.background,

                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    headlineContentColor = MaterialTheme.colorScheme.onBackground,

                    weekdayContentColor = MaterialTheme.colorScheme.onBackground,
                    subheadContentColor = MaterialTheme.colorScheme.onBackground,

                    yearContentColor = MaterialTheme.colorScheme.onBackground,
                    currentYearContentColor = MaterialTheme.colorScheme.onBackground,
                    selectedYearContentColor = MaterialTheme.colorScheme.onBackground,

                    dayContentColor = MaterialTheme.colorScheme.onBackground,
                    todayContentColor = MaterialTheme.colorScheme.onBackground,

                    todayDateBorderColor = MaterialTheme.colorScheme.onPrimary,


                    dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.primary,
                    dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onSurface,


                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    selectedDayContentColor = MaterialTheme.colorScheme.onSurface,


                    selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                )

            )
        }
    }
}



@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NotePreview(note: Note, height: Dp, navController: NavController) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier
            .padding(8.dp)
            .height(if (expanded) Dp.Unspecified else height)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .background(color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp))
        ) {
            Column(
                modifier = Modifier
                    .weight(9f)
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Text("Дата создания: " + note.createTime, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(5.dp))
                Text("Дата редактирования: " + note.lastEditTime, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(5.dp))
                Text(note.title, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
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


                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { expanded = !expanded }) {
                    Text(if (expanded) "Свернуть" else "Развернуть", color = MaterialTheme.colorScheme.onSurface)
                }


            }
            Column(
                modifier = Modifier.weight(1f)
            ) {

                IconButton(
                    onClick = {

                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.share),
                        contentDescription = "Поделиться",
                        modifier = Modifier.size(20.dp).background(MaterialTheme.colorScheme.onSurface)
                    )
                }

                IconButton(
                    onClick = {
                        navController.navigate(Destinations.NOTE_PAGE.title + "/" + note.id)
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Редактировать",
                        modifier = Modifier.size(20.dp).background(MaterialTheme.colorScheme.onSurface)
                    )
                }


            }
        }
    }


}


@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            colors = DatePickerDefaults.colors(
                dayContentColor = Color.Gray,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = Color(0xFF6200EE),
                todayContentColor = Color.Red,
                todayDateBorderColor = Color.Red
            ),
            title = {
                Text("Select date range")
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}

