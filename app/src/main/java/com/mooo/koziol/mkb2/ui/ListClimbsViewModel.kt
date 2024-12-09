package com.mooo.koziol.mkb2.ui

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mooo.koziol.mkb2.data.ClimbsRepository
import com.mooo.koziol.mkb2.data.ConfigRepository
import com.mooo.koziol.mkb2.data.RestClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ListClimbsViewModel : ViewModel() {

    private val _searchText = MutableStateFlow(ClimbsRepository.activeFilter.name)
    val searchText = _searchText.asStateFlow()

    val climbList = ClimbsRepository.climbs

    var currentAngle = ClimbsRepository.filterFlow.map{ filter -> filter.angle }

    val isSearching = ClimbsRepository.isQuerying

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading = _isDownloading.asStateFlow()

    val isLoggedIn = ConfigRepository.isLoggedIn

    init {
        viewModelScope.launch {
            searchText.debounce(1000).collectLatest { name ->
                if (name != ClimbsRepository.activeFilter.name) {
                    ClimbsRepository.activeFilter = ClimbsRepository.activeFilter.copy(name = name)
                }
            }
        }
    }
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun onSelectAngle(newAngle: Int) {
        ClimbsRepository.activeFilter = ClimbsRepository.activeFilter.copy(angle = newAngle)
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