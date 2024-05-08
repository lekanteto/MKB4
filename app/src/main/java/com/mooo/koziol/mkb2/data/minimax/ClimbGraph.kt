package com.mooo.koziol.mkb2.data.minimax

import com.mooo.koziol.mkb2.data.Hold
import com.mooo.koziol.mkb2.data.HoldRole
import com.mooo.koziol.mkb2.data.HoldsRepository
import kotlin.math.min

class ClimbGraph(
    private val holdString: String
) {
    private fun calcMoves(holds: List<Hold>): Set<Move> {
        val moves = mutableSetOf<Move>()
        for (i in 0..holds.size - 2) {
            for (j in i + 1..<holds.size) {
                val first = holds[i]
                val second = holds[j]
                moves.add(Move(first, second, HoldsRepository.getDistance(first, second)))
            }
        }
        return moves
    }

    private fun getMovesInvolvingHold(hold: Hold, moves: Set<Move>): Collection<Move> {
        return moves.filter { it.firstHold == hold || it.secondHold == hold }
    }

    private fun prims(holds: List<Hold>): Collection<Move> {
        val visitedHolds = mutableSetOf<Hold>()
        val allMoves = this.calcMoves(holds)
        val mstMoves = mutableSetOf<Move>()
        visitedHolds.add(holds.first())

        while (!visitedHolds.containsAll(holds)) {
            val nextMove = visitedHolds.flatMap { getMovesInvolvingHold(it, allMoves) }
                .filter { !visitedHolds.contains(it.firstHold) || !visitedHolds.contains(it.secondHold) }
                .minByOrNull { it.distance }

            if (nextMove != null) {
                visitedHolds.addAll(setOf(nextMove.firstHold, nextMove.secondHold))
                mstMoves.add(nextMove)
            }
        }
        return mstMoves
    }

    private fun getLongestDistance(moves: Collection<Move>): Float {
        return (moves.maxByOrNull { it.distance })?.distance ?: 0f
    }

    fun calcLongestMove(): Float {
        val holds = HoldsRepository.getHoldsListForHoldsString(holdString)
        val handHolds = holds.filter { hold -> hold.role != HoldRole.FootHold }
        val startHolds = holds.filter { hold -> hold.role == HoldRole.StartHold }
        val closerStartHold = getCloserHold(startHolds, handHolds)
        val relevantHolds =
            handHolds.filter { it.role != HoldRole.StartHold || it.id == closerStartHold?.id }

        val mstMoves = prims(relevantHolds)
        val distance = getLongestDistance(mstMoves)
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
