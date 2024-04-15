package koziol.mooo.com.mkb2

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import koziol.mooo.com.mkb2.data.ClimbsRepository
import koziol.mooo.com.mkb2.data.HoldsRepository
import koziol.mooo.com.mkb2.data.OriginalDbOpenHelper
import koziol.mooo.com.mkb2.data.RestClient
import koziol.mooo.com.mkb2.ui.MainSurface
import koziol.mooo.com.mkb2.ui.theme.MKB2Theme


class MainActivity : ComponentActivity() {

    private lateinit var db: SQLiteDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.IO).launch {
            db = OriginalDbOpenHelper(applicationContext).writableDatabase
            HoldsRepository.setup(db)
            ClimbsRepository.db = db
            RestClient.setup(db)
        }

        setContent {
            MKB2Theme {
                MainSurface()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
        RestClient.close()
    }

}
