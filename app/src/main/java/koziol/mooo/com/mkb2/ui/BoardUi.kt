package koziol.mooo.com.mkb2.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import koziol.mooo.com.mkb2.R
import kotlin.math.max

@Composable
fun KilterBard(boardViewModel: BoardViewModel = viewModel()) {
    val boardUiState by boardViewModel.uiState.collectAsState()

    Column {
        // set up all transformation states
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
            scale = (scale * zoomChange).coerceIn(1F, 5F)
            offset += offsetChange * scale
        }

        Box(
            modifier = Modifier
                .clipToBounds()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                )
                .transformable(state = state)
        ) {
            Image(painter = painterResource(id = R.drawable._546),
                "KB image",
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { offset ->
                            boardViewModel.updateHoldsSelection(
                                Pair(
                                    offset.x / size.width, offset.y / size.height
                                )
                            )
                        }, onLongPress = { offset ->
                            boardViewModel.removeHold(
                                Pair(
                                    offset.x / size.width, offset.y / size.height
                                )
                            )
                        })
                    }
                    .drawWithContent {
                        drawContent()
                        for (hold in boardViewModel.selectedHoldsList) {
                            drawCircle(
                                color = Color(hold.role.screenColor), 20F, center = Offset(
                                    hold.xFraction * size.width, hold.yFraction * size.height
                                ), 1F, style = Stroke(10F)
                            )
                        }
                    })
        }
        Text(boardViewModel.info, Modifier.fillMaxHeight())
    }
}