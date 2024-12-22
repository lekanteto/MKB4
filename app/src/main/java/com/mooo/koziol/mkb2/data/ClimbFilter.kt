package com.mooo.koziol.mkb2.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClimbFilter(
    val name: String = "",
    val angle: Int = 40,
    val holds: String = "",
    val minRating: Float = 1f,
    val maxRating: Float = 3f,
    val minGradeIndex: Int = 10,
    val maxGradeIndex: Int = 27,
    val minGradeDeviation: Float = -0.5f,
    val maxGradeDeviation: Float = 0.5f,
    val minDistance: Float = 0f,
    val maxDistance: Float = 60f,
    val minAscents: Int = 0,
    val setterName: String = "",
    val includeMyAscents: Boolean = true,
    val onlyMyAscents: Boolean = false,
    val includeMyTries: Boolean = true,
    val onlyMyTries: Boolean = false,
    val onlyMyLikes: Boolean = false,
    val sortOrder: SortOrder = SortOrder.RATING,
    val sortDescending: Boolean = true,
    ) : Parcelable

enum class SortOrder {
    RATING, DIFFICULTY, AGE, ASCENTS
}