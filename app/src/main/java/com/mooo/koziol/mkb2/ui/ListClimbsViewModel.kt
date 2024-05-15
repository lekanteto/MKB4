package com.mooo.koziol.mkb2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mooo.koziol.mkb2.data.ClimbsRepository
import com.mooo.koziol.mkb2.data.ConfigRepository
import com.mooo.koziol.mkb2.data.RestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class ListClimbsViewModel : ViewModel() {

    private val _searchText = MutableStateFlow(ClimbsRepository.activeFilter.name)
    val searchText = _searchText.asStateFlow()

    var climbList = ClimbsRepository.climbs

    val isSearching = ClimbsRepository.isQuerying

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading = _isDownloading.asStateFlow()

    val isLoggedIn = ConfigRepository.isLoggedIn

    @OptIn(FlowPreview::class)
    fun onSearchTextChange(text: String) {
        _searchText.value = text
        viewModelScope.launch {
            _searchText.debounce(1000).collect { searchText ->
                if (ClimbsRepository.activeFilter.name != searchText) {
                    ClimbsRepository.activeFilter =
                        ClimbsRepository.activeFilter.copy(name = searchText)
                }
            }
        }
    }

    fun downloadSyncTables() {
        CoroutineScope(Dispatchers.IO).launch {
            _isDownloading.value = true
            RestClient.downloadSharedData()
            val userId = ConfigRepository.getCurrentUserId()
            if (userId != null) {
                RestClient.downloadUserData(userId)

            }
            _isDownloading.value = false
        }
    }
}