package com.mooo.koziol.mkb2.data

import com.mooo.koziol.mkb2.data.minimax.ClimbGraph
import kotlin.math.min

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

    private fun getHandHolds(): List<Hold> {
        return getHoldsList().filter { hold ->  hold.role != HoldRole.FootHold}
    }

    private fun getStartHolds(): List<Hold> {
        return getHoldsList().filter { hold ->  hold.role == HoldRole.StartHold}
    }

    fun calcLongestMove(): Float {
        val handHolds = getHandHolds()
        val startHolds = getStartHolds()
        val closerStartHold = getCloserHold(startHolds, handHolds)
        val relevantHolds = handHolds.filter { it.role != HoldRole.StartHold || it.id == closerStartHold?.id }
        val distance = ClimbGraph(relevantHolds).calcLongestMove()
        return distance
    }

    private fun getShortestMove(hold: Hold, holds: List<Hold>): Float {
        var ds = Float.MAX_VALUE
        holds.forEach { other ->
            if (other.id != hold.id) {
                if (!(hold.role == HoldRole.StartHold && other.role == HoldRole.StartHold)) {
                    ds = min(ds, HoldsRepository.getDistanceSquared(hold, other))
                }
            }
        }
        return ds
    }

    private fun getCloserHold(startHolds: List<Hold>, handHolds: List<Hold>): Hold? {
        return startHolds.minByOrNull { getShortestMove(it, handHolds) }
    }


}
