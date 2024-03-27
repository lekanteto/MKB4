package koziol.mooo.com.mkb2.data

import android.database.sqlite.SQLiteDatabase

class ClimbRepository () {

    companion object {
        lateinit var db: SQLiteDatabase
    }
    fun getClimbs(): List<Climb> {
        val climbsList = mutableListOf<Climb>()
        val climbsCursor = db.rawQuery(
            """
                SELECT name, setter_username, climb_stats.ascensionist_count, difficulty_grades.boulder_name, climb_stats.difficulty_average - round(climb_stats.difficulty_average), climb_stats.quality_average
                FROM climbs
                JOIN climb_stats on climb_stats.climb_uuid=climbs.uuid
                JOIN difficulty_grades on difficulty_grades.difficulty=round(climb_stats.difficulty_average)
                WHERE layout_id=1 AND
                climbs.is_listed=1 AND
                is_draft=0 AND
                frames_count=1 AND
                edge_left > 0 AND
                edge_bottom > 0 AND
                edge_right < 144 AND
                edge_top < 156 AND
                climb_stats.angle=40 AND
                climb_stats.ascensionist_count>5
                ORDER BY climb_stats.quality_average DESC, climb_stats.ascensionist_count DESC
                LIMIT 100
            """.trimIndent(), null
        )


        if (climbsCursor.moveToFirst()) do {
            val name = climbsCursor.getString(0)
            val setter = climbsCursor.getString(1)
            val ascents = climbsCursor.getInt(2)
            val grade = climbsCursor.getString(3)
            val gradeDeviation = climbsCursor.getFloat(4)
            val rating = climbsCursor.getFloat(5)
            val currentClimb = Climb(name, setter, grade, gradeDeviation, rating, ascents)
            climbsList.add(currentClimb)
        } while (climbsCursor.moveToNext())

        climbsCursor.close()
        return climbsList
    }
}

