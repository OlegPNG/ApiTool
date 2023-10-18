package com.beck.apitool

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.request
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val client = HttpClient(OkHttp)

    fun httpRequest(protocol: String, url: String) {
        viewModelScope.launch {
            if(protocol == "get") {
                val response = client.get(url)
            }
        }
    }
}