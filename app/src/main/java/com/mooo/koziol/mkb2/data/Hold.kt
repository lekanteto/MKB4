package com.mooo.koziol.mkb2.data

data class Hold(val id: Int, val x: Int, val y: Int, var role: HoldRole) {
    val xFraction: Float = x / 144F
    val yFraction: Float = (156 - y) / 156F
}

enum class HoldRole {
    StartHold {
        override val id = 12
        override val ledColor = 0x00FF00
        override val screenColor = 0xFF00FF00.toInt()
        override val fullName = "Start"
    },
    MiddleHold {
        override val id = 13
        override val ledColor = 0x00FFFF
        override val screenColor = 0xFF00FFFF.toInt()
        override val fullName = "Middle"
    },
    FinishHold {
        override val id = 14
        override val ledColor = 0xFF00FF
        override val screenColor = 0xFFFF00FF.toInt()
        override val fullName = "Middle"
    },
    FootHold {
        override val id = 15
        override val ledColor = 0xFFA500
        override val screenColor = 0xFFFFA500.toInt()
        override val fullName = "Foot only"
    }

    ;
    abstract val id: Int
    abstract val ledColor: Int
    abstract val screenColor: Int
    abstract val fullName: String
}