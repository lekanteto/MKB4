package koziol.mooo.com.mkb2.data

import android.graphics.Color

data class KBHold(val id: Int, val x: Int, val y: Int, var role: HoldRole) {
    val xFraction: Float = x / 144F
    val yFraction: Float = (156 - y) / 156F
}

interface HoldRole{
    val id: Int
    val ledColor: Int
    val screenColor: Int
    val fullName: String

}
object StartHold: HoldRole {
    override val id = 12
    override val ledColor = 0x00FF00
    override val screenColor = 0xFF00FF00.toInt()
    override val fullName = "Start"
}

object MiddleHold: HoldRole {
    override val id = 13
    override val ledColor = 0x00FFFF
    override val screenColor = 0xFF00FFFF.toInt()
    override val fullName = "Middle"
}

object FinishHold: HoldRole {
    override val id = 14
    override val ledColor = 0xFF00FF
    override val screenColor = 0xFFFF00FF.toInt()
    override val fullName = "Middle"
}
object FootHold: HoldRole {
    override val id = 15
    override val ledColor = 0xFFA500
    override val screenColor = 0xFFFFA500.toInt()
    override val fullName = "Foot only"
}