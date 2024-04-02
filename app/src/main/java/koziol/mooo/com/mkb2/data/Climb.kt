package koziol.mooo.com.mkb2.data

data class Climb(
    val uuid: String = "",
    val name: String,
    val setter: String,
    val grade: String,
    val gradeIndex: Int = 10,
    val deviation: Float,
    val rating: Float,
    val ascents: Int
) {

}
