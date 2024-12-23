package com.mooo.koziol.mkb2.data

data class Ascent(
    val uuid: String = "",
    val climbUuid: String = "",
    val angle: Int = 0,
    val bids: Int = 0,
    val grade: String = "",
    val gradeIndex: Int = 10,
    val rating: Float = 1f,
    val comment: String = "",
    val climbedAt: String = "",
)
