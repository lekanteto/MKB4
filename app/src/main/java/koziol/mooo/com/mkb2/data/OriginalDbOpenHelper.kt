package koziol.mooo.com.mkb2.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class OriginalDbOpenHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private suspend fun installDatabaseFromAssets() {
        withContext(Dispatchers.IO) {
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
    }

    override fun onCreate(db: SQLiteDatabase?) {
        CoroutineScope(Dispatchers.IO).launch {
            context.deleteDatabase(DATABASE_NAME)
            installDatabaseFromAssets()
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onCreate(db)
    }

    companion object {
        const val ASSETS_PATH = "databases"
        const val DATABASE_NAME = "db-286.sqlite3"
        const val DATABASE_VERSION = 7
    }
}