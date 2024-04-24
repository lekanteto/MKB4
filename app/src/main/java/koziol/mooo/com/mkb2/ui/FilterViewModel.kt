package koziol.mooo.com.mkb2.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import koziol.mooo.com.mkb2.data.ClimbFilter
import koziol.mooo.com.mkb2.data.ClimbsRepository
import koziol.mooo.com.mkb2.data.HoldRole
import koziol.mooo.com.mkb2.data.HoldsRepository
import koziol.mooo.com.mkb2.data.KBHold
import kotlin.math.roundToInt

class FilterViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val numOfAscentsOptions = arrayOf(0, 1, 5, 10, 20, 30, 50, 100, 500, 1000)
    val gradeNames = arrayOf(
        "4a/V0",
        "4b/V0",
        "4c/V0",
        "5a/V1",
        "5b/V1",
        "5c/V2",
        "6a/V3",
        "6a+/V3",
        "6b/V4",
        "6b+/V4",
        "6c/V5",
        "6c+/V5",
        "7a/V6",
        "7a+/V7",
        "7b/V8",
        "7b+/V8",
        "7c/V9",
        "7c+/V10"
    )

    val filter: StateFlow<ClimbFilter> = savedStateHandle.getStateFlow("filter", ClimbFilter(minGradeIndex = 0, maxGradeIndex = 17))

    private fun updateFilterInRepository() {
        val smoothedFilter = filter.value.copy(
            minGradeIndex = filter.value.minGradeIndex + 10,
            maxGradeIndex = filter.value.maxGradeIndex + 10,
            minRating = (filter.value.minRating * 10).roundToInt() / 10f,
            minGradeDeviation = (filter.value.minGradeDeviation * 10).roundToInt() / 10f,
            maxGradeDeviation = (filter.value.maxGradeDeviation * 10).roundToInt() / 10f,
            minAscents = numOfAscentsOptions[filter.value.minAscents]
        )
        ClimbsRepository.activeFilter = smoothedFilter
    }

    fun updateSetterName(name: String) {
        savedStateHandle["filter"] = filter.value.copy(name = name)
    }

    fun updateGradeRange(min: Int, max: Int) {
        savedStateHandle["filter"] = filter.value.copy(minGradeIndex = min, maxGradeIndex = max)
    }

    fun updateDeviationRange(min: Float, max: Float) {
        savedStateHandle["filter"] =
            filter.value.copy(minGradeDeviation = min, maxGradeDeviation = max)
    }

    fun updateRatingRange(min: Float, max: Float) {
        savedStateHandle["filter"] = filter.value.copy(minRating = min, maxRating = max)
    }

    fun updateMinNumOfAscents(min: Int) {
        savedStateHandle["filter"] = filter.value.copy(minAscents = min)
    }

    fun updateMyAscents(option: FilterOptions) {
        val include = (option == FilterOptions.INCLUDE) || (option == FilterOptions.EXCLUSIVE)
        savedStateHandle["filter"] = filter.value.copy(includeMyAscents = include)
    }

    fun updateMyTries(option: FilterOptions) {
        val include = (option == FilterOptions.INCLUDE) || (option == FilterOptions.EXCLUSIVE)
        savedStateHandle["filter"] = filter.value.copy(includeMyTries = include)
    }

    fun updateMyBoulders(option: FilterOptions) {

    }

    fun updateTheirAscents(option: FilterOptions) {

    }

    fun updateTheirTries(option: FilterOptions) {

    }

    fun updateTheirBoulders(option: FilterOptions) {

    }

    fun clearAllFilters() {
        savedStateHandle["filter"] = ClimbFilter()
        unselectAllHolds()
    }

    fun applyAllFilters() {
        updateFilterInRepository()
    }

    val selectedHolds = savedStateHandle.getStateFlow("holds", "")

    val selectedHoldsList = mutableStateListOf<KBHold>()

    fun applyHoldsFilter(respectHoldRoles: Boolean = true) {

        var holdsFilter = ""
        selectedHoldsList.sortedBy { it.id }.forEach { hold ->
            holdsFilter += "%p" + hold.id + "r"
            if (respectHoldRoles) {
                holdsFilter += hold.role.id
            }
        }
        holdsFilter += "%"
        savedStateHandle["filter"] = filter.value.copy(holds = holdsFilter)

    }

    fun unselectAllHolds() {
        selectedHoldsList.clear()
        savedStateHandle["filter"] = filter.value.copy(holds = "")
    }

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