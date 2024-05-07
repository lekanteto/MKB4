package com.mooo.koziol.mkb2.data.minimax

import com.mooo.koziol.mkb2.data.Hold
import com.mooo.koziol.mkb2.data.HoldsRepository

data class ClimbGraph(
    val relevantHolds: List<Hold>
) {
    private lateinit var moves: MutableSet<Move>

    init {
        calcMoves()
    }

    private fun calcMoves() {
        moves = mutableSetOf()
        for (i in 0..relevantHolds.size-2) {
            for (j in i + 1..< relevantHolds.size) {
                val first = relevantHolds[i]
                val second = relevantHolds[j]
                moves.add(Move(first, second, HoldsRepository.getDistance(first, second)))
            }

        }
    }

    private fun getMovesInvolvingHold(hold: Hold): Collection<Move> {
        return moves.filter { it.firstHold == hold || it.secondHold == hold }
    }

    private fun prims(): Collection<Move> {
        val visitedHolds = mutableSetOf<Hold>()
        val moves = mutableSetOf<Move>()
        visitedHolds.add(relevantHolds.first())

        while (!visitedHolds.containsAll(relevantHolds)) {
            val nextMove = visitedHolds.flatMap { getMovesInvolvingHold(it) }
                .filter { !visitedHolds.contains(it.firstHold) || !visitedHolds.contains(it.secondHold) }
                .minByOrNull { it.distance }

            if (nextMove != null) {
                visitedHolds.addAll(setOf(nextMove.firstHold, nextMove.secondHold))
                moves.add(nextMove)
            }
        }
        return moves
    }

    private fun getLongestDistance(moves: Collection<Move>): Float {
        return (moves.maxByOrNull { it.distance })?.distance ?: 0f
    }

    fun calcLongestMove(): Float {
        return getLongestDistance(prims())
    }
}
