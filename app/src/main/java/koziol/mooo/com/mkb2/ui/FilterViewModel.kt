package koziol.mooo.com.mkb2.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import koziol.mooo.com.mkb2.data.ClimbsRepository
import koziol.mooo.com.mkb2.data.HoldRole
import koziol.mooo.com.mkb2.data.HoldsRepository
import koziol.mooo.com.mkb2.data.KBHold
import kotlin.math.roundToInt

class FilterViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

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

    private fun updateFilterInRepository() {
        ClimbsRepository.activeFilter = ClimbsRepository.activeFilter.copy(
            holds = savedStateHandle["holds"] ?: "",
            minRating = ((savedStateHandle["minRating"] ?: 1f) * 10).roundToInt() / 10f,
            maxRating = ((savedStateHandle["maxRating"] ?: 3f) * 10).roundToInt() / 10f,
            minGradeIndex = (savedStateHandle["minGrade"] ?: 0) + 10,
            maxGradeIndex = (savedStateHandle["maxGrade"] ?: 0) + 10,
            minGradeDeviation = ((savedStateHandle["minDeviation"]
                ?: -0.5f) * 10).roundToInt() / 10f,
            maxGradeDeviation = ((savedStateHandle["maxDeviation"]
                ?: 0.5f) * 10).roundToInt() / 10f,
            minAscents = numOfAscentsOptions[savedStateHandle["minAscents"] ?: 0],
            setterName = "",
            includeMyAscents = (savedStateHandle["myAscents"]
                ?: FilterOptions.INCLUDE) == FilterOptions.INCLUDE || (savedStateHandle["myAscents"]
                ?: FilterOptions.EXCLUSIVE) == FilterOptions.EXCLUSIVE,
            onlyMyAscents = (savedStateHandle["myAscents"]
                ?: FilterOptions.INCLUDE) == FilterOptions.EXCLUSIVE,
            includeMyTries = (savedStateHandle["myTries"]
                ?: FilterOptions.INCLUDE) == FilterOptions.INCLUDE || (savedStateHandle["myTries"]
                ?: FilterOptions.EXCLUSIVE) == FilterOptions.EXCLUSIVE,
            onlyMyTries = (savedStateHandle["myTries"]
                ?: FilterOptions.INCLUDE) == FilterOptions.EXCLUSIVE,
        )
    }

    val numOfAscentsOptions = arrayOf(0, 1, 5, 10, 20, 30, 50, 100, 500, 1000)

    val minGrade = savedStateHandle.getStateFlow("minGrade", 0)
    val maxGrade = savedStateHandle.getStateFlow("maxGrade", gradeNames.size - 1)

    val minDeviation = savedStateHandle.getStateFlow("minDeviation", -0.5f)
    val maxDeviation = savedStateHandle.getStateFlow("maxDeviation", 0.5f)

    val minRating = savedStateHandle.getStateFlow("minRating", 1f)
    val maxRating = savedStateHandle.getStateFlow("maxRating", 3f)

    val minNumOfAscents = savedStateHandle.getStateFlow("minAscents", 0)

    val myAscents = savedStateHandle.getStateFlow("myAscents", FilterOptions.INCLUDE)
    val myTries = savedStateHandle.getStateFlow("myTries", FilterOptions.INCLUDE)
    val myBoulders = savedStateHandle.getStateFlow("myBoulders", FilterOptions.INCLUDE)

    val theirAscents = savedStateHandle.getStateFlow("theirAscents", FilterOptions.INCLUDE)
    val theirTries = savedStateHandle.getStateFlow("theirTries", FilterOptions.INCLUDE)
    val theirBoulders = savedStateHandle.getStateFlow("theirBoulders", FilterOptions.INCLUDE)

    fun updateGradeRange(min: Int, max: Int) {
        savedStateHandle["minGrade"] = min
        savedStateHandle["maxGrade"] = max
    }

    fun updateDeviationRange(min: Float, max: Float) {
        savedStateHandle["minDeviation"] = min
        savedStateHandle["maxDeviation"] = max
    }

    fun updateRatingRange(min: Float, max: Float) {
        savedStateHandle["minRating"] = min
        savedStateHandle["maxRating"] = max
    }

    fun updateMinNumOfAscents(min: Int) {
        savedStateHandle["minAscents"] = min
    }

    fun updateMyAscents(option: FilterOptions) {
        savedStateHandle["myAscents"] = option
    }

    fun updateMyTries(option: FilterOptions) {
        savedStateHandle["myTries"] = option
    }

    fun updateMyBoulders(option: FilterOptions) {
        savedStateHandle["myBoulders"] = option
    }

    fun updateTheirAscents(option: FilterOptions) {
        savedStateHandle["theirAscents"] = option
    }

    fun updateTheirTries(option: FilterOptions) {
        savedStateHandle["theirTries"] = option
    }

    fun updateTheirBoulders(option: FilterOptions) {
        savedStateHandle["theirBoulders"] = option
    }

    fun clearAllFilters() {
        savedStateHandle["minGrade"] = 0
        savedStateHandle["maxGrade"] = gradeNames.size - 1

        savedStateHandle["minDeviation"] = -0.5f
        savedStateHandle["maxDeviation"] = 0.5f

        savedStateHandle["minRating"] = 0f
        savedStateHandle["maxRating"] = 3f

        savedStateHandle["minAscents"] = 0

        savedStateHandle["myAscents"] = FilterOptions.INCLUDE
        savedStateHandle["myTries"] = FilterOptions.INCLUDE
        savedStateHandle["myBoulders"] = FilterOptions.INCLUDE
        savedStateHandle["theirAscents"] = FilterOptions.INCLUDE
        savedStateHandle["theirTries"] = FilterOptions.INCLUDE
        savedStateHandle["theirBoulders"] = FilterOptions.INCLUDE

        unselectAllHolds()
    }

    fun applyAllFilters() {
        updateFilterInRepository()
    }

    val selectedHolds = savedStateHandle.getStateFlow("holds", "")

    val selectedHoldsList = mutableStateListOf<KBHold>()

    fun applyHoldsFilter(respectHoldRoles: Boolean = true) {

        var holdsFilter = "%"
        selectedHoldsList.sortedBy { it.id }.forEach { hold ->
            holdsFilter += "p" + hold.id + "r"
            if (respectHoldRoles) {
                holdsFilter += hold.role.id
            }
            holdsFilter += "%"
        }
        savedStateHandle["holds"] = holdsFilter
    }

    fun unselectAllHolds() {
        selectedHoldsList.clear()
        savedStateHandle["holds"] = ""
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