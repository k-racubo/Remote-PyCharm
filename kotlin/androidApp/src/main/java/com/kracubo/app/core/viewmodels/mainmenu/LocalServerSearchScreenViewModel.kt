package com.kracubo.app.core.viewmodels.mainmenu

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kracubo.app.core.networking.Client
import com.kracubo.app.core.nsdManager.NsdHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocalServerSearchScreenViewModel(application: Application) : AndroidViewModel(application) {
    var searchState by mutableStateOf(SearchState.MDNS_SEARCHING)

    private var searchJob: Job? = null
    private val appContext get() = getApplication<Application>().applicationContext

    private var nsdHelper: NsdHelper? = null

    private val prefs by lazy { appContext.getSharedPreferences("local_search_prefs", Context.MODE_PRIVATE) }

    private val KEY_CACHE_LOCAL_SEARCH_IP = "LOCAL_SEARCH_IP"
    private val KEY_CACHE_LOCAL_SEARCH_PORT = "LOCAL_SEARCH_PORT"

    init {
        nsdHelper = NsdHelper(appContext).apply {
            listener = object : NsdHelper.DiscoveryListener {
                override fun onServiceFound(ip: String, port: Int) {
                    viewModelScope.launch(Dispatchers.IO) {
                        if (searchState == SearchState.FOUND) return@launch

                        val connected = Client.connect(ip, port)

                        withContext(Dispatchers.Main) {
                            if (connected) {
                                println("successful connection!")
                                searchState = SearchState.FOUND
                                stopSearch()
                            } else {
                                println("handshake failed verification :(")
                                Toast.makeText(context,"Connection failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                override fun onError() { Log.e("Nsd Helper", "error") }
            }
        }

        startSearch()
    }

    fun startSearch() {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            searchState = SearchState.MDNS_SEARCHING

            nsdHelper?.discoverService()

            delay(5000)

            if (searchState == SearchState.MDNS_SEARCHING) {
                nsdHelper?.stopDiscovery()

                searchState = SearchState.CACHE_SEARCHING
                cacheSearching()

                Log.i("NsdViewModel", "Тайм-аут: сервис не найден")
            }
        }
    }

    fun startFullSearch(ip: String, port: Int){
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            searchState = SearchState.MANUAL_CONNECTING

            val connected = Client.connect(ip, port)

            withContext(Dispatchers.Main) {
                if (connected) {
                    searchState = SearchState.FOUND
                    stopSearch()
                } else {
                    searchState = SearchState.ERROR
                }
            }
        }
    } // дописать логику прямого подключения с searchJob и delay на время подключения иначе финальный ERROR

    fun stopSearch() {
        nsdHelper?.stopDiscovery()
        searchJob?.cancel()
    }

    fun cacheSearching() {
        searchJob = viewModelScope.launch {
            val cachedIp = prefs.getString(KEY_CACHE_LOCAL_SEARCH_IP, null)
            val cachedPort = prefs.getInt(KEY_CACHE_LOCAL_SEARCH_PORT, -1)

            if(cachedPort != -1 && cachedIp != null) {
                val connected = Client.connect(cachedIp, cachedPort)

                searchState = if (connected) SearchState.FOUND else SearchState.MANUAL_INPUT
            }
            else searchState = SearchState.MANUAL_INPUT
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopSearch()
        nsdHelper = null
    }
}

