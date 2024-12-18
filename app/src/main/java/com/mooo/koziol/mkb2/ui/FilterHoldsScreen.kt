package com.mooo.koziol.mkb2.ui

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mooo.koziol.mkb2.R

@Composable
fun FilterHoldsScreen(
    navController: NavHostController,
    filterViewModel: FilterViewModel = viewModel(LocalContext.current as ComponentActivity)
) {
    Scaffold(topBar = {
        FilterHoldsTopBar(
            navController,
            onApplyFilter = filterViewModel::updateHolds,
            onClearFilter = filterViewModel::unselectAllHolds
        )
    }, content = { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
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
                            filterViewModel.addOrUpdateHoldAt(
                                Offset(
                                    offset.x / size.width, offset.y / size.height
                                )
                            )
                        }, onLongPress = { offset ->
                            filterViewModel.removeHoldAt(
                                Offset(
                                    offset.x / size.width, offset.y / size.height
                                )
                            )
                        })
                    }
                    .drawWithContent {
                        drawContent()
                        for (hold in filterViewModel.selectedHoldsList) {
                            drawCircle(
                                color = Color(hold.role.screenColor),
                                size.width * 0.024f,
                                center = Offset(
                                    hold.xFraction * size.width, hold.yFraction * size.height
                                ),
                                1F,
                                style = Stroke(size.width * 0.012f)
                            )
                        }
                    })
        }
    })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterHoldsTopBar(
    navController: NavHostController,
    onApplyFilter: () -> Unit,
    onClearFilter: () -> Unit
) {
    TopAppBar(colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ), title = {
        Text("Griff-Filter")
    }, navigationIcon = {
        OutlinedIconButton(onClick = {
            onApplyFilter()
            navController.popBackStack()
        }, content = {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_alt_24),
                contentDescription = null,
            )
        })

    }, actions = {
        IconButton(onClick = { onClearFilter() }, content = {
            Icon(
                painter = painterResource(id = R.drawable.outline_filter_alt_off_24),
                contentDescription = null
            )
        })
    })
}
