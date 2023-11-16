package com.beck.apitool

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.Logging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.asStateFlow

enum class LoadingStatus {
    LOADING,
    DONE
}
/*
data class GridRow(
    url: String,
    key: String,
    Value: String

)


val gridItems: List<MutableStateFlows<GridRows>>

onGridRowChange(index: Int) {
    gridItems[index].value =
}
 */

enum class InputView {
    HEADERS,
    QUERY,
    BODY
}

enum class HTTPMethod {
    GET,
    POST,
    PUT,
    DELETE
}

data class GridRowState (
    val key: String,
    val value: String,
)
class MainViewModel: ViewModel() {
    private val client = HttpClient(OkHttp) {
        install(Logging)
        install(ContentNegotiation) {
            json()
        }
        //TODO: Install ContentNegotiation plugin
    }

    val url = MutableLiveData<String>()
    val requestBody = MutableLiveData<String>()
    val responseView = MutableLiveData<String>()

    private val _currentView = MutableStateFlow(InputView.BODY)
    val currentView = _currentView.asStateFlow()

    val isLoading = MutableLiveData(false)

    val headerState = mutableStateListOf<GridRowState>(GridRowState("Content-Type", "application/json"))
    val queryState = mutableStateListOf<GridRowState>(GridRowState("", ""))
    val gridState = mutableStateListOf<GridRowState>(GridRowState("", ""))

    private val _bodyState = MutableStateFlow("")
    val bodyState = _bodyState.asStateFlow()

    val spinnerState = MutableLiveData<Int>()

    fun httpRequest(protocol: String) {
        val requestUrl = url.value ?: ""
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = client.request(requestUrl) {
                    method = when(spinnerState.value) {
                        0 -> HttpMethod.Get
                        1 -> HttpMethod.Post
                        2 -> HttpMethod.Put
                        3 -> HttpMethod.Delete
                        else -> HttpMethod.Get
                    }
                    accept(io.ktor.http.ContentType.Any)
                    url {
                        for (header in headerState) {
                            if(header.key != "" && header.value != "") {
                                headers.append(header.key, header.value)
                            }
                        }
                        for (param in queryState) {
                            if(param.key != "" && param.value != "") {
                                parameters.append(param.key, param.value)
                            }
                        }
                    }
                    setBody(bodyState.value)
                }
                responseView.value = response.body()
            } catch (e: Exception) {
               // TODO: Make toast
                responseView.value = e.stackTraceToString()
            }
        }
        isLoading.value = false
    }

    fun setBody(body: String) {
       _bodyState.value = body
    }
    fun setCurrentView(view: InputView) {
        _currentView.value = view
    }
    fun addGridRow() {
        gridState.add(GridRowState("", ""))
    }
    fun onHeaderChange(index: Int, key: String?, value: String?) {
        val content = headerState[index]
        val newContent = GridRowState(key ?: content.key, value?: content.value)
        headerState[index] = newContent
    }
    fun addHeader() {
        headerState.add(GridRowState("",""))
    }
    fun removeHeader(index: Int) {
        headerState.removeAt(index)
    }
    fun onQueryChange(index: Int, key: String?, value: String?) {
        val content = queryState[index]
        val newContent = GridRowState(key ?: content.key, value?: content.value)
        queryState[index] = newContent
    }
    fun addQuery() {
        queryState.add(GridRowState("",""))
    }
    fun removeQuery(index: Int) {
        queryState.removeAt(index)
    }
    fun onGridInput(index: Int, key: String?, value: String?) {
        val content = gridState[index]
        val newContent = GridRowState(key ?: content.key, value?: content.value)
        gridState[index] = newContent
        //state = GridRowState(key = key ?: state.value.key, value ?: state.value.value)
    }

}