package com.mooo.koziol.mkb2.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.mooo.koziol.mkb2.data.ClimbsRepository

class BoardViewModel : ViewModel() {

    val currentClimb = ClimbsRepository.currentClimb


    fun moveToNextClimb(moveBackwards: Boolean = false) {
        CoroutineScope(Dispatchers.IO).launch {
            val climbList = ClimbsRepository.climbs.value
            if (moveBackwards) {
                val index = climbList.indexOf(ClimbsRepository.currentClimb.value)
                ClimbsRepository.currentClimb.value =
                    climbList.getOrElse(index + 1) { ClimbsRepository.currentClimb.value }
            } else {
                val index = climbList.indexOf(ClimbsRepository.currentClimb.value)
                ClimbsRepository.currentClimb.value =
                    climbList.getOrElse(index - 1) { ClimbsRepository.currentClimb.value }
            }
        }
    }

}