package koziol.mooo.com.mkb2

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import koziol.mooo.com.mkb2.data.ClimbsRepository
import koziol.mooo.com.mkb2.data.ConfigRepository
import koziol.mooo.com.mkb2.data.HoldsRepository
import koziol.mooo.com.mkb2.data.OriginalDbOpenHelper
import koziol.mooo.com.mkb2.data.RestClient
import koziol.mooo.com.mkb2.data.SetterRepository
import koziol.mooo.com.mkb2.ui.MainSurface
import koziol.mooo.com.mkb2.ui.theme.MKB2Theme


class MainActivity : ComponentActivity() {

    private lateinit var db: SQLiteDatabase
    private val _isInitializing = MutableStateFlow(false)
    val isInitializing = _isInitializing.asStateFlow()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        CoroutineScope(Dispatchers.IO).launch {
            _isInitializing.update { true }
            Log.d("MKB", "start init")
            ConfigRepository.setup(applicationContext)
            db = async { openDb() }.await()
            HoldsRepository.setup(db)
            ClimbsRepository.setup(db)
            SetterRepository.setup(db)
            async { RestClient.setup(db) }.await()
            //RestClient.downloadSharedData()
            //RestClient.downloadUserData()

            //val mkbDb = Room.databaseBuilder(applicationContext, MkbDatabase::class.java, "mkb.db").build()
            //val bookmarkDao = mkbDb.filterDao()
            _isInitializing.update { false }
            Log.d("MKB", "end init")
            ClimbsRepository.triggerListUpdate()
        }


        setContent {
            MKB2Theme {
                MainSurface()
            }
        }
    }

    private fun openDb(): SQLiteDatabase {
        return OriginalDbOpenHelper(this).writableDatabase
    }

    override fun onDestroy() {
        RestClient.close()
        db.close()
        super.onDestroy()
    }

}
