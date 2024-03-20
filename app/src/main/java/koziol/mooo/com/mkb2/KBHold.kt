package koziol.mooo.com.mkb2

data class KBHold(val id: Int, val x: Int, val y: Int) {
    val xFraction: Float = x / 144F
    val yFraction: Float = (156 - y) / 156F
}
