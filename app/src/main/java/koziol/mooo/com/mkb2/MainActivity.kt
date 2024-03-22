package koziol.mooo.com.mkb2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import koziol.mooo.com.mkb2.data.HoldsRepository
import koziol.mooo.com.mkb2.ui.theme.MKB2Theme
import kotlin.math.max


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var holdsList: List<KBHold> = mutableListOf<KBHold>()

        CoroutineScope(Dispatchers.IO).launch {
            holdsList = HoldsRepository(applicationContext).getAllHolds()
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
        val textMeasurer = rememberTextMeasurer()

        Image(
            painter = painterResource(id = R.drawable._546), "KB image",
            modifier = Modifier
                .clipToBounds()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                )
                .transformable(state = state)
//                .drawWithContent {
//                    drawContent()
//                    for (hold in holds) {
//                        drawCircle(
//                            color = Color.Magenta, 20F, center = Offset(
//                                hold.xFraction * size.width, hold.yFraction * size.height
//                            ), 1F, style = Stroke(10F)
//                        )
//                    }
//                }
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        Log.d("MKB2","${offset.x/size.width} ${offset.y/size.height}")
                    }
                }
        )
        Canvas(modifier = Modifier) {
            val canvasQuadrantSize = Size(40F, 40F)
            drawRect(
                color = Color.Magenta,
                size = canvasQuadrantSize
            )
        }
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