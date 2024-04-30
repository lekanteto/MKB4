package com.mooo.koziol.mkb2.data

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
                val syncResponse: SyncResponse =
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
        newClimbStats: List<SyncResponse.PUT.ClimbStat>, date: String
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

    private suspend fun updateClimbs(newClimbs: List<SyncResponse.PUT.Climb>, date: String) {
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
            val numOfRows =
                db.update("shared_syncs", syncDateRow, "table_name = ?", arrayOf("climbs"))
            Log.d("MKB4", numOfRows.toString())
        }
    }

    suspend fun downloadUserData(userId: Int) {
        if (this::client.isInitialized) {
            withContext(Dispatchers.IO) {
                val requestBody = createUserSyncRequestContent(userId)
                val syncResponse: SyncResponse =
                    client.post("https://api.kilterboardapp.com/v1/sync") {
                        contentType(ContentType.Application.Json)
                        setBody(requestBody)
                    }.body()

                var ascentsDate = ""
                var bidsDate = ""
                syncResponse.pUT.userSyncs.forEach { row ->
                    if (row.tableName == "ascents") {
                        ascentsDate = row.lastSynchronizedAt

                    }
                    if (row.tableName == "bids") {
                        bidsDate = row.lastSynchronizedAt

                    }
                }
                if (ascentsDate.isNotEmpty()) {
                    updateAscents(syncResponse.pUT.ascents, ascentsDate, userId)
                }
                if (bidsDate.isNotEmpty()) {
                    updateBids(syncResponse.pUT.bids, bidsDate, userId)
                }
            }

        } else {
            Log.d("MKB4", "not inited")
        }
    }

    private suspend fun updateAscents(
        newAscents: List<SyncResponse.PUT.Ascent>, date: String, userId: Int
    ) {
        Log.d("Mkb4", "in updateAscents")
        withContext(Dispatchers.IO) {
            newAscents.forEach { ascent ->
                val row = ContentValues(18)
                row.put("uuid", ascent.uuid)
                //row.put("wall_uuid", ascent.wallUuid)
                row.put("climb_uuid", ascent.climbUuid)
                row.put("angle", ascent.angle)
                row.put("is_mirror", ascent.isMirror)
                row.put("user_id", ascent.userId)
                row.put("attempt_id", ascent.attemptId)
                row.put("bid_count", ascent.bidCount)
                row.put("quality", ascent.quality)
                row.put("difficulty", ascent.difficulty)
                row.put("is_benchmark", ascent.isBenchmark)
                row.put("comment", ascent.comment)
                row.put("climbed_at", ascent.climbedAt)
                row.put("created_at", ascent.createdAt)

                if (ascent.isListed) {
                    db.insertWithOnConflict("ascents", null, row, CONFLICT_REPLACE)
                } else {
                    db.delete("ascents", "uuid = ?", arrayOf(ascent.uuid))
                }

            }

            val syncDateRow = ContentValues(3)
            syncDateRow.put("user_id", userId)
            syncDateRow.put("table_name", "ascents")
            syncDateRow.put("last_synchronized_at", date)

            val numOfRows =
                db.insertWithOnConflict("user_syncs", null, syncDateRow, CONFLICT_REPLACE)
            Log.d("MKB4", numOfRows.toString())
        }
    }

    private suspend fun updateBids(newBids: List<SyncResponse.PUT.Bid>, date: String, userId: Int) {
        Log.d("Mkb4", "in updateBids")
        withContext(Dispatchers.IO) {
            newBids.forEach { bid ->
                val row = ContentValues(9)
                row.put("uuid", bid.uuid)
                row.put("user_id", bid.userId)
                row.put("climb_uuid", bid.climbUuid)
                row.put("angle", bid.angle)
                row.put("is_mirror", bid.isMirror)
                row.put("bid_count", bid.bidCount)
                row.put("comment", bid.comment)
                row.put("climbed_at", bid.climbedAt)
                row.put("created_at", bid.createdAt)

                if (bid.isListed) {
                    db.insertWithOnConflict("bids", null, row, CONFLICT_REPLACE)
                } else {
                    db.delete("bids", "uuid = ?", arrayOf(bid.uuid))
                }
            }

            val syncDateRow = ContentValues(3)
            syncDateRow.put("user_id", userId)
            syncDateRow.put("table_name", "bids")
            syncDateRow.put("last_synchronized_at", date)

            val numOfRows =
                db.insertWithOnConflict("user_syncs", null, syncDateRow, CONFLICT_REPLACE)
            Log.d("MKB4", numOfRows.toString())
        }
    }


    private suspend fun createUserSyncRequestContent(id: Int): String =
        withContext(Dispatchers.IO) {

            val syncsCursor = db.query(
                "user_syncs", null, "user_id = ?", arrayOf(id.toString()), null, null, null
            )

            val userSyncs = ArrayList<UserSyncRequest.GET.Query.Syncs.UserSync>(8)
            while (syncsCursor.moveToNext()) {
                val entry = UserSyncRequest.GET.Query.Syncs.UserSync(
                    userId = id,
                    lastSynchronizedAt = syncsCursor.getString(2),
                    tableName = syncsCursor.getString(1)
                )
                userSyncs.add(entry)
            }
            syncsCursor.close()

            val syncs = UserSyncRequest.GET.Query.Syncs(userSyncs)
            val query = UserSyncRequest.GET.Query(syncs = syncs)
            val get = UserSyncRequest.GET(query = query)
            val userSyncRequest = UserSyncRequest(gET = get)
            val json = Json { encodeDefaults = true }
            return@withContext json.encodeToString<UserSyncRequest>(userSyncRequest)
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
        val user = ConfigRepository.getCurrentUsername()
        val token: String = ConfigRepository.getSessionTokenForUser(user ?: "") ?: ""

        withContext(Dispatchers.Default) {
            client = HttpClient(CIO) {
                expectSuccess = true
                install(Auth) {
                    bearer {
                        loadTokens {
                            BearerTokens(token, "")
                        }
                    }
                }
                install(ContentNegotiation) {
                    json()
                }
            }
        }
    }

    suspend fun login(username: String, password: String) {
        if (this::client.isInitialized) {
            withContext(Dispatchers.IO) {
                val json = Json { encodeDefaults = true }
                val loginBody = json.encodeToString<LoginRequest>(
                    LoginRequest(
                        password = password, username = username
                    )
                )
                val loginResponse: LoginResponse =
                    client.post("https://kilterboardapp.com/sessions") {
                        contentType(ContentType.Application.Json)
                        setBody(loginBody)
                    }.body()
                ConfigRepository.saveSession(
                    username, loginResponse.session.userId, loginResponse.session.token
                )
                close()
                setup(db)
            }
        } else {
            Log.d("MKB4", "not inited for login")
        }
        withContext(Dispatchers.IO) {

        }

    }
}