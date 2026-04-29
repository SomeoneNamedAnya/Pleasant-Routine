package com.coursework.pleasantroutineui.pages.room
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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

import coil.compose.AsyncImage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.coursework.pleasantroutineui.config.TokenManager
import com.coursework.pleasantroutineui.domain.ChatMessage
import com.coursework.pleasantroutineui.domain.Participant
import com.coursework.pleasantroutineui.repo.prod.ChatRepository
import com.coursework.pleasantroutineui.services.ChatWebSocketClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepo: ChatRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _participants = MutableStateFlow<List<Participant>>(emptyList())
    val participants: StateFlow<List<Participant>> = _participants

    private val _currentMessage = MutableStateFlow("")
    val currentMessage: StateFlow<String> = _currentMessage

    private val _chatTitle = MutableStateFlow("Чат комнаты")
    val chatTitle: StateFlow<String> = _chatTitle

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var currentChatId: Long = -1
    private var currentUserId: Long = -1
    private var initialized = false

    fun loadRoomChat() {
        if (initialized) return
        initialized = true

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {

                val chat = chatRepo.getRoomChat()

                if (chat == null) {
                    _error.value = "Не удалось загрузить чат. Возможно, вы не привязаны к комнате."
                    _isLoading.value = false
                    return@launch
                }

                currentChatId = chat.id
                _chatTitle.value = chat.title

                _participants.value = chat.participants


                _messages.value = chatRepo.getMessages(chat.id)

                val token = tokenManager.getAccessToken()
                if (token != null) {
                    chatRepo.connectWebSocket(token)
                }

                _isLoading.value = false

            } catch (e: Exception) {
                _error.value = "Ошибка: ${e.message}"
                _isLoading.value = false
            }
        }

        viewModelScope.launch {
            chatRepo.connectionState.collect { state ->
                if (state == ChatWebSocketClient.ConnectionState.CONNECTED && currentChatId > 0) {
                    chatRepo.joinChat(currentChatId)
                }
            }
        }

        viewModelScope.launch {
            chatRepo.incomingMessages.collect { message ->
                if (message.chatId == currentChatId) {
                    val current = _messages.value
                    if (current.none { it.id == message.id }) {
                        _messages.value = current + message
                    }
                }
            }
        }
    }

    fun setCurrentUserId(userId: Long) {
        currentUserId = userId
    }

    fun onMessageChange(text: String) {
        _currentMessage.value = text
    }

    fun sendMessage() {
        val text = _currentMessage.value.trim()
        if (text.isEmpty() || currentChatId < 0) return

        chatRepo.sendMessage(currentChatId, text)
        _currentMessage.value = ""
    }

    fun isMyMessage(message: ChatMessage): Boolean {
        return message.senderId == currentUserId
    }

    override fun onCleared() {
        super.onCleared()
        chatRepo.disconnectWebSocket()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(
    userId: Long,
    navController: NavController,
    viewModel: ChatViewModel
) {
    val messages by viewModel.messages.collectAsState()
    val currentMessage by viewModel.currentMessage.collectAsState()
    val chatTitle by viewModel.chatTitle.collectAsState()
    val participants by viewModel.participants.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.setCurrentUserId(userId)
        viewModel.loadRoomChat()
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(chatTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.People, contentDescription = "Участники")
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

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages, key = { it.id }) { message ->
                        val sender = participants.find { it.id == message.senderId }

                        MessageBubble(
                            message = message,
                            participant = sender,
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
            Icon(Icons.Default.Send, contentDescription = "Отправить")
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    participant: Participant?,
    isMine: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMine) {
            AsyncImage(
                model = participant?.photoLink ?: message.senderPhotoLink,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isMine) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary,
            tonalElevation = 4.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (participant != null) "${participant.name} ${participant.surname}"
                    else "${message.senderName} ${message.senderSurname}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isMine) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isMine) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = formatTime(message.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isMine) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}