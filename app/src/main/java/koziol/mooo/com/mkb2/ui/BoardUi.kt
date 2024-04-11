package koziol.mooo.com.mkb2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import koziol.mooo.com.mkb2.R

@Composable
fun KilterBard(boardViewModel: BoardViewModel = viewModel()) {

    Column {
        // set up all transformation states
        var zoomFactor by remember { mutableFloatStateOf(1f) }
        var panOffset by remember { mutableStateOf(Offset.Zero) }
        var imageSize by remember { mutableStateOf(IntSize.Zero) }
        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
            zoomFactor = (zoomFactor * zoomChange).coerceIn(1F, 5F)

            val newOffset = panOffset + offsetChange * zoomFactor

            val maxX = (imageSize.width * (zoomFactor - 1) / 2f)
            val maxY = (imageSize.height * (zoomFactor - 1) / 2f)

            panOffset = Offset(
                newOffset.x.coerceIn(-maxX, maxX), newOffset.y.coerceIn(-maxY, maxY)
            )
            //offset += offsetChange * scale
        }

        Image(painter = painterResource(id = R.drawable._546),
            "KB image",
            modifier = Modifier
                .clipToBounds()
                .onSizeChanged { imageSize = it }
                .graphicsLayer(
                    scaleX = zoomFactor,
                    scaleY = zoomFactor,
                    translationX = panOffset.x,
                    translationY = panOffset.y,
                )
                .transformable(state = state)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { offset ->
                        boardViewModel.updateHoldsSelection(
                            Offset(
                                offset.x / size.width, offset.y / size.height
                            )
                        )
                    }, onLongPress = { offset ->
                        boardViewModel.removeHold(
                            Offset(
                                offset.x / size.width, offset.y / size.height
                            )
                        )
                    })
                }
                .drawWithContent {
                    drawContent()
                    for (hold in boardViewModel.selectedHoldsList) {
                        drawCircle(
                            color = Color(hold.role.screenColor), 25F, center = Offset(
                                hold.xFraction * size.width, hold.yFraction * size.height
                            ), 1F, style = Stroke(10F)
                        )
                    }
                })
    }
}