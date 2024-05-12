package com.example.composeplaygrounds

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt


enum class DragAnchors {
    Start, End
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnchoredDraggableDemo() {

    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val state = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Start,
            positionalThreshold = { distance: Float -> distance * 0.1f },
            velocityThreshold = { 50f },
            animationSpec = spring(),
        ).apply {
            updateAnchors(
                DraggableAnchors {
                    DragAnchors.Start at 0f
                    DragAnchors.End at  with(density) { -200.dp.toPx() }
                }
            )
        }
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(
                        y = state
                            .requireOffset()
                            .roundToInt(),
                        x = 0,
                    )
                }
                .anchoredDraggable(state, Orientation.Vertical)
                .height(120.dp)
                .background(Color.Black)
        )
    }

}




@Preview (showBackground = true)
@Composable
fun AnchoredDraggableDemoPrev() {
    AnchoredDraggableDemo()
}