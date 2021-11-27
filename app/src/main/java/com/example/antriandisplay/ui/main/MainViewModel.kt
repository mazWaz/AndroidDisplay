package com.example.antriandisplay.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.antriandisplay.sockets.AppService
import com.tinder.scarlet.WebSocket
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val  service: AppService
): ViewModel() {

    sealed class Test(){
        class Success(val response: String): Test()
        class Fail(val error: String): Test()
        object Empty: Test()
    }

    sealed class Information(){
        class Success(val response: String): Information()
        class Fail(val error: String): Information()
        object Empty: Information()
    }

    private val _test = MutableStateFlow<Test>(Test.Empty)
    val _info = MutableStateFlow<Information>(Information.Empty)
    val test: StateFlow<Test> = _test
    val info: StateFlow<Information> = _info

    init {

        service.observeWebSocket().flowOn(Dispatchers.IO).onEach {
            event ->
            if(event !is WebSocket.Event.OnMessageReceived){
                Log.d("Event Main", event::class.java.simpleName)
            }

            if(event is WebSocket.Event.OnConnectionOpened<*>){
                service.information("info");
            }

        }.launchIn(viewModelScope)

        service.observeTicker().flowOn(Dispatchers.IO).onEach {
            _test.value = Test.Success(it)
            Log.d("Event Main Data", it)
        }.launchIn(viewModelScope)
    }



}