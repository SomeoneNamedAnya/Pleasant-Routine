package com.coursework.pleasantroutineui.pages.room

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
import androidx.compose.material3.Button
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.coursework.pleasantroutineui.domain.Comment
import com.coursework.pleasantroutineui.domain.Task
import com.coursework.pleasantroutineui.domain.TaskStatus
import com.coursework.pleasantroutineui.domain.TaskStatus.Companion.fromDisplayName
import com.coursework.pleasantroutineui.domain.TaskType
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.domain.UserShort
import com.coursework.pleasantroutineui.domain.Watcher
import com.coursework.pleasantroutineui.dto.task.ApproveRequest
import com.coursework.pleasantroutineui.dto.task.ChangeStatusRequest
import com.coursework.pleasantroutineui.dto.task.CreateCommentRequest
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo
import com.coursework.pleasantroutineui.repo.prod.IUserRepo
import com.coursework.pleasantroutineui.ui_services.Menue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TaskPageViewModel @Inject constructor(
    private val repository: ITaskRepo,
    private val userRepo: IUserRepo
) : ViewModel() {

    private val _task = MutableStateFlow<Task?>(null)
    val task = _task.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _userId = MutableStateFlow<Long?>(null)
    val userId = _userId.asStateFlow()

    fun loadTask(taskId: Long) {
        viewModelScope.launch {
            _userId.value = userRepo.getSelfInfo().id?.toLong()
            _isLoading.value = true
            _task.value = repository.getTask(taskId)
            _comments.value = repository.getComments(taskId)
            _isLoading.value = false
        }
    }

    fun changeStatus(newDisplayName: String) {
        val task = _task.value ?: return
        val newStatus = TaskStatus.fromDisplayName(newDisplayName)

        viewModelScope.launch {
            val result = repository.changeStatus(
                taskId = task.id,
                req = ChangeStatusRequest(newStatus = newStatus.apiName)
            )
            result.onSuccess { _task.value = it }
            result.onFailure { _error.emit(it.message ?: "Ошибка") }
        }
    }

    fun sendComment(text: String) {
        val task = _task.value ?: return
        viewModelScope.launch {
            val comment = repository.addComment(
                taskId = task.id,
                req = CreateCommentRequest(text = text)
            )
            if (comment != null) {
                _comments.value = _comments.value + comment
            }
        }
    }

    fun approve(approved: Boolean) {
        val task = _task.value ?: return
        viewModelScope.launch {
            val result = repository.approve(
                taskId = task.id,
                req = ApproveRequest(approved = approved)
            )
            result.onSuccess { updatedWatcher ->
                _task.value = task.copy(
                    watchers = task.watchers.map { w ->
                        if (w.id == updatedWatcher.id) w.copy(approved = updatedWatcher.approved)
                        else w
                    }
                )
            }
            result.onFailure { _error.emit(it.message ?: "Ошибка аппрува") }
        }
    }


}
@Composable
fun TaskScreen(
    taskId: Long,
    navController: NavController,
    vm: TaskPageViewModel
) {
    LaunchedEffect(taskId) {
        vm.loadTask(taskId)
    }

    val task by vm.task.collectAsState()
    val comments by vm.comments.collectAsState()
    val isLoading by vm.isLoading.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        vm.error.collect { snackbarHostState.showSnackbar(it) }
    }

    if (isLoading || task == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { scaffoldPadding ->
        Menue("Задача", false, navController) { paddingValues ->
            LazyColumn(
                Modifier.padding(paddingValues).padding(scaffoldPadding).fillMaxSize()
            ) {
                item { TaskHeader(task!!, vm) }
                item {
                    Spacer(Modifier.height(10.dp))
                    ParticipantsSection(task!!, vm)
                }
                item {
                    Spacer(Modifier.height(10.dp))
                    CommentsSection(
                        comments = comments,
                        onSend = { vm.sendComment(it) }
                    )
                }
            }
        }
    }
}
@Composable
fun TaskHeader(task: Task, vm: TaskPageViewModel) {

    Column(Modifier.padding(8.dp)) {

        Text(task.title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text(task.description)

        Spacer(Modifier.height(8.dp))
        Text("Дата создания: ${task.creationDate}")
        Text("Дедлайн: ${task.deadline}")

        Spacer(Modifier.height(8.dp))

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = "Тип: ${task.type.displayName}",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(Modifier.height(8.dp))

        StatusSelector(
            currentStatus = task.status,
            onStatusChange = { vm.changeStatus(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusSelector(
    currentStatus: TaskStatus,
    onStatusChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val statuses = TaskStatus.entries

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Text("Статус: ", style = MaterialTheme.typography.titleSmall)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (currentStatus) {
                    TaskStatus.OPEN -> Color(0xFFBBDEFB)
                    TaskStatus.IN_PROGRESS -> Color(0xFFFFF9C4)
                    TaskStatus.IN_REVIEW -> Color(0xFFFFCCBC)
                    TaskStatus.DONE -> Color(0xFFC8E6C9)
                },
                modifier = Modifier.menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        currentStatus.displayName,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.surface
                    )
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                statuses.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status.displayName) },
                        onClick = {
                            onStatusChange(status.displayName)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ParticipantsSection(task: Task, vm: TaskPageViewModel) {

    Column(Modifier.padding(8.dp)) {

        // Создатель
        Text("Создатель", style = MaterialTheme.typography.titleMedium)
        UserShortRow(task.creator)

        Spacer(Modifier.height(8.dp))

        // Исполнители
        Text("Исполнители", style = MaterialTheme.typography.titleMedium)
        if (task.performers.isEmpty()) {
            Text("Нет исполнителей", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        } else {
            task.performers.forEach { UserShortRow(it) }
        }

        Spacer(Modifier.height(8.dp))


        Text("Наблюдатели", style = MaterialTheme.typography.titleMedium)
        if (task.watchers.isEmpty()) {
            Text("Нет наблюдателей", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        } else {
            task.watchers.forEach { watcher ->
                WatcherRow(
                    watcher = watcher,
                    isCurrentUser = watcher.id == vm.userId.collectAsState().value,
                    canApprove = task.status == TaskStatus.IN_REVIEW,
                    onToggleApprove = { newValue ->
                        vm.approve(newValue)
                    }
                )
            }


            Spacer(Modifier.height(4.dp))
            val approved = task.watchers.count { it.approved }
            val total = task.watchers.size
            val label = when (task.type) {
                TaskType.ALL_MUST_APPROVE -> "Нужно: все ($approved/$total)"
                TaskType.ANY_MUST_APPROVE -> "Нужно: хотя бы 1 ($approved/$total)"
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (
                    (task.type == TaskType.ALL_MUST_APPROVE && approved == total) ||
                    (task.type == TaskType.ANY_MUST_APPROVE && approved > 0)
                ) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun UserShortRow(user: UserShort) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        Text("${user.name} ${user.surname}")
    }
}

@Composable
fun WatcherRow(
    watcher: Watcher,
    isCurrentUser: Boolean,
    canApprove: Boolean,
    onToggleApprove: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {


        Column(modifier = Modifier.weight(1f)) {
            Text("${watcher.name} ${watcher.surname}")
            Text(
                text = if (watcher.approved) "Подтвердил" else "Ожидание",
                style = MaterialTheme.typography.labelSmall,
                color = if (watcher.approved) Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
        }

        if (isCurrentUser && canApprove) {
            if (watcher.approved) {
                OutlinedButton(onClick = { onToggleApprove(false) }) {
                    Text("Отозвать")
                }
            } else {
                Button(onClick = { onToggleApprove(true) }) {
                    Text("Подтвердить")
                }
            }
        }
    }
}

@Composable
fun CommentsSection(
    comments: List<Comment>,
    onSend: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Column(Modifier.fillMaxWidth().padding(8.dp)) {

        Text("Комментарии", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        if (comments.isEmpty()) {
            Text(
                "Пока нет комментариев",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        } else {
            comments.forEach { CommentItem(it) }
        }

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                placeholder = {
                    Text(
                        "Написать комментарий",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = MaterialTheme.colorScheme.surface,
                    unfocusedTextColor = MaterialTheme.colorScheme.surface,
                    cursorColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSend(text)
                        text = ""
                    }
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = comment.author.photoLink,
                contentDescription = null,
                modifier = Modifier.size(24.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "${comment.author.name} ${comment.author.surname}",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.width(8.dp))
            Text(
                comment.createdAt,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.padding(top = 2.dp, start = 30.dp)
        ) {
            Text(comment.text, modifier = Modifier.padding(8.dp))
        }
    }
}

