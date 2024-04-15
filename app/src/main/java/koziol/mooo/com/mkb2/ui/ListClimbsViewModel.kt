package koziol.mooo.com.mkb2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import koziol.mooo.com.mkb2.data.ClimbsRepository

class ListClimbsViewModel : ViewModel() {

    var climbList = ClimbsRepository.climbs

    //var climbs: StateFlow<List<Climb>> = ClimbsRepository.climbs

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()


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
}