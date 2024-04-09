package koziol.mooo.com.mkb2.data

data class Climb(
    val uuid: String = "",
    val name: String,
    val setter: String,
    val holdsString: String,
    val grade: String,
    val gradeIndex: Int = 10,
    val deviation: Float,
    val rating: Float,
    val ascents: Int
) {

    fun getHoldsList(): List<KBHold> {
        val holdsMap = HoldsRepository.getAllHoldsMap()

        val holdsList = mutableListOf<KBHold>()
        holdsString.split('p').forEach {holdString ->
            if (holdString.isNotEmpty()) {
                val holdInfo = holdString.split('r')
                val hold = holdsMap[holdInfo[0].toInt()]
                if (hold != null) {
                    hold.role = when (holdInfo[1].toInt()) {
                        12 -> StartHold
                        13 -> MiddleHold
                        14 -> FinishHold
                        15 -> FootHold
                        else -> {FootHold}
                    }
                    holdsList.add(hold)
                }

            }
        }
        return holdsList

/*        for (const frame of frames.split("p")) {
            if (frame.length > 0) {
                const [placementId, colorId] = frame.split("r");
                const circle = document.getElementById(`hold-${placementId}`);
                circle.setAttribute("stroke", colorMap[colorId]);
                circle.setAttribute("stroke-opacity", 1.0);
            }
        }*/
    }

}
