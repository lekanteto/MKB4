package koziol.mooo.com.mkb2.ui

import koziol.mooo.com.mkb2.data.KBHold

data class ClimbsUiState(
    var minGrade: Int = 1,
    var maxGrade: Int = 20,
    var gradeDeviation: Float = 0f,

    var minRating: Float = 1f,
    var maxRating: Float = 3f,
    var minNumOfAscents: Int = 0,

    var myAscentsFilter: FilterOptions = FilterOptions.INCLUDE,
    var myTriesFilter: FilterOptions = FilterOptions.INCLUDE,

    var climbedByFollowees: FilterOptions = FilterOptions.INCLUDE,
    var setByFollowees: FilterOptions = FilterOptions.INCLUDE,
    var setterName: String = "",
    var circuitName: String = "",

    var selectedHolds: List<KBHold> = mutableListOf(),
    var sortBy: String = ""
)