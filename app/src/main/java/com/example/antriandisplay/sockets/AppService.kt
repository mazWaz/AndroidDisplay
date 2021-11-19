package com.example.antriandisplay.sockets

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import kotlinx.coroutines.flow.Flow

interface AppService {
    @Receive
    fun observeWebSocket(): Flow<WebSocket.Event>

    @Receive
    fun observeTicker(): Flow<String>
}