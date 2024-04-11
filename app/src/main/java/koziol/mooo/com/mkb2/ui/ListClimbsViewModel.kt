package koziol.mooo.com.mkb2.ui

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import koziol.mooo.com.mkb2.data.Climb
import koziol.mooo.com.mkb2.data.ClimbRepository

class ListClimbsViewModel : ViewModel() {
    var climbsList = mutableStateListOf<Climb>()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            climbsList.addAll(ClimbRepository.getClimbsWithCurrentFilter())
        }
    }

    @OptIn(FlowPreview::class)
    fun onSearchTextChange(text: String) {
        _searchText.value = text
        viewModelScope.launch {
            _searchText.debounce(1000).collect { searchText ->
                if (ClimbRepository.currentFilter.name != searchText) {
                    ClimbRepository.currentFilter.name = searchText
                    climbsList.clear()
                    climbsList.addAll(ClimbRepository.getClimbsWithCurrentFilter())

                }
            }
        }
    }
}