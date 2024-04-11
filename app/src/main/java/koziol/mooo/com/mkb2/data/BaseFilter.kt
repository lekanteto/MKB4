package koziol.mooo.com.mkb2.data

data class BaseFilter(
    var name: String = "",
    var holds: String = "",
    val minRating: Float = 1f,
    val maxRating: Float = 3f,
    val minGradeIndex: Int = 10,
    val maxGradeIndex: Int = 33,
    val minGradeDeviation: Float = -0.5f,
    val maxGradeDeviation: Float = 0.5f,
    val minAscents: Int = 0,
    val setterName: String = "",
    val includeMyAscents: Boolean = true,
    val onlyMyAscents: Boolean = false,
    val includeMyTries: Boolean = true,
    val onlyMyTries: Boolean = false,
    )
