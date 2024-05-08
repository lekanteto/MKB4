package com.mooo.koziol.mkb2.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalTime

object ClimbsRepository {

    private lateinit var db: SQLiteDatabase

    private val filterFlow = MutableStateFlow(ClimbFilter())
    var activeFilter: ClimbFilter
        get() = filterFlow.value
        set(value) {
            filterFlow.value = value
            CoroutineScope(Dispatchers.IO).launch {
                climbsList = getFilteredClimbs(value)
                _climbs.value = climbsList
            }
        }

    val currentClimb = MutableStateFlow(Climb())

    private var climbsList = emptyList<Climb>()

    private val _climbs = MutableStateFlow(climbsList)
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

        val holdsFilter = filter.holds.replace("p", "%p") + "%"

        return arrayOf(
            filter.name,
            holdsFilter,
            ignoreHolds,
            filter.minRating.toString(),
            filter.maxRating.toString(),
            (filter.minGradeIndex - 0.5f).toString(),
            (filter.maxGradeIndex + 0.49f).toString(),
            (filter.minGradeDeviation - 0.05f).toString(),
            (filter.maxGradeDeviation + 0.05f).toString(),
            filter.minAscents.toString(),
            filter.setterName,
            ignoreSetter,
            includeNotClimbedByMe,
            includeMyAscents,
            includeNotTriedByMe,
            includeMyTries
        )
    }

    private suspend fun getFilteredClimbs(filter: ClimbFilter): List<Climb> {
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

                val uuidIndex = climbsCursor.getColumnIndexOrThrow("climbUuid")
                val nameIndex = climbsCursor.getColumnIndexOrThrow("climbName")
                val setterIndex = climbsCursor.getColumnIndexOrThrow("setterName")
                val holdsIndex = climbsCursor.getColumnIndexOrThrow("holdsString")
                val ascentsIndex = climbsCursor.getColumnIndexOrThrow("ascents")
                val gradeIndex = climbsCursor.getColumnIndexOrThrow("gradeName")
                val deviationIndex = climbsCursor.getColumnIndexOrThrow("gradeDeviation")
                val ratingIndex = climbsCursor.getColumnIndexOrThrow("rating")


                while (climbsCursor.moveToNext()) {
                    climbsList.add(
                        Climb(
                            uuid = climbsCursor.getString(uuidIndex),
                            name = climbsCursor.getString(nameIndex),
                            setter = climbsCursor.getString(setterIndex),
                            holdsString = climbsCursor.getString(holdsIndex),
                            grade = climbsCursor.getString(gradeIndex),
                            deviation = climbsCursor.getFloat(deviationIndex),
                            rating = climbsCursor.getFloat(ratingIndex),
                            ascents = climbsCursor.getInt(ascentsIndex)
                        )
                    )
                }

                climbsCursor.close()
            }
            _isQuerying.update { false }
            return@withContext climbsList
        }
    }

    fun getAscentsFor(climb: Climb): List<Ascent> {
        val ascentsCursor = db.query(
            "ascents", arrayOf(
                "climb_uuid", "angle", "bid_count", "quality", "difficulty", "comment", "climbed_at"
            ), "climb_uuid = ?", arrayOf(climb.uuid), null, null, "climbed_at"
        )
        val ascentsList = mutableListOf<Ascent>()
        var currentAscent: Ascent
        while ((ascentsCursor.moveToNext())) {
            currentAscent = Ascent(
                climbedAt = ascentsCursor.getString(6)
            )
            ascentsList.add(currentAscent)
        }
        ascentsCursor.close()
        return ascentsList
    }

    fun getBidsFor(climb: Climb): List<Bid> {
        val ascentsCursor = db.query(
            "bids",
            arrayOf("climb_uuid", "angle", "bid_count", "comment", "climbed_at"),
            "climb_uuid = ?",
            arrayOf(climb.uuid),
            null,
            null,
            "climbed_at"
        )
        val bidsList = mutableListOf<Bid>()
        var currentBid: Bid
        while ((ascentsCursor.moveToNext())) {
            currentBid = Bid(
                climbedAt = ascentsCursor.getString(4)
            )
            bidsList.add(currentBid)
        }
        ascentsCursor.close()
        return bidsList
    }

    fun setup(db: SQLiteDatabase) {
        this.db = db
        storeDistances()
    }

    private fun storeDistances() {
        Log.d("MKB rep", "start to store distances ${LocalTime.now()}")
        db.delete("climb_cache_fields", null, null)

        val whereString = """
        climbs.layout_id = 1 -- KB Original
        AND climbs.is_listed = 1
        AND climbs.is_draft = 0 -- no drafts for now
        AND climbs.frames_count = 1 -- only boulders and no routes
        AND climbs.edge_left > 0 -- dimensions of 12x12 with kick board
        AND climbs.edge_bottom > 0
        AND climbs.edge_right < 144
        AND climbs.edge_top < 156
        """.trimIndent()
        val climbsCursor = db.query("climbs", arrayOf("uuid", "frames"), whereString, null, null, null, null)

        var climbUuid: String
        var frames: String
        var holdsList: List<Hold>
        var maxDistance: Float
        while (climbsCursor.moveToNext()) {
            climbUuid = climbsCursor.getString(0)
            frames = climbsCursor.getString(1)
            holdsList = HoldsRepository.getHoldsListForHoldsString(frames)
            maxDistance = HoldsRepository.getLongestDistance(holdsList)
            db.insert("climb_cache_fields", null, ContentValues().apply {
                put("climb_uuid", climbUuid)
                put("display_difficulty", maxDistance)
            })

        }
        climbsCursor.close()
        Log.d("MKB rep", "after distance calcs ${LocalTime.now()}")

    }
}

