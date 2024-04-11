package koziol.mooo.com.mkb2.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import koziol.mooo.com.mkb2.data.FinishHold
import koziol.mooo.com.mkb2.data.FootHold
import koziol.mooo.com.mkb2.data.HoldsRepository
import koziol.mooo.com.mkb2.data.KBHold
import koziol.mooo.com.mkb2.data.MiddleHold
import koziol.mooo.com.mkb2.data.StartHold

class FilterHoldsViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val holds = savedStateHandle.getStateFlow("holds", "")

    val selectedHoldsList = mutableStateListOf<KBHold>()

    fun addOrUpdateHoldAt(tap: Offset) {
        val selectedHold = HoldsRepository.getNearestHold(tap)
        if (selectedHoldsList.contains(selectedHold)) {
            determineHoldColor(selectedHold)
            selectedHoldsList.remove(selectedHold)
            selectedHoldsList.add(selectedHold)
        } else {
            determineHoldColor(selectedHold)
            selectedHoldsList.add(selectedHold)
        }
    }

    fun removeHoldAt(tap: Offset) {
        val selectedHold = HoldsRepository.getNearestHold(tap)
        if (selectedHoldsList.contains(selectedHold)) {
            selectedHoldsList.remove(selectedHold)
        } else {
            addOrUpdateHoldAt(tap)
        }
    }

    private fun determineHoldColor(hold: KBHold) {
        if (selectedHoldsList.contains(hold)) {
            hold.role = when (hold.role) {
                is FootHold -> {
                    if (limitOfStartHoldsReached()) MiddleHold else StartHold
                }

                is StartHold -> MiddleHold
                is MiddleHold -> {
                    if (limitOfFinishHoldsReached()) FootHold else FinishHold
                }

                is FinishHold -> FootHold
                else -> {
                    hold.role
                }
            }
        } else {
            hold.role = when (hold.role) {
                is StartHold -> {
                    if (limitOfStartHoldsReached()) MiddleHold else StartHold
                }

                is FinishHold -> {
                    if (limitOfFinishHoldsReached()) MiddleHold else FinishHold
                }

                else -> {
                    hold.role
                }
            }
        }
    }

    private fun limitOfStartHoldsReached(): Boolean {
        var numOfStartHolds = 0
        selectedHoldsList.forEach {
            if (it.role == StartHold) {
                numOfStartHolds++
            }
        }
        return numOfStartHolds >= 2
    }

    private fun limitOfFinishHoldsReached(): Boolean {
        var numOfFinishHolds = 0
        selectedHoldsList.forEach {
            if (it.role == FinishHold) {
                numOfFinishHolds++
            }
        }
        return numOfFinishHolds >= 2
    }

    fun doNothing() {
        return
    }
}