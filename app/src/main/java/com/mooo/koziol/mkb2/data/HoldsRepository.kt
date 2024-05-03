package com.mooo.koziol.mkb2.data

import android.database.sqlite.SQLiteDatabase
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object HoldsRepository {

    private lateinit var allHoldsMap: Map<Int, KBHold>

    private fun getHoldById(id: Int): KBHold? {
        return allHoldsMap[id]
    }

    fun getHoldsListForHoldsString(frames: String): List<KBHold> {
        val holdsList = mutableListOf<KBHold>()
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

    fun getHoldsStringForHoldsList(holds: List<KBHold>): String {
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

    private fun getHoldsFromDb(db: SQLiteDatabase): Map<Int, KBHold> {
        val holdsMap = HashMap<Int, KBHold>()
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

            holdsMap[id] = KBHold(id, x, y, role)
        }
        holdsCursor.close()
        return holdsMap
    }

    fun getNearestHold(offset: Offset): KBHold {
        lateinit var nearestHold: KBHold
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

    private fun getDistanceSquared(point: Offset, hold: KBHold): Float {
        return point.minus(Offset(hold.xFraction, hold.yFraction)).getDistanceSquared()
    }

    fun getLongestDistance(holds: List<KBHold>): Float {
        var maxDistance = 0f
        var minDistance: Float
        var ds: Float
        holds.forEach { first ->
            if (first.role != HoldRole.FootHold) {
                minDistance = Float.MAX_VALUE
                holds.forEach { second ->
                    if (second.id != first.id) {
                        if (second.role != HoldRole.FootHold) {
                            ds = (Offset(first.xFraction, first.yFraction) - Offset(
                                second.xFraction, second.yFraction
                            )).getDistanceSquared()
                            minDistance = minOf(minDistance, ds)
                        }

                    }
                }
                maxDistance = maxOf(maxDistance, minDistance)

            }
        }
        return maxDistance
    }
}