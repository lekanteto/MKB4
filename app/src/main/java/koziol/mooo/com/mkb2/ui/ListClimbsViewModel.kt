package koziol.mooo.com.mkb2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import koziol.mooo.com.mkb2.data.Climb
import koziol.mooo.com.mkb2.data.ClimbsRepository

class ListClimbsViewModel : ViewModel() {
    private var _climbsList = MutableStateFlow(emptyList<Climb>())
    var climbList = _climbsList.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            _climbsList.value = ClimbsRepository.getClimbsWithCurrentFilter()
        }
    }

    @OptIn(FlowPreview::class)
    fun onSearchTextChange(text: String) {
        _searchText.value = text
        viewModelScope.launch {
            _searchText.debounce(1000).collect { searchText ->
                if (ClimbsRepository.currentFilter.name != searchText) {
                    ClimbsRepository.currentFilter.name = searchText
                    _climbsList.value = ClimbsRepository.getClimbsWithCurrentFilter()

                }
            }
        }
    }
}