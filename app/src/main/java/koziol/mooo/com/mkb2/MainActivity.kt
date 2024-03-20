package koziol.mooo.com.mkb2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import koziol.mooo.com.mkb2.ui.theme.MKB2Theme
import kotlin.math.max


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val holdsList = mutableListOf<KBHold>()
        CoroutineScope(Dispatchers.IO).launch {
            val myDb = OriginalDbOpenHelper(applicationContext).readableDatabase
            val holdsCursor = myDb.rawQuery(
                """
                SELECT placements.id, holes.x, holes.y
                FROM placements JOIN holes ON placements.hole_id=holes.id
                WHERE 
                	placements.layout_id=1 AND -- Kilterboard Original layout
                	holes.x BETWEEN 1 AND 143 AND holes.y BETWEEN 0 AND 156 -- dimensions of 12x12 w/ kickboard
                ORDER BY holes.x, holes.y

            """.trimIndent(), null
            )

            var id: Int
            var x: Int
            var y: Int
            holdsCursor.moveToFirst()
            do {
                id = holdsCursor.getInt(0)
                x = holdsCursor.getInt(1)
                y = holdsCursor.getInt(2)
                holdsList.add(KBHold(id, x, y))
                println("$id, $x, $y")
                holdsCursor.moveToNext()
            } while (!holdsCursor.isLast)
            holdsCursor.close()
        }
        setContent {
            MKB2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    KilterBard(holdsList)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Composable
fun KilterBard(holds: List<KBHold>) {
    Column {
        // set up all transformation states
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
            scale = max(scale * zoomChange, 1.0F)
            offset += offsetChange * scale
        }
        Image(
            painter = painterResource(id = R.drawable._546), "KB image",
            Modifier
                .clipToBounds()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                )
                .transformable(state = state)
                .drawWithContent {
                    drawContent()
                    for (hold in holds) {
                        drawCircle(
                            color = Color.Magenta, 20F, center = Offset(
                                hold.xFraction * size.width, hold.yFraction * size.height
                            ), 1F, style = Stroke(10F)
                        )
                    }
                },
        )
        Text("Foo", Modifier.fillMaxHeight())
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MKB2Theme {
        Greeting("Foo")
    }
}