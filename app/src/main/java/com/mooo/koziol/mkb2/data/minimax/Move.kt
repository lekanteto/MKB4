package com.mooo.koziol.mkb2.data.minimax

import com.mooo.koziol.mkb2.data.Hold

data class Move(
    val firstHold: Hold,
    val secondHold: Hold,
    val distance: Float
)
