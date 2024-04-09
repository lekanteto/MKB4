package koziol.mooo.com.mkb2.data

import android.database.sqlite.SQLiteDatabase
import androidx.compose.ui.geometry.Offset

object HoldsRepository {

    private lateinit var myDb: SQLiteDatabase

    private lateinit var allHoldsMap: Map<Int, KBHold>
    fun getAllHoldsMap(): Map<Int, KBHold> {
        return allHoldsMap
    }

    private val holdRoles = mapOf(
        StartHold.id to StartHold,
        MiddleHold.id to MiddleHold,
        FinishHold.id to FinishHold,
        FootHold.id to FootHold
    )

    fun setup(db: SQLiteDatabase) {
        myDb = db
        allHoldsMap = getAllHolds()
    }

    private fun getAllHolds(): Map<Int, KBHold> {
        val holdsMap = HashMap<Int, KBHold>()
        val holdsCursor = myDb.rawQuery(
            """
                SELECT placements.id, holes.x, holes.y, placement_roles.id
                FROM placements 
                JOIN holes ON placements.hole_id=holes.id
                JOIN placement_roles ON placements.default_placement_role_id=placement_roles.id
                WHERE 
                	placements.layout_id=1 AND -- Kilterboard Original layout
                	holes.x BETWEEN 1 AND 143 AND holes.y BETWEEN 1 AND 155 -- dimensions of 12x12 w/ kickboard
                ORDER BY holes.x, holes.y
            """.trimIndent(), null
        )

        var id: Int
        var x: Int
        var y: Int
        var role: HoldRole?

        holdsCursor.moveToFirst()
        while (!holdsCursor.isAfterLast) {
            id = holdsCursor.getInt(0)
            x = holdsCursor.getInt(1)
            y = holdsCursor.getInt(2)
            role = holdRoles[holdsCursor.getInt(3)] ?: MiddleHold
            holdsMap[id] = KBHold(id, x, y, role)
            holdsCursor.moveToNext()
        }
        holdsCursor.close()
        return holdsMap
    }

    fun getNearestHold(offset: Offset): KBHold {
        lateinit var nearestHold: KBHold
        var nearestDistance = Float.MAX_VALUE
        allHoldsMap.forEach {val hold = it.value
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
}