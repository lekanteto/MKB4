package koziol.mooo.com.mkb2.ui

import androidx.lifecycle.ViewModel
import koziol.mooo.com.mkb2.data.ClimbsRepository

class BoardViewModel : ViewModel() {

    val currentClimb = ClimbsRepository.currentClimb

    val climbs = ClimbsRepository.climbs.value

    fun moveToNextClimb(moveBackwards: Boolean = false) {
        if (moveBackwards) {
            val index = climbs.indexOf(currentClimb.value)
            currentClimb.value = climbs.getOrElse(index+1) { currentClimb.value }
        } else {
            val index = climbs.indexOf(currentClimb.value)
            currentClimb.value = climbs.getOrElse(index-1) { currentClimb.value }
        }
    }
}