package koziol.mooo.com.mkb2.data

import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ClimbsRepository {

    private lateinit var db: SQLiteDatabase

    var activeFilter = ClimbFilter()
        set(value) {
            field = value
            triggerListUpdate()
        }

    val _filter = MutableStateFlow(ClimbFilter())

    val currentClimb = MutableStateFlow(Climb())

    private val _climbs = MutableStateFlow(emptyList<Climb>())
    val climbs = _climbs.asStateFlow()

    private val _isQuerying = MutableStateFlow(false)
    val isQuerying = _isQuerying.asStateFlow()

    private fun convertToSqlArgs(filter: ClimbFilter): Array<String> {
        val ignoreSetter = if (filter.setterName.isEmpty()) "1" else "0"
        val ignoreHolds = if (filter.holds.isEmpty()) "1" else "0"

        val includeNotClimbedByMe = if (filter.onlyMyAscents) "0" else "1"
        val includeMyAscents = if (filter.includeMyAscents) "1" else "0"
        val includeNotTriedByMe = if (filter.onlyMyTries) "0" else "1"
        val includeMyTries = if (filter.includeMyTries) "1" else "0"

        return arrayOf(
            filter.name,
            filter.holds,
            ignoreHolds,
            filter.minRating.toString(),
            filter.maxRating.toString(),
            filter.minGradeIndex.toString(),
            filter.maxGradeIndex.toString(),
            filter.minGradeDeviation.toString(),
            filter.maxGradeDeviation.toString(),
            filter.minAscents.toString(),
            filter.setterName,
            ignoreSetter,
            includeNotClimbedByMe,
            includeMyAscents,
            includeNotTriedByMe,
            includeMyTries
        )
    }

    fun triggerListUpdate() {
        CoroutineScope(Dispatchers.IO).launch {
            _climbs.value = getFilteredClimbs(activeFilter)
        }
    }

    suspend fun getClimbsWithFilter(filter: ClimbFilter): Flow<List<Climb>> {
        return getFilteredClimbs(filter).
    }

    suspend fun getFilteredClimbs(filter: ClimbFilter): List<Climb> {
        return withContext(Dispatchers.IO) {
            _isQuerying.update { true }
            val climbsList = mutableListOf<Climb>()

            if (ClimbsRepository::db.isInitialized) {
                val climbsCursor = db.rawQuery(
                    """
                SELECT climbs.uuid AS climbUuid,
                       climbs.name AS climbName,
                       climbs.frames AS holdsString,
                       climbs.setter_username AS setterName,
                       climb_stats.ascensionist_count AS ascents,
                       difficulty_grades.boulder_name AS gradeName,
                       climb_stats.difficulty_average - Round(climb_stats.difficulty_average) AS gradeDeviation,
                       climb_stats.quality_average AS rating,
                       Round(climb_stats.difficulty_average) AS gradeKey
                FROM   climbs
                       LEFT JOIN climb_stats
                         ON climb_stats.climb_uuid = climbs.uuid
                       JOIN difficulty_grades
                         ON difficulty_grades.difficulty = Round(climb_stats.difficulty_average)
                WHERE  climbs.layout_id = 1 -- KB Original
                       AND climbs.is_listed = 1
                       AND climbs.is_draft = 0 -- no drafts for now
                       AND climbs.frames_count = 1 -- only boulders and no routes
                       AND climbs.edge_left > 0 -- dimensions of 12x12 with kickboard
                       AND climbs.edge_bottom > 0
                       AND climbs.edge_right < 144
                       AND climbs.edge_top < 156
                       AND climb_stats.angle = 40
                       -- filters
                       AND climbs.name LIKE '%' || ? || '%' -- name
                       AND (climbs.frames LIKE ? OR ?)
                       AND climb_stats.quality_average BETWEEN ? AND ? -- min/max rating
                       AND climb_stats.display_difficulty BETWEEN ? AND ? -- min/max difficulty
                       AND 
                       CAST ( climb_stats.difficulty_average -
                       Round(climb_stats.difficulty_average) AS REAL ) 
                       BETWEEN ? AND ? -- min/max grade deviation
                       AND climb_stats.ascensionist_count >= ? -- min num of ascents
                       AND (climbs.setter_username = ? OR ?) -- set by
                       AND 
                       (climbs.uuid IN (SELECT ascents.climb_uuid -- only my ascents
                       FROM ascents) OR ?)
                       AND 
                       (climbs.uuid NOT IN (SELECT ascents.climb_uuid -- exclude my ascents
                       FROM ascents) OR ?)
                       AND 
                       (climbs.uuid IN (SELECT bids.climb_uuid FROM bids)  -- only my tries
                       OR ?)
                       AND 
                       (climbs.uuid NOT IN (SELECT bids.climb_uuid FROM bids)  -- exclude my tries
                       OR ?)
                ORDER  BY climb_stats.quality_average DESC,
                          climb_stats.ascensionist_count DESC
                LIMIT  100 
                """.trimIndent(), convertToSqlArgs(filter)
                )


                while (climbsCursor.moveToNext()) {
                    var columnIndex = climbsCursor.getColumnIndexOrThrow("climbUuid")
                    val uuid = climbsCursor.getString(columnIndex)

                    columnIndex = climbsCursor.getColumnIndexOrThrow("climbName")
                    val name = climbsCursor.getString(columnIndex)

                    columnIndex = climbsCursor.getColumnIndexOrThrow("setterName")
                    val setter = climbsCursor.getString(columnIndex)

                    columnIndex = climbsCursor.getColumnIndexOrThrow("holdsString")
                    val holdsString = climbsCursor.getString(columnIndex)

                    columnIndex = climbsCursor.getColumnIndexOrThrow("ascents")
                    val ascents = climbsCursor.getInt(columnIndex)

                    columnIndex = climbsCursor.getColumnIndexOrThrow("gradeName")
                    val grade = climbsCursor.getString(columnIndex)

                    columnIndex = climbsCursor.getColumnIndexOrThrow("gradeDeviation")
                    val gradeDeviation = climbsCursor.getFloat(columnIndex)

                    columnIndex = climbsCursor.getColumnIndexOrThrow("rating")
                    val rating = climbsCursor.getFloat(columnIndex)

                    val currentClimb = Climb(
                        uuid = uuid,
                        name = name,
                        setter = setter,
                        holdsString = holdsString,
                        grade = grade,
                        deviation = gradeDeviation,
                        rating = rating,
                        ascents = ascents
                    )
                    climbsList.add(currentClimb)
                }

                climbsCursor.close()
            }
            _isQuerying.update { false }
            return@withContext climbsList
        }
    }

    fun setup(db: SQLiteDatabase) {
        this.db = db
    }
}

