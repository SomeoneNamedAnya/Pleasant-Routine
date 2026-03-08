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

import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope

import androidx.room.util.copy
import coil.compose.AsyncImage
import com.coursework.pleasantroutineui.domain.Comment

import com.coursework.pleasantroutineui.domain.TaskStatus.Companion.fromDisplayName
import com.coursework.pleasantroutineui.domain.User
import com.coursework.pleasantroutineui.repo.interfaces.INotesRepo

import com.coursework.pleasantroutineui.ui_services.Menue

import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.domain.Destinations
import com.coursework.pleasantroutineui.domain.Message
import com.coursework.pleasantroutineui.domain.Task
import com.coursework.pleasantroutineui.domain.TaskStatus
import com.coursework.pleasantroutineui.repo.interfaces.IChatRepo
import com.coursework.pleasantroutineui.repo.interfaces.ITaskRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
class ChatViewModel(
    private val chatRepo: IChatRepo
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _participants = MutableStateFlow<List<User>>(emptyList())
    val participants: StateFlow<List<User>> = _participants

    private val _currentMessage = MutableStateFlow("")
    val currentMessage: StateFlow<String> = _currentMessage

    private val _chatTitle = MutableStateFlow("")
    val chatTitle: StateFlow<String> = _chatTitle

    private var currentChatId: String = ""
    private var currentUserId: String = ""

    fun loadChat(chatId: String, userId: String) {
        currentChatId = chatId
        currentUserId = userId

        val chat = chatRepo.getChat(chatId)

        _chatTitle.value = chat.title
        _participants.value = chatRepo.getParticipants(chatId)
        _messages.value = chatRepo.getMessages(chatId)
    }

    fun onMessageChange(text: String) {
        _currentMessage.value = text
    }

    fun sendMessage() {
        val text = _currentMessage.value.trim()
        if (text.isEmpty() || currentChatId.isEmpty()) return

        val message = Message(
            id = UUID.randomUUID().toString(),
            idChat = currentChatId,
            text = text,
            senderId = currentUserId,
            timestamp = System.currentTimeMillis()
        )

        chatRepo.sendMessage(message)

        _messages.value = chatRepo.getMessages(currentChatId)
        _currentMessage.value = ""
    }

    fun isMyMessage(message: Message): Boolean {
        return message.senderId == currentUserId
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(
    chatId: String,
    userId: String,
    navController: NavController,
    viewModel: ChatViewModel
) {

    val messages by viewModel.messages.collectAsState()
    val currentMessage by viewModel.currentMessage.collectAsState()
    val chatTitle by viewModel.chatTitle.collectAsState()

    val listState = rememberLazyListState()

    println(chatId)
    println(userId)

    LaunchedEffect(chatId, userId) {

        viewModel.loadChat(chatId, userId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    Menue("Чат", false, navController) { paddingValues ->


        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(paddingValues),

            topBar = {
                TopAppBar(
                    title = { Text(chatTitle) },
                    windowInsets = WindowInsets(0),
                    actions = {
                        IconButton(
                            onClick = {
                                navController.navigate("participants/$chatId")
                            }
                        ) {
                            Icon(Icons.Default.People, contentDescription = "Participants")
                        }
                    }
                )
            },

            bottomBar = {
                BottomMessageBar(
                    text = currentMessage,
                    onTextChange = viewModel::onMessageChange,
                    onSendClick = { viewModel.sendMessage() }
                )
            }

        ) { padding ->

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->

                    val sender = viewModel.participants.value
                        .find { it.id == message.senderId }

                    if (sender != null) {
                        MessageBubble(
                            message = message,
                            user = sender,
                            isMine = viewModel.isMyMessage(message)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomMessageBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Введите сообщение") },
            maxLines = 5
        )

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(onClick = onSendClick) {
            Icon(Icons.Default.Send, contentDescription = "Send")
        }
    }

}
@Composable
fun MessageBubble(
    message: Message,
    user: User,
    isMine: Boolean
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {

        // Аватар слева (если не моё сообщение)
        if (!isMine) {
            AsyncImage(
                model = user.photoLink,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isMine)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.secondary,
            tonalElevation = 4.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                // Имя пользователя (И + Ф)
                Text(
                    text = "${user.firstName} ${user.surname}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isMine)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Текст сообщения
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isMine)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Время снизу справа
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = formatTime(message.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isMine)
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}