package koziol.mooo.com.mkb2.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object RestClient {

    private lateinit var db: SQLiteDatabase

    private lateinit var client: HttpClient

    suspend fun downloadSharedData() {
        if (this::client.isInitialized) {
            withContext(Dispatchers.IO) {
                val syncResponse: SharedSyncResponse =
                    client.post("https://api.kilterboardapp.com/v1/sync") {
                        contentType(ContentType.Application.Json)
                        setBody(createSharedSyncRequestContent())
                    }.body()

                var climbsDate = ""
                var statsDate = ""
                syncResponse.pUT.sharedSyncs.forEach { row ->
                    if (row.tableName == "climbs") {
                        climbsDate = row.lastSynchronizedAt

                    }
                    if (row.tableName == "climb_stats") {
                        statsDate = row.lastSynchronizedAt

                    }
                }

                updateClimbs(syncResponse.pUT.climbs, climbsDate)
                updateClimbStats(syncResponse.pUT.climbStats, statsDate)
            }

        } else {
            Log.d("MKB4", "not inited")
        }
    }

    private suspend fun updateClimbStats(
        newClimbStats: List<SharedSyncResponse.PUT.ClimbStat>, date: String
    ) {
        withContext(Dispatchers.IO) {
            Log.d("Mkb4", "in updateClimbStats")
            newClimbStats.forEach { stats ->

                val row = ContentValues(9)
                row.put("climb_uuid", stats.climbUuid)
                row.put("angle", stats.angle)
                row.put("display_difficulty", stats.benchmarkDifficulty ?: stats.difficultyAverage)
                row.put("benchmark_difficulty", stats.benchmarkDifficulty)
                row.put("ascensionist_count", stats.ascensionistCount)
                row.put("difficulty_average", stats.difficultyAverage)
                row.put("quality_average", stats.qualityAverage)
                row.put("fa_username", stats.faUsername)
                row.put("fa_at", stats.faAt)



                if (row.get("display_difficulty") != null) {
                    db.insertWithOnConflict("climb_stats", null, row, CONFLICT_REPLACE)
                } else {
                    db.delete(
                        "climb_stats",
                        "climb_uuid = ? AND angle = ?",
                        arrayOf(stats.climbUuid, stats.angle.toString())
                    )
                }
            }

            val syncDateRow = ContentValues(2)
            syncDateRow.put("table_name", "climb_stats")
            syncDateRow.put("last_synchronized_at", date)
            db.update("shared_syncs", syncDateRow, "table_name = ?", arrayOf("climb_stats"))
        }
    }


    private suspend fun updateClimbs(newClimbs: List<SharedSyncResponse.PUT.Climb>, date: String) {
        Log.d("Mkb4", "in updateClimbs")
        withContext(Dispatchers.IO) {
            newClimbs.forEach { climb ->
                val row = ContentValues(18)
                row.put("uuid", climb.uuid)
                row.put("layout_id", climb.layoutId)
                row.put("setter_id", climb.setterId)
                row.put("setter_username", climb.setterUsername)
                row.put("name", climb.name)
                row.put("description", climb.description)
                row.put("hsm", climb.hsm)
                row.put("edge_left", climb.edgeLeft)
                row.put("edge_right", climb.edgeRight)
                row.put("edge_bottom", climb.edgeBottom)
                row.put("edge_top", climb.edgeTop)
                row.put("angle", climb.angle)
                row.put("frames_count", climb.framesCount)
                row.put("frames_pace", climb.framesPace)
                row.put("frames", climb.frames)
                row.put("is_draft", climb.isDraft)
                row.put("is_listed", climb.isListed)
                row.put("created_at", climb.createdAt)

                db.insertWithOnConflict("climbs", null, row, CONFLICT_REPLACE)
            }

            val syncDateRow = ContentValues(2)
            syncDateRow.put("table_name", "climbs")
            syncDateRow.put("last_synchronized_at", date)
            var numOfRows = db.update("shared_syncs", syncDateRow, "table_name = ?", arrayOf("climbs"))
            Log.d("MKB4", numOfRows.toString())
        }
    }


    private suspend fun createSharedSyncRequestContent(): String = withContext(Dispatchers.IO) {

        val syncsCursor = db.query("shared_syncs", null, null, null, null, null, null)

        val sharedSyncs = ArrayList<SharedSyncRequest.GET.Query.Syncs.SharedSync>(17)
        while (syncsCursor.moveToNext()) {
            val entry = SharedSyncRequest.GET.Query.Syncs.SharedSync(
                lastSynchronizedAt = syncsCursor.getString(1), tableName = syncsCursor.getString(0)
            )
            sharedSyncs.add(entry)
        }
        syncsCursor.close()

        val syncs = SharedSyncRequest.GET.Query.Syncs(sharedSyncs)
        val query = SharedSyncRequest.GET.Query(syncs = syncs)
        val get = SharedSyncRequest.GET(query = query)
        val sharedSyncRequest = SharedSyncRequest(gET = get)
        val json = Json { encodeDefaults = true }
        return@withContext json.encodeToString<SharedSyncRequest>(sharedSyncRequest)
    }

    fun close() {
        client.close()
    }

    suspend fun setup(db: SQLiteDatabase) {

        this.db = db
        withContext(Dispatchers.Default) {
            client = HttpClient(CIO) {
                expectSuccess = true
                install(Auth) {
                    bearer {
                        loadTokens {
                            BearerTokens("651d46009d9a0d59fe6123f53805da0f7acf11d1", "")
                        }
                    }
                }
                install(ContentNegotiation) {
                    json()
                }
            }
        }
    }
}