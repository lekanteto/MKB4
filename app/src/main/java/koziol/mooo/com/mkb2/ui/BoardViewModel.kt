package koziol.mooo.com.mkb2.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import koziol.mooo.com.mkb2.data.FinishHold
import koziol.mooo.com.mkb2.data.FootHold
import koziol.mooo.com.mkb2.data.HoldsRepository
import koziol.mooo.com.mkb2.data.KBHold
import koziol.mooo.com.mkb2.data.MiddleHold
import koziol.mooo.com.mkb2.data.StartHold

class BoardViewModel : ViewModel() {
    private var allHoldsList: List<KBHold> = mutableListOf()
    private lateinit var holdsRepo: HoldsRepository

    init {
        CoroutineScope(Dispatchers.IO).launch {
            holdsRepo = HoldsRepository
        }
    }

    private val _uiState = MutableStateFlow(BoardUiState())
    val uiState: StateFlow<BoardUiState> = _uiState.asStateFlow()

    val selectedHoldsList = mutableStateListOf<KBHold>()

    var info by mutableStateOf("No tap yet.")
        private set

    fun updateInfo(newInfo: String) {
        info = newInfo
    }

    fun updateHoldsSelection(tap: Pair<Float, Float>) {
        val selectedHold = holdsRepo.getNearestHold(tap)
        if (selectedHoldsList.contains(selectedHold)) {
            determineHoldColor(selectedHold)
            selectedHoldsList.remove(selectedHold)
            selectedHoldsList.add(selectedHold)
        } else {
            determineHoldColor(selectedHold)
            selectedHoldsList.add(selectedHold)
        }
    }

    fun removeHold(tap: Pair<Float, Float>) {
        val selectedHold = holdsRepo.getNearestHold(tap)
        if (selectedHoldsList.contains(selectedHold)) {
            selectedHoldsList.remove(selectedHold)
        } else {
            updateHoldsSelection(tap)
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
        var numOfStartHolds = 0;
        selectedHoldsList.forEach {
            if (it.role == StartHold) {
                numOfStartHolds++
            }
        }
        return numOfStartHolds >= 2
    }

    private fun limitOfFinishHoldsReached(): Boolean {
        var numOfFinishHolds = 0;
        selectedHoldsList.forEach {
            if (it.role == FinishHold) {
                numOfFinishHolds++
            }
        }
        return numOfFinishHolds >= 2
    }
}