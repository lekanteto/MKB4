package koziol.mooo.com.mkb2.data

import android.content.Context
import koziol.mooo.com.mkb2.KBHold

class HoldsRepository(context: Context) {

    private val myDb = OriginalDbOpenHelper(context).readableDatabase

    fun getAllHolds(): List<KBHold>{
        val holdsList = mutableListOf<KBHold>()
        val holdsCursor = myDb.rawQuery(
            """
                SELECT placements.id, holes.x, holes.y
                FROM placements JOIN holes ON placements.hole_id=holes.id
                WHERE 
                	placements.layout_id=1 AND -- Kilterboard Original layout
                	holes.x BETWEEN 1 AND 143 AND holes.y BETWEEN 1 AND 155 -- dimensions of 12x12 w/ kickboard
                ORDER BY holes.x, holes.y
            """.trimIndent(), null
        )

        var id: Int
        var x: Int
        var y: Int
        holdsCursor.moveToFirst()
        while (!holdsCursor.isAfterLast) {
            id = holdsCursor.getInt(0)
            x = holdsCursor.getInt(1)
            y = holdsCursor.getInt(2)
            holdsList.add(KBHold(id, x, y))
            holdsCursor.moveToNext()
        }
        holdsCursor.close()
        return holdsList
    }
}