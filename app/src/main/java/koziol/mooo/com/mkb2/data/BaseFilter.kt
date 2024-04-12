package koziol.mooo.com.mkb2.data

data class BaseFilter(
    var name: String = "",
    var holds: String = "",
    var minRating: Float = 1f,
    var maxRating: Float = 3f,
    var minGradeIndex: Int = 10,
    var maxGradeIndex: Int = 33,
    var minGradeDeviation: Float = -0.5f,
    var maxGradeDeviation: Float = 0.5f,
    var minAscents: Int = 0,
    var setterName: String = "",
    var includeMyAscents: Boolean = true,
    var onlyMyAscents: Boolean = false,
    var includeMyTries: Boolean = true,
    var onlyMyTries: Boolean = false,
    )
