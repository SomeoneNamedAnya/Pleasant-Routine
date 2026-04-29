package com.coursework.pleasantroutineui.pages.room

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.coursework.pleasantroutineui.domain.TaskType
import com.coursework.pleasantroutineui.domain.UserShort
import com.coursework.pleasantroutineui.dto.task.CreateTaskRequest
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
@HiltViewModel
class CreateTaskViewModel @Inject constructor(
    private val repository: ITaskRepo
) : ViewModel() {


    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _deadline = MutableStateFlow<LocalDateTime?>(null)
    val deadline = _deadline.asStateFlow()

    private val _taskType = MutableStateFlow(TaskType.ALL_MUST_APPROVE)
    val taskType = _taskType.asStateFlow()


    private val _residents = MutableStateFlow<List<UserShort>>(emptyList())
    val residents = _residents.asStateFlow()

    private val _selectedPerformers = MutableStateFlow<Set<Long>>(emptySet())
    val selectedPerformers = _selectedPerformers.asStateFlow()

    private val _selectedWatchers = MutableStateFlow<Set<Long>>(emptySet())
    val selectedWatchers = _selectedWatchers.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    private val _created = MutableSharedFlow<Unit>()
    val created = _created.asSharedFlow()

    fun loadResidents() {
        viewModelScope.launch {
            _isLoading.value = true
            _residents.value = repository.getMyRoomResidents()
            _isLoading.value = false
        }
    }


    fun updateTitle(value: String) {
        _title.value = value
    }

    fun updateDescription(value: String) {
        _description.value = value
    }

    fun updateDeadline(value: LocalDateTime) {
        _deadline.value = value
    }

    fun updateTaskType(type: TaskType) {
        _taskType.value = type
    }

    fun togglePerformer(userId: Long) {
        _selectedPerformers.value = _selectedPerformers.value.let { set ->
            if (userId in set) set - userId else set + userId
        }
    }

    fun toggleWatcher(userId: Long) {
        _selectedWatchers.value = _selectedWatchers.value.let { set ->
            if (userId in set) set - userId else set + userId
        }
    }



    fun create(roomId: Long) {
        viewModelScope.launch {
            if (_title.value.isBlank()) {
                _error.emit("Введите заголовок")
                return@launch
            }
            if (_description.value.isBlank()) {
                _error.emit("Введите описание")
                return@launch
            }
            if (_deadline.value == null) {
                _error.emit("Укажите дедлайн")
                return@launch
            }
            if (_selectedPerformers.value.isEmpty()) {
                _error.emit("Выберите хотя бы одного исполнителя")
                return@launch
            }
            if (_selectedWatchers.value.isEmpty()) {
                _error.emit("Выберите хотя бы одного наблюдателя")
                return@launch
            }

            _isSaving.value = true

            val deadlineInstant = _deadline.value!!
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toString()

            val req = CreateTaskRequest(
                title = _title.value.trim(),
                description = _description.value.trim(),
                deadline = deadlineInstant,
                type = _taskType.value.apiName,
                roomId = roomId,
                performerIds = _selectedPerformers.value.toList(),
                watcherIds = _selectedWatchers.value.toList()
            )

            val result = repository.createTask(req)

            _isSaving.value = false
            if (result != null) {
                _created.emit(Unit)
            } else {
                _error.emit("Не удалось создать задачу")
            }
        }
    }



    fun reset() {
        _title.value = ""
        _description.value = ""
        _deadline.value = null
        _taskType.value = TaskType.ALL_MUST_APPROVE
        _selectedPerformers.value = emptySet()
        _selectedWatchers.value = emptySet()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    roomId: Long,
    navController: NavController,
    vm: CreateTaskViewModel
) {

    LaunchedEffect(Unit) {
        vm.reset()
        vm.loadResidents()
    }


    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        vm.error.collect { snackbarHostState.showSnackbar(it) }
    }


    LaunchedEffect(Unit) {
        vm.created.collect {
            navController.popBackStack()
        }
    }

    val title by vm.title.collectAsState()
    val description by vm.description.collectAsState()
    val deadline by vm.deadline.collectAsState()
    val taskType by vm.taskType.collectAsState()
    val residents by vm.residents.collectAsState()
    val selectedPerformers by vm.selectedPerformers.collectAsState()
    val selectedWatchers by vm.selectedWatchers.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val isSaving by vm.isSaving.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Новая задача") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->

        if (isLoading) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            item {
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { vm.updateTitle(it) },
                    label = { Text("Заголовок") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }


            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { vm.updateDescription(it) },
                    label = { Text("Описание") },
                    minLines = 3,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }


            item {
                DeadlinePicker(
                    deadline = deadline,
                    onDeadlineSelected = { vm.updateDeadline(it) }
                )
            }


            item {
                TaskTypeSelector(
                    selected = taskType,
                    onSelect = { vm.updateTaskType(it) }
                )
            }
            item {
                Text(
                    "Исполнители",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(residents.size) { id ->
                UserCheckboxRow(
                    user = residents.get(id),
                    checked = residents.get(id).id in selectedPerformers,
                    onToggle = { vm.togglePerformer(residents.get(id).id) }
                )
            }

            item {
                Text(
                    "Наблюдатели",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(residents.size) { id ->
                UserCheckboxRow(
                    user = residents.get(id),
                    checked = residents.get(id).id in selectedWatchers,
                    onToggle = { vm.toggleWatcher(residents.get(id).id) }
                )
            }

            item {
                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { vm.create(roomId) },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Создать задачу", style = MaterialTheme.typography.titleMedium)
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeadlinePicker(
    deadline: LocalDateTime?,
    onDeadlineSelected: (LocalDateTime) -> Unit
) {
    val context = LocalContext.current
    val formatter = remember {
        DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm")
    }

    val displayText = deadline?.format(formatter) ?: "Не указан"

    Column {
        Text("Дедлайн", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(4.dp))

        Surface(

            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth().
            clickable {
                    val now = LocalDateTime.now()
                    val initialDate = deadline ?: now


                    android.app.DatePickerDialog(
                        context,
                        { _, year, month, day ->
                            android.app.TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    onDeadlineSelected(
                                        LocalDateTime.of(year, month + 1, day, hour, minute)
                                    )
                                },
                                initialDate.hour,
                                initialDate.minute,
                                true
                            ).show()
                        },
                        initialDate.year,
                        initialDate.monthValue - 1,
                        initialDate.dayOfMonth
                    ).show()
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayText,
                    color = if (deadline != null)
                        MaterialTheme.colorScheme.surface
                    else
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                )
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TaskTypeSelector(
    selected: TaskType,
    onSelect: (TaskType) -> Unit
) {
    Column {
        Text("Тип проверки", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TaskType.entries.forEach { type ->
                val isSelected = type == selected

                Surface(
                    onClick = { onSelect(type) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    border = if (isSelected)
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    else
                        null,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (type == TaskType.ALL_MUST_APPROVE)
                                Icons.Default.DoneAll
                            else
                                Icons.Default.Done,
                            contentDescription = null,
                            tint = if (isSelected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = type.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (type == TaskType.ALL_MUST_APPROVE)
                                "Все наблюдатели"
                            else
                                "Хотя бы один",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserCheckboxRow(
    user: UserShort,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(Modifier.width(8.dp))

        AsyncImage(
            model = user.photoLink,
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "${user.name} ${user.surname}",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
