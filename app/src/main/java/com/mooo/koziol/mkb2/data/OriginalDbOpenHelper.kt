package com.mooo.koziol.mkb2.data

import android.content.Context
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class OriginalDbOpenHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        "${context.packageName}.database_versions", Context.MODE_PRIVATE
    )

    private fun installedDatabaseIsOutdated(): Boolean {
        return preferences.getInt(DATABASE_NAME, 0) < DATABASE_VERSION
    }

    private fun writeDatabaseVersionInPreferences() {
        preferences.edit().apply {
            putInt(DATABASE_NAME, DATABASE_VERSION)
            apply()
        }
    }

    private fun installDatabaseFromAssets() {
        val inputStream = context.assets.open("$ASSETS_PATH/$DATABASE_NAME")

        try {
            val outputFile = File(context.getDatabasePath(DATABASE_NAME).path)
            val outputStream = FileOutputStream(outputFile)

            inputStream.copyTo(outputStream)
            inputStream.close()

            outputStream.flush()
            outputStream.close()
        } catch (exception: Throwable) {
            throw RuntimeException("The $DATABASE_NAME database couldn't be installed.", exception)
        }
    }

    @Synchronized
    private fun installOrUpdateIfNecessary() {
        if (installedDatabaseIsOutdated()) {
            Log.d("MKB DBO", "Copy DB start")
            context.deleteDatabase(DATABASE_NAME)
            installDatabaseFromAssets()
            writeDatabaseVersionInPreferences()
            Log.d("MKB DBO", "Copy DB finish")
            CoroutineScope(Dispatchers.IO).launch {
                ConfigRepository.climbCacheIsUpdated(false)
            }
        }
    }

    override fun getWritableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        val db = super.getWritableDatabase()
        Log.d("MKB4", "b4 indexing ${db.path}")
        createIndexes(db)
        Log.d("MKB4", "after indexing ${db.path}")
        return db
    }

    // Method to execute all CREATE INDEX statements
    private fun createIndexes(db: SQLiteDatabase) {
        // For JOINs
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climbs_uuid ON climbs (uuid);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climb_stats_climb_uuid ON climb_stats (climb_uuid);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_difficulty_grades_difficulty ON difficulty_grades (difficulty);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climb_cache_climb_uuid ON climb_cache_fields (climb_uuid);")

        // For WHERE clauses (equality filters)
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climbs_layout_id ON climbs (layout_id);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climbs_is_listed ON climbs (is_listed);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climbs_is_draft ON climbs (is_draft);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climbs_frames_count ON climbs (frames_count);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climb_stats_angle ON climb_stats (angle);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climbs_setter_username ON climbs (setter_username);")

        // For WHERE clauses (range conditions)
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climbs_edge ON climbs (edge_left, edge_bottom, edge_right, edge_top);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climb_stats_quality_avg ON climb_stats (quality_average);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climb_stats_display_difficulty ON climb_stats (display_difficulty);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climb_stats_difficulty_avg ON climb_stats (difficulty_average);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climb_cache_display_difficulty ON climb_cache_fields (display_difficulty);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climb_stats_ascensionist_count ON climb_stats (ascensionist_count);")

        // For LIKE clause
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_climbs_name ON climbs (name);")

        // For subqueries
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_ascents_climb_uuid ON ascents (climb_uuid);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_ascents_angle ON ascents (angle);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_bids_climb_uuid ON bids (climb_uuid);")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_bids_angle ON bids (angle);")
    }

    override fun getReadableDatabase(): SQLiteDatabase {
        installOrUpdateIfNecessary()
        return super.getReadableDatabase()
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("MKB4", "inOnCreateDB")
        // Nothing to do
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d("MKB4", "inOnUpdateDB")
        // Nothing to do
    }

    companion object {
        const val ASSETS_PATH = "databases"
        const val DATABASE_NAME = "db.sqlite3"
        const val DATABASE_VERSION = 16
    }

}