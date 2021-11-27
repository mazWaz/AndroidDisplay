package com.example.antriandisplay.sockets

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import kotlinx.coroutines.flow.Flow

interface AppService {
    @Receive
    fun observeWebSocket(): Flow<WebSocket.Event>

    @Send
    fun information(data: String)

    @Receive
    fun observeTicker(): Flow<String>
}