package koziol.mooo.com.mkb2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import koziol.mooo.com.mkb2.R
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@Composable
fun BoardScreen(
    destinations: Map<String, () -> Unit>, boardViewModel: BoardViewModel = viewModel()
) {

    val climb by boardViewModel.currentClimb.collectAsState()

    Scaffold(bottomBar = {
        BoardBottomBar(destinations, climb.uuid)
    }) { paddingValues ->
        var dragOffset by remember { mutableFloatStateOf(0f) }
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Text(
                text = climb.name + " " + climb.grade + " " + (climb.rating * 100).roundToInt() / 100f
            )
            // set up all transformation states
            var zoomFactor by remember { mutableFloatStateOf(1f) }
            var panOffset by remember { mutableStateOf(Offset.Zero) }

            var imageSize by remember { mutableStateOf(IntSize.Zero) }
            val panAndZoomState = rememberTransformableState { zoomChange, offsetChange, _ ->
                zoomFactor = (zoomFactor * zoomChange).coerceIn(1F, 5F)

                val newOffset = panOffset + offsetChange * zoomFactor

                val maxX = (imageSize.width * (zoomFactor - 1) / 2f)
                val maxY = (imageSize.height * (zoomFactor - 1) / 2f)

                panOffset = Offset(
                    newOffset.x.coerceIn(-maxX, maxX), newOffset.y.coerceIn(-maxY, maxY)
                )
            }

            Image(painter = painterResource(id = R.drawable._546),
                "KB image",
                modifier = Modifier
                    .clipToBounds()
                    .onSizeChanged { size -> imageSize = size }
                    .graphicsLayer(
                        scaleX = zoomFactor,
                        scaleY = zoomFactor,
                        translationX = panOffset.x,
                        translationY = panOffset.y,
                    )
                    .transformable(state = panAndZoomState)
                    .drawWithContent {
                        drawContent()
                        for (hold in climb.getHoldsList()) {
                            drawCircle(
                                color = Color(hold.role.screenColor), 28F, center = Offset(
                                    hold.xFraction * size.width, hold.yFraction * size.height
                                ), 1F, style = Stroke(7F)
                            )
                        }
                    }
                    .draggable(orientation = Orientation.Horizontal,
                        enabled = zoomFactor == 1f,
                        onDragStarted = { dragOffset = 0f },
                        state = rememberDraggableState { delta ->
                            dragOffset += delta
                            if (dragOffset.absoluteValue / imageSize.width > 0.15) {
                                boardViewModel.moveToNextClimb(dragOffset < 0)
                                dragOffset = 0f
                            }
                        }

                    ))
        }
    }
}

@Composable
fun BoardBottomBar(
    destinations: Map<String, () -> Unit>, climbUuid: String
) {
    val uriHandler = LocalUriHandler.current

    BottomAppBar(actions = {
        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_bookmark_add_24),
                contentDescription = "Set Boulder"
            )
        }, selected = false, onClick = { })

        NavigationBarItem(icon = {
            Icon(
                imageVector = Icons.Outlined.Search, contentDescription = "Search Boulder"
            )
        }, selected = false, onClick = { })

        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.mountain_flag_fill0_wght400_grad0_opsz24),
                contentDescription = "Set Boulder"
            )
        }, selected = false, onClick = { })

        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.mountain_flag_24px),
                contentDescription = "Log ascent"
            )
        }, selected = false, onClick = { })

        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_open_in_new_24),
                contentDescription = "Open in Kilter"
            )
        },
            selected = false,
            onClick = { uriHandler.openUri("https://kilterboardapp.com/climbs/${climbUuid}") })

    }, floatingActionButton = {
        FloatingActionButton(
            onClick = destinations["climbsFilter"] ?: {},
            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_wb_twilight_24),
                contentDescription = "LED connect"
            )
        }
    })
}