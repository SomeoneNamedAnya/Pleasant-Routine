package com.coursework.pleasantroutineui.services

import android.util.Log
import com.coursework.pleasantroutineui.domain.ChatMessage
import com.coursework.pleasantroutineui.domain.WsOutgoingMessage
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class ChatWebSocketClient(
    private val okHttpClient: OkHttpClient,
    private val baseWsUrl: String
) {
    private val gson = Gson()
    private var webSocket: WebSocket? = null

    private val _incomingMessages = MutableSharedFlow<ChatMessage>(extraBufferCapacity = 64)
    val incomingMessages: SharedFlow<ChatMessage> = _incomingMessages

    private val _connectionState = MutableSharedFlow<ConnectionState>(extraBufferCapacity = 64)
    val connectionState: SharedFlow<ConnectionState> = _connectionState

    enum class ConnectionState { CONNECTED, DISCONNECTED, ERROR }

    fun connect(token: String) {
        val url = "$baseWsUrl/ws/chat?token=$token"
        val request = Request.Builder().url(url).build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WS", "Connected")
                _connectionState.tryEmit(ConnectionState.CONNECTED)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WS", "Message received: $text")
                try {
                    val message = gson.fromJson(text, ChatMessage::class.java)
                    _incomingMessages.tryEmit(message)
                } catch (e: Exception) {
                    Log.e("WS", "Failed to parse message", e)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                _connectionState.tryEmit(ConnectionState.DISCONNECTED)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WS", "WebSocket failure", t)
                _connectionState.tryEmit(ConnectionState.ERROR)
            }
        })
    }

    fun joinChat(chatId: Long) {
        val msg = WsOutgoingMessage(chatId = chatId, text = null)
        webSocket?.send(gson.toJson(msg))
    }

    fun sendMessage(chatId: Long, text: String) {
        val msg = WsOutgoingMessage(chatId = chatId, text = text)
        webSocket?.send(gson.toJson(msg))
    }

    fun disconnect() {
        webSocket?.close(1000, "User left")
        webSocket = null
    }
}