package koziol.mooo.com.mkb2.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import koziol.mooo.com.mkb2.data.ClimbsRepository

class BoardViewModel : ViewModel() {

    val currentClimb = ClimbsRepository.currentClimb


    fun moveToNextClimb(moveBackwards: Boolean = false) {
        ClimbsRepository.moveToNextClimb(moveBackwards)
    }
}