package koziol.mooo.com.mkb2.data

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlin.math.pow
import kotlin.math.sqrt

@SuppressLint("StaticFieldLeak")
object HoldsRepository {

    private lateinit var context: Context

    private lateinit var myDb: SQLiteDatabase

    lateinit var allHoldsList: List<KBHold>

    private val holdRoles = mapOf(
        StartHold.id to StartHold,
        MiddleHold.id to MiddleHold,
        FinishHold.id to FinishHold,
        FootHold.id to FootHold
    )

    fun setup(context: Context) {
        this.context = context
        myDb = OriginalDbOpenHelper(context).readableDatabase
        allHoldsList = getAllHolds()
    }

    private fun getAllHolds(): List<KBHold> {
        val holdsList = mutableListOf<KBHold>()
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
            holdsList.add(KBHold(id, x, y, role))
            holdsCursor.moveToNext()
        }
        holdsCursor.close()
        return holdsList
    }

    fun getNearestHold(coordinates: Pair<Float, Float>): KBHold {
        var nearestHold = allHoldsList.first()
        var nearestDistance = 2F
        allHoldsList.forEach {
            val currentDistance = getDistance(coordinates, it)
            if (currentDistance < nearestDistance) {
                nearestHold = it
                nearestDistance = currentDistance
            }
        }
        return nearestHold
    }

    private fun getDistance(point: Pair<Float, Float>, hold: KBHold): Float {
        return sqrt(
            (point.first - hold.xFraction).pow(2) + (point.second - hold.yFraction).pow(2)
        )
    }
}