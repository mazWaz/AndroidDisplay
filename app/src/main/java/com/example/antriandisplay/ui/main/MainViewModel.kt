package com.example.antriandisplay.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antriandisplay.sockets.AppService
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val  service: AppService
): ViewModel() {

    sealed class Test(){
        class Sucess(val response: String): Test()
        class Fail(val error: String): Test()
        object Empty: Test()
    }
    val _test = MutableStateFlow<Test>(Test.Empty)
    val test: StateFlow<Test> = _test

    init {
        service.observeWebSocket().flowOn(Dispatchers.IO).onEach {
            event ->
            if(event !is WebSocket.Event.OnMessageReceived){
                Log.d("Event Main", event::class.java.simpleName)
            }

            if(event is WebSocket.Event.OnConnectionOpened<*>){

            }
        }.launchIn(viewModelScope)
        service.observeTicker().flowOn(Dispatchers.IO).onEach {
            _test.value = Test.Sucess(it)
            Log.d("Event Main Data", it)
        }.launchIn(viewModelScope)
    }

}