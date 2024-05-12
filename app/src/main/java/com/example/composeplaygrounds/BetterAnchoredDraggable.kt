package com.example.composeplaygrounds

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun BetterAnchoredDraggable() {
    Column (modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {

        val config = LocalConfiguration.current
        val density = LocalDensity.current
        var offsetY by remember {
            mutableFloatStateOf(0f)
        }
        val end by remember {
            mutableFloatStateOf(with(density) {-config.screenHeightDp.dp.toPx() + 120.dp.toPx()})
        }
        val animatedOffsetY by animateFloatAsState(targetValue = offsetY, animationSpec = spring(),
            label = "offset y"
        )
        var positionalThreshold by remember {
            mutableFloatStateOf(end / 2)
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .offset {
                    IntOffset(
                        x = 0,
                        y = animatedOffsetY.roundToInt()
                    )
                }
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            Log.e("offsetY", offsetY.toString())
                            if (offsetY <= positionalThreshold) {
                                offsetY = end
                            } else {
                                offsetY = 0f
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetY += dragAmount
                    }
                }
        )
    }
}


@Preview (showBackground = true)
@Composable
fun BetterAnchoredDraggablePrev() {
    BetterAnchoredDraggable()
}