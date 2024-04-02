package koziol.mooo.com.mkb2.data

import android.database.sqlite.SQLiteDatabase
import android.util.Log

class ClimbRepository() {

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
            val currentClimb = Climb(
                name = name,
                setter = setter,
                grade = grade,
                deviation = gradeDeviation,
                rating = rating,
                ascents = ascents
            )
            climbsList.add(currentClimb)
        } while (climbsCursor.moveToNext())

        climbsCursor.close()
        return climbsList
    }

    private fun convertToArgs(filter: BaseFilter): Array<String> {
        Log.d("Mkb2 convert", filter.minGradeDeviation.toString())
        val ignoreSetter = if (filter.setterName.isEmpty()) "1" else "0"

        val ignoreMyAscentsInfo = if (filter.onlyMyAscents) "0" else "1"
        val includeMyAscents = if (filter.includeMyAscents) "0" else "1"
        val ignoreMyTriesInfo = if (filter.onlyMyTries) "0" else "1"
        val includeMyTries = if (filter.includeMyTries) "0" else "1"

        return arrayOf(
            filter.name,
            filter.minRating.toString(),
            filter.maxRating.toString(),
            filter.minGradeIndex.toString(),
            filter.maxGradeIndex.toString(),
            filter.minGradeDeviation.toString(),
            filter.maxGradeDeviation.toString(),
            filter.minAscents.toString(),
            filter.setterName,
            ignoreSetter,
            ignoreMyAscentsInfo,
            includeMyAscents,
            ignoreMyTriesInfo,
            includeMyTries
        )
    }

    fun getFilteredClimbs(filter: BaseFilter): List<Climb> {
        val climbsList = mutableListOf<Climb>()
        val climbsCursor = db.rawQuery(
            """
                SELECT climbs.uuid AS climbUuid,
                       climbs.name AS climbName,
                       climbs.setter_username AS setterName,
                       climb_stats.ascensionist_count AS ascents,
                       difficulty_grades.boulder_name AS gradeName,
                       climb_stats.difficulty_average - Round(climb_stats.difficulty_average) AS gradeDeviation,
                       climb_stats.quality_average AS rating,
                       Round(climb_stats.difficulty_average) AS gradeKey
                FROM   climbs
                       JOIN climb_stats
                         ON climb_stats.climb_uuid = climbs.uuid
                       JOIN difficulty_grades
                         ON difficulty_grades.difficulty = Round(climb_stats.difficulty_average)
                WHERE  layout_id = 1
                       AND climbs.is_listed = 1
                       AND is_draft = 0
                       AND frames_count = 1
                       AND edge_left > 0
                       AND edge_bottom > 0
                       AND edge_right < 144
                       AND edge_top < 156
                       AND climb_stats.angle = 40
                       AND
                       -- filters
                       climbs.name LIKE '%' || ? || '%' -- name
                       AND
                       climb_stats.quality_average BETWEEN ? AND ? -- min/max rating
                       AND 
                       climb_stats.display_difficulty BETWEEN ? AND ? -- min/max difficulty
                       AND 
                       CAST ( climb_stats.difficulty_average -
                       Round(climb_stats.difficulty_average) AS REAL ) 
                       BETWEEN ? AND ? -- min/max grade deviation
                       AND 
                       climb_stats.ascensionist_count >= ? -- min num of ascents
                       AND 
                       (climbs.setter_username = ? -- set by
                       OR ?)
                       AND 
                       (climbs.uuid IN (SELECT ascents.climb_uuid -- only my ascents
                       FROM   ascents)
                       OR ?)
                       AND 
                       (climbs.uuid NOT IN (SELECT ascents.climb_uuid -- only or exclude my ascents
                       FROM   ascents)
                       OR ?)
                       AND 
                       (climbs.uuid IN (SELECT bids.climb_uuid -- only my tries
                       FROM   bids)
                       OR ?)
                       AND 
                       (climbs.uuid NOT IN (SELECT bids.climb_uuid -- exclude my tries
                       FROM   bids)
                       OR ?)
                ORDER  BY climb_stats.quality_average DESC,
                          climb_stats.ascensionist_count DESC
                LIMIT  100 
            """.trimIndent(), convertToArgs(filter)
        )


        if (climbsCursor.moveToFirst()) do {
            var columnIndex = climbsCursor.getColumnIndexOrThrow("climbName")
            val name = climbsCursor.getString(columnIndex)

            columnIndex = climbsCursor.getColumnIndexOrThrow("setterName")
            val setter = climbsCursor.getString(columnIndex)

            columnIndex = climbsCursor.getColumnIndexOrThrow("ascents")
            val ascents = climbsCursor.getInt(columnIndex)

            columnIndex = climbsCursor.getColumnIndexOrThrow("gradeName")
            val grade = climbsCursor.getString(columnIndex)

            columnIndex = climbsCursor.getColumnIndexOrThrow("gradeDeviation")
            val gradeDeviation = climbsCursor.getFloat(columnIndex)
            Log.d("Mkb2", "Deviation: $gradeDeviation")

            columnIndex = climbsCursor.getColumnIndexOrThrow("rating")
            val rating = climbsCursor.getFloat(columnIndex)
            val currentClimb = Climb(
                name = name,
                setter = setter,
                grade = grade,
                deviation = gradeDeviation,
                rating = rating,
                ascents = ascents
            )
            climbsList.add(currentClimb)
        } while (climbsCursor.moveToNext())

        climbsCursor.close()
        return climbsList
    }
}

