package com.mooo.koziol.mkb2

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.mooo.koziol.mkb2.data.BookmarkedFilter
import com.mooo.koziol.mkb2.data.ClimbFilter
import com.mooo.koziol.mkb2.data.ClimbsRepository
import com.mooo.koziol.mkb2.data.ConfigRepository
import com.mooo.koziol.mkb2.data.FilterDao
import com.mooo.koziol.mkb2.data.HoldsRepository
import com.mooo.koziol.mkb2.data.MkbDatabase
import com.mooo.koziol.mkb2.data.OriginalDbOpenHelper
import com.mooo.koziol.mkb2.data.RestClient
import com.mooo.koziol.mkb2.data.SetterRepository
import com.mooo.koziol.mkb2.ui.MainSurface
import com.mooo.koziol.mkb2.ui.theme.MKB2Theme


class MainActivity : ComponentActivity() {

    private lateinit var db: SQLiteDatabase
    private lateinit var mkbDb: MkbDatabase
    private lateinit var bookmarkDao: FilterDao
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
            RestClient.setup(db)

            mkbDb = Room.databaseBuilder(applicationContext, MkbDatabase::class.java, "mkb.db").build()
            bookmarkDao = mkbDb.filterDao()
            val bookmark = bookmarkDao.getLastActive()
            ClimbsRepository.activeFilter = bookmark?.climbFilter ?: ClimbFilter()
            _isInitializing.update { false }
            Log.d("MKB", "end init")
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

    override fun onStop() {
        CoroutineScope(Dispatchers.IO).launch {
            RestClient.close()
            val bookmark = BookmarkedFilter(1, "active", ClimbsRepository.activeFilter)
            bookmarkDao.insert(bookmark)
            //db.close()
            //mkbDb.close()
        }
        super.onStop()
    }

}
