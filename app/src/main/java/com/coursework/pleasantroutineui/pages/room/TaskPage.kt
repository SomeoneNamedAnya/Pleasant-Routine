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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.room.util.copy
import coil.compose.AsyncImage
import com.coursework.pleasantroutineui.domain.Comment
import com.coursework.pleasantroutineui.domain.Task
import com.coursework.pleasantroutineui.domain.TaskStatus.Companion.fromDisplayName
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo
import com.coursework.pleasantroutineui.ui_services.Menue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

class TaskPageViewModel(
    private val repository: ITaskRepo
) : ViewModel() {

    private val _task = MutableStateFlow<Task?>(null)
    val task = _task.asStateFlow()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments = _comments.asStateFlow()

    fun loadAccountId(id: String) {
        viewModelScope.launch {
            _task.value = repository.getTask(id)
            _comments.value = repository.getComments(id)
        }
    }

    fun changeTypeOfTask(newType: String) {
        _task.value = _task.value?.copy(type = newType)
    }

    fun changeStatus(newStatus: String) {
        _task.value = _task.value?.copy(status = fromDisplayName(newStatus))
    }

    fun sendComment(text: String, author: User) {
        val newComment = Comment(
            id = UUID.randomUUID().toString(),
            text = text,
            author = author,
            date = LocalDateTime.now()
        )
        _comments.value = _comments.value + newComment
    }
}
@Composable
fun TaskScreen(
    id: String,
    navController: NavController,
    vm: TaskPageViewModel
) {

    LaunchedEffect(id) {
        vm.loadAccountId(id)
    }

    val task by vm.task.collectAsState()
    val comments by vm.comments.collectAsState()

    if (task == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Menue("Моя комната", false, navController) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {

            item {
                TaskHeader(task!!, vm)
            }

            item {
                Spacer(Modifier.height(10.dp))
                ParticipantsSection(task!!)
            }

            item {
                Spacer(Modifier.height(10.dp))
                CommentsSection(
                    comments = comments,
                    onSend = { text ->
                        vm.sendComment(text, task!!.creator)
                    }
                )
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

        chosenButton(
            listOf("Для каждого", "Хотя бы один"),
            task.type,
            { vm.changeTypeOfTask(it) },
            10.dp
        )

        Spacer(Modifier.height(8.dp))

        chosenButton(
            listOf("Открыта", "В работе", "На проверке", "Завершена"),
            task.status.displayName,
            { vm.changeStatus(it) },
            10.dp
        )
    }
}
@Composable
fun ParticipantsSection(task: Task) {

    Column(Modifier.padding(8.dp)) {

        Text("Создатель", style = MaterialTheme.typography.titleMedium)
        UserRow(task.creator)

        Spacer(Modifier.height(8.dp))

        Text("Исполнители", style = MaterialTheme.typography.titleMedium)
        task.performers.forEach {
            UserRow(it)
        }

        Spacer(Modifier.height(8.dp))

        Text("Наблюдатели", style = MaterialTheme.typography.titleMedium)
        task.watchers.forEach {
            UserRow(it)
        }
    }
}
@Composable
fun UserRow(user: User) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {

        AsyncImage(
            model = user.photoLink,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(Modifier.width(8.dp))

        Column {
            Text(user.firstName + " " + user.surname)
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

        comments.forEach {
            CommentItem(it)
        }

        Spacer(Modifier.height(8.dp))

        Row {
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
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

                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

                    cursorColor = MaterialTheme.colorScheme.onSurface,

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

        Text(
            comment.author.firstName + " " + comment.author.surname,
            style = MaterialTheme.typography.labelMedium
        )

        Surface(
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Text(
                comment.text,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun chosenButton(
    allTags: List<String>,
    chosenTag: String,
    onUpdateChosenTag: (String) -> Unit,
    width: Dp,
    // isInclusive: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(5.dp)
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
                            onUpdateChosenTag(option)
                            expanded = false
                        }
                    )

                }
            }



        }

        Spacer(modifier = Modifier.width(width))

        Text(
            text = chosenTag,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
        )
    }

}
