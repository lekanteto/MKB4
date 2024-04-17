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
import koziol.mooo.com.mkb2.data.ClimbsRepository
import koziol.mooo.com.mkb2.data.RestClient

class BoardViewModel : ViewModel() {

    val climb = ClimbsRepository.currentClimb

    fun moveToNextClimb(moveBackwards: Boolean = false) {
        if (moveBackwards) {
            ClimbsRepository.goToPreviousClimb()
        } else {
            ClimbsRepository.goToNextClimb()
        }
    }




}