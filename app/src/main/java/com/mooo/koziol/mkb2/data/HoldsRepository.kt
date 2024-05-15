package com.mooo.koziol.mkb2.data

import android.database.sqlite.SQLiteDatabase
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

object HoldsRepository {

    private lateinit var allHoldsMap: Map<Int, Hold>

    private fun getHoldById(id: Int): Hold? {
        return allHoldsMap[id]
    }

    fun getHoldsListForHoldsString(frames: String): MutableList<Hold> {
        val holdsList = mutableListOf<Hold>()
        frames.split('p').forEach { holdString ->
            if (holdString.isNotEmpty()) {
                val holdInfo = holdString.split('r')
                val hold = getHoldById(holdInfo[0].toInt())
                if (hold != null) {
                    hold.role = when (holdInfo[1].toInt()) {
                        12 -> HoldRole.StartHold
                        13 -> HoldRole.MiddleHold
                        14 -> HoldRole.FinishHold
                        15 -> HoldRole.FootHold
                        else -> HoldRole.FootHold
                    }
                    holdsList.add(hold)
                }

            }
        }
        return holdsList
    }

    fun getHoldsStringForHoldsList(holds: List<Hold>): String {
        var holdsString = ""
        holds.sortedBy { it.id }.forEach { hold ->
            holdsString += "p" + hold.id + "r" + hold.role.id
        }
        return holdsString
    }


    private val holdRoles = mapOf(
        HoldRole.StartHold.id to HoldRole.StartHold,
        HoldRole.MiddleHold.id to HoldRole.MiddleHold,
        HoldRole.FinishHold.id to HoldRole.FinishHold,
        HoldRole.FootHold.id to HoldRole.FootHold
    )

    suspend fun setup(db: SQLiteDatabase) {
        withContext(Dispatchers.IO) {
            allHoldsMap = getHoldsFromDb(db)
        }
    }

    private fun getHoldsFromDb(db: SQLiteDatabase): Map<Int, Hold> {
        val holdsMap = HashMap<Int, Hold>()
        val holdsCursor = db.rawQuery(
            """
                SELECT placements.id, holes.x, holes.y, placement_roles.id
                FROM placements 
                JOIN holes ON placements.hole_id=holes.id
                JOIN placement_roles ON placements.default_placement_role_id=placement_roles.id
                WHERE 
                	placements.layout_id=1 AND -- Kilterboard Original layout
                	holes.x BETWEEN 1 AND 143 AND holes.y BETWEEN 1 AND 155 -- dimensions of 12x12 w/ kickboard
                -- ORDER BY holes.x, holes.y
            """.trimIndent(), null
        )

        var id: Int
        var x: Int
        var y: Int
        var role: HoldRole

        while (holdsCursor.moveToNext()) {
            id = holdsCursor.getInt(0)
            x = holdsCursor.getInt(1)
            y = holdsCursor.getInt(2)
            role = holdRoles[holdsCursor.getInt(3)] ?: HoldRole.MiddleHold

            holdsMap[id] = Hold(id, x, y, role)
        }
        holdsCursor.close()
        return holdsMap
    }

    fun getNearestHold(offset: Offset): Hold {
        lateinit var nearestHold: Hold
        var nearestDistance = Float.MAX_VALUE
        allHoldsMap.forEach {
            val hold = it.value
            val currentDistance = getDistanceSquared(offset, hold)
            if (currentDistance < nearestDistance) {
                nearestHold = hold
                nearestDistance = currentDistance
            }
        }
        return nearestHold
    }

    private fun getDistanceSquared(point: Offset, hold: Hold): Float {
        return point.minus(Offset(hold.xFraction, hold.yFraction)).getDistanceSquared()
    }

    fun getLongestDistance(holds: List<Hold>): Float {
        var maxDistance = 0f
        var minDistance: Float
        var minStartDistance: Float = Float.MAX_VALUE
        var ds: Float
        holds.forEach { first ->
            ds = getShortestMove(first, holds)
            if (first.role == HoldRole.StartHold) {
                minStartDistance = min(minStartDistance, ds)
            } else {
                maxDistance = max(maxDistance, ds)
            }
            maxDistance = max(maxDistance, minStartDistance)
        }
        return maxDistance
    }

    private fun getShortestMove(hold: Hold, holds: List<Hold>): Float {
        var minDistance = Float.MAX_VALUE
        holds.forEach { other ->
            if (other.id != hold.id) {
                if (!(hold.role == HoldRole.StartHold && other.role == HoldRole.StartHold)) {
                    minDistance = min(minDistance, getDistance(hold, other))
                }
            }
        }
        return minDistance
    }

    fun getDistance(first: Hold, second: Hold): Float {
        return (Offset(first.x.toFloat(), first.y.toFloat()) - Offset(
            second.x.toFloat(), second.y.toFloat()
        )).getDistance()
    }

    fun getDistanceSquared(first: Hold, second: Hold): Float {
        return (Offset(first.x.toFloat(), first.y.toFloat()) - Offset(
            second.x.toFloat(), second.y.toFloat()
        )).getDistanceSquared()
    }
}