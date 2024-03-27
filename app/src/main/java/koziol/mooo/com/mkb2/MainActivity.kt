package koziol.mooo.com.mkb2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import koziol.mooo.com.mkb2.data.ClimbRepository
import koziol.mooo.com.mkb2.data.HoldsRepository
import koziol.mooo.com.mkb2.data.OriginalDbOpenHelper
import koziol.mooo.com.mkb2.ui.MkbScaffold
import koziol.mooo.com.mkb2.ui.theme.MKB2Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = OriginalDbOpenHelper(applicationContext).readableDatabase
        HoldsRepository.setup(db)
        ClimbRepository.db = db

        setContent {
            MKB2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    //KilterBard()
                    MkbScaffold()
                }
            }
        }
    }
}
