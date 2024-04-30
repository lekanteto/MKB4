package koziol.mooo.com.mkb2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import koziol.mooo.com.mkb2.data.ClimbFilter
import koziol.mooo.com.mkb2.data.ClimbsRepository
import koziol.mooo.com.mkb2.data.ConfigRepository
import koziol.mooo.com.mkb2.data.RestClient

class ListClimbsViewModel : ViewModel() {

    private val _searchText = MutableStateFlow(ClimbsRepository.activeFilter.name)
    val searchText = _searchText.asStateFlow()

    var climbList = ClimbsRepository.climbs

    val isSearching = ClimbsRepository.isQuerying

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading = _isDownloading.asStateFlow()

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
        CoroutineScope(Dispatchers.Main).launch {
            _isDownloading.value = true
            RestClient.downloadSharedData()
            val userId = ConfigRepository.getCurrentUserId() ?: 0
            RestClient.downloadUserData(userId)
            _isDownloading.value = false
        }
    }
}