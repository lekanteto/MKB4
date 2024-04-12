package koziol.mooo.com.mkb2.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import koziol.mooo.com.mkb2.data.HoldRole
import koziol.mooo.com.mkb2.data.HoldsRepository
import koziol.mooo.com.mkb2.data.KBHold

class BoardViewModel : ViewModel() {
    val selectedHoldsList = mutableStateListOf<KBHold>()

    fun updateHoldsSelection(tap: Offset) {
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

    fun removeHold(tap: Offset) {
        val selectedHold = HoldsRepository.getNearestHold(tap)
        if (selectedHoldsList.contains(selectedHold)) {
            selectedHoldsList.remove(selectedHold)
        } else {
            updateHoldsSelection(tap)
        }
    }

    private fun determineHoldColor(hold: KBHold) {
        if (selectedHoldsList.contains(hold)) {
            hold.role = when (hold.role) {
                HoldRole.FootHold -> {
                    if (limitOfStartHoldsReached()) HoldRole.MiddleHold else HoldRole.StartHold
                }

                HoldRole.StartHold -> HoldRole.MiddleHold
                HoldRole.MiddleHold -> {
                    if (limitOfFinishHoldsReached()) HoldRole.FootHold else HoldRole.FinishHold
                }

                HoldRole.FinishHold -> HoldRole.FootHold
            }
        } else {
            hold.role = when (hold.role) {
                HoldRole.StartHold -> {
                    if (limitOfStartHoldsReached()) HoldRole.MiddleHold else HoldRole.StartHold
                }
                HoldRole.FinishHold -> {
                    if (limitOfFinishHoldsReached()) HoldRole.MiddleHold else HoldRole.FinishHold
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
            if (it.role == HoldRole.StartHold) {
                numOfStartHolds++
            }
        }
        return numOfStartHolds >= 2
    }

    private fun limitOfFinishHoldsReached(): Boolean {
        var numOfFinishHolds = 0
        selectedHoldsList.forEach {
            if (it.role == HoldRole.FinishHold) {
                numOfFinishHolds++
            }
        }
        return numOfFinishHolds >= 2
    }
}