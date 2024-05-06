package com.mooo.koziol.mkb2.data

data class Climb(
    val uuid: String = "",
    val name: String = "",
    val setter: String = "",
    val holdsString: String = "",
    val grade: String = "",
    val gradeIndex: Int = 10,
    val deviation: Float = 0f,
    val rating: Float = 1f,
    val ascents: Int = 0
) {

    fun getHoldsList(): List<Hold> {
        return HoldsRepository.getHoldsListForHoldsString(holdsString)
    }
}
