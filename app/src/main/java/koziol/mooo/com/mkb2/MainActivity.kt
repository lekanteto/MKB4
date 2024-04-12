package koziol.mooo.com.mkb2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import koziol.mooo.com.mkb2.data.ClimbsRepository
import koziol.mooo.com.mkb2.data.HoldsRepository
import koziol.mooo.com.mkb2.data.OriginalDbOpenHelper
import koziol.mooo.com.mkb2.ui.MainSurface
import koziol.mooo.com.mkb2.ui.theme.MKB2Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = OriginalDbOpenHelper(applicationContext).readableDatabase
        HoldsRepository.setup(db)
        ClimbsRepository.db = db

        setContent {
            MKB2Theme {
                MainSurface()
            }
        }
    }
}
