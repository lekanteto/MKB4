package koziol.mooo.com.mkb2.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import koziol.mooo.com.mkb2.data.BaseFilter
import koziol.mooo.com.mkb2.data.Climb
import koziol.mooo.com.mkb2.data.ClimbRepository

class ClimbsViewModel : ViewModel() {
    var climbsList = mutableStateListOf<Climb>()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            climbsList.addAll(ClimbRepository.getClimbsWithCurrentFilter())
        }
    }
}