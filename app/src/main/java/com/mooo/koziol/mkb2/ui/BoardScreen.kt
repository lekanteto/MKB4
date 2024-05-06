package com.mooo.koziol.mkb2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mooo.koziol.mkb2.R
import com.mooo.koziol.mkb2.data.ClimbsRepository
import com.mooo.koziol.mkb2.data.HoldRole
import com.mooo.koziol.mkb2.data.HoldsRepository
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


@Composable
fun BoardScreen(
    navController: NavHostController,
    destinations: Map<String, () -> Unit>,
    boardViewModel: BoardViewModel = viewModel()
) {

    val climb by boardViewModel.currentClimb.collectAsState()

    Scaffold(bottomBar = {
        BoardBottomBar(navController, destinations, climb.uuid)
    }) { paddingValues ->
        var dragOffset by remember { mutableFloatStateOf(0f) }
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Text(
                text = climb.name + " " + climb.grade + " " + (climb.rating * 100).roundToInt() / 100f,
                modifier = Modifier.padding(10.dp)
            )
            Row(modifier = Modifier.padding(10.dp)) {
                val ascents = ClimbsRepository.getAscentsFor(climb)
                if (ascents.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.mountain_flag_24px),
                        contentDescription = null
                    )
                    Text(text = ascents.last().climbedAt)
                }
                val bids = ClimbsRepository.getBidsFor(climb)
                if (bids.isNotEmpty()) {
                    Icon(
                        painter = painterResource(id = R.drawable.mountain_flag_half),
                        contentDescription = null
                    )
                    Text(text = bids.last().climbedAt)
                }

            }
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

            val textMeasurer = rememberTextMeasurer()
            val color  = MaterialTheme.colorScheme.onSurface
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
                                color = Color(hold.role.screenColor),
                                size.width * 0.026f,
                                center = Offset(
                                    hold.xFraction * size.width, hold.yFraction * size.height
                                ),
                                1F,
                                style = Stroke(size.width * 0.006f)
                            )

                            var text = hold.id.toString()
                            if (hold.role != HoldRole.FootHold) {
                                text = text + "\n" + HoldsRepository.getClosestDistance(hold, climb.getHoldsList())
                            }
                            drawText(
                                textMeasurer, text , topLeft = Offset(
                                    hold.xFraction * size.width, hold.yFraction * size.height
                                ), style = TextStyle(color = color)
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
    navController: NavHostController, destinations: Map<String, () -> Unit>, climbUuid: String
) {
    val uriHandler = LocalUriHandler.current

    BottomAppBar(actions = {
        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_bookmark_add_24),
                contentDescription = null
            )

        }, selected = false, onClick = { }, enabled = false)

        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.outline_view_list_24),
                contentDescription = null
            )
        }, selected = false, onClick = { navController.popBackStack() })

        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.mountain_flag_half),
                contentDescription = null
            )
        }, selected = false, onClick = { }, enabled = false)

        NavigationBarItem(icon = {
            Icon(
                painter = painterResource(id = R.drawable.mountain_flag_24px),
                contentDescription = null
            )
        }, selected = false, onClick = { }, enabled = false)

        /*        NavigationBarItem(icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_open_in_new_24),
                        contentDescription = null
                    )
                },
                    selected = false,
                    onClick = { uriHandler.openUri("https://kilterboardapp.com/climbs/${climbUuid}") })*/

    }, floatingActionButton = {
        FloatingActionButton(
            onClick = { uriHandler.openUri("https://kilterboardapp.com/climbs/${climbUuid}") },
            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_open_in_new_24),
                contentDescription = null
            )
        }
    }/*floatingActionButton = {
            FloatingActionButton(
                //onClick = destinations["climbsFilter"] ?: {},
                onClick = {},
                //containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_wb_twilight_24),
                    contentDescription = null
                )
            }
        }*/
    )
}