package koziol.mooo.com.mkb2.data

import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SetterRepository {

    private lateinit var db: SQLiteDatabase


    suspend fun getSetters(filter: String): List<String> {
        return withContext(Dispatchers.IO) {
            val likeFilter = "%$filter%"
            val setters = mutableListOf<String>()

            if (SetterRepository::db.isInitialized) {
                val setterCursor = db.rawQuery(
                    """
                    SELECT DISTINCT setter_username FROM climbs
                    WHERE setter_username like ?
                    ORDER  BY setter_username
                    LIMIT  100 
                """.trimIndent(), arrayOf(likeFilter)
                )


                while (setterCursor.moveToNext()) {
                    setters.add(setterCursor.getString(0))
                }

                setterCursor.close()
            }
            return@withContext setters
        }
    }

    fun setup(db: SQLiteDatabase) {
        this.db = db
    }
}

