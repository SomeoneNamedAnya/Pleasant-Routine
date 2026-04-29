package com.coursework.pleasantroutineui.pages.room
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewModelScope

import com.coursework.pleasantroutineui.ui_services.Menue

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon

import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.style.TextOverflow

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.Task
import com.coursework.pleasantroutineui.domain.TaskStatus
import com.coursework.pleasantroutineui.domain.TaskType
import com.coursework.pleasantroutineui.repo.interfaces.IRoomRepo
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class KanbanViewModel @Inject constructor(
    private val repository: ITaskRepo,
    private val roomRepository: IRoomRepo
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks = _tasks.asStateFlow()

    private val _selectedTab = MutableStateFlow(TaskStatus.OPEN)
    val selectedTab = _selectedTab.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _roomId = MutableStateFlow<Long?>(null)
    val roomId = _roomId.asStateFlow()


    fun loadMyTasks() {
        viewModelScope.launch {
            _isLoading.value = true
            _tasks.value = repository.getMyTasks()
            _isLoading.value = false
            _roomId.value = roomRepository.getRoomInfo().id?.toLong()
        }
    }

    fun loadTasksByRoom(roomId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _tasks.value = repository.getTasksByRoom(roomId)
            _isLoading.value = false
        }
    }

    fun selectTab(status: TaskStatus) {
        _selectedTab.value = status
    }
}
@Composable
fun KanbanScreen(
    navController: NavController,
    vm: KanbanViewModel
) {
    LaunchedEffect(Unit) {
        vm.loadMyTasks()
    }

    val selectedTab by vm.selectedTab.collectAsState()
    val tasks by vm.tasks.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val tabs = TaskStatus.entries

    Menue("Доска задач", false, navController) { paddingValues ->

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(paddingValues),

            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Destinations.CREATE_TASK.title + "/${vm.roomId.value}")
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Создать задачу"
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End

        ) { scaffoldPadding ->

            Column(
                modifier = Modifier
                    .padding(scaffoldPadding)
                    .fillMaxSize()
            ) {

                ScrollableTabRow(
                    selectedTabIndex = tabs.indexOf(selectedTab),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    tabs.forEach { status ->
                        Tab(
                            selected = selectedTab == status,
                            onClick = { vm.selectTab(status) },
                            text = { Text(status.displayName) }
                        )
                    }
                }

                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    val filtered = tasks.filter { it.status == selectedTab }

                    if (filtered.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "Нет задач",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            items(filtered, key = { it.id }) { task ->
                                TaskPreviewCard(task) {
                                    navController.navigate(
                                        Destinations.TASK_PAGE.title + "/${task.id}"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskPreviewCard(
    task: Task,
    onClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (task.type == TaskType.ALL_MUST_APPROVE)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = task.type.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Text("Создано: ${task.creationDate}", style = MaterialTheme.typography.labelSmall)
            Text("Дедлайн: ${task.deadline}", style = MaterialTheme.typography.labelSmall)

            Spacer(Modifier.height(4.dp))
            Text(
                "Создатель: ${task.creator.name} ${task.creator.surname}",
                style = MaterialTheme.typography.labelMedium
            )

            if (task.watchers.isNotEmpty()) {
                val approvedCount = task.watchers.count { it.approved }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Аппрувы: $approvedCount / ${task.watchers.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (approvedCount == task.watchers.size)
                        Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = task.description,
                maxLines = if (expanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))
            Text(
                text = if (expanded) "Свернуть" else "Развернуть",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}