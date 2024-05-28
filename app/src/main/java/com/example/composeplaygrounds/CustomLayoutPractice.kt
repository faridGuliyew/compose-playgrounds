package com.example.composeplaygrounds

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun CustomLayoutPractice(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.fillMaxSize(0.8f)
    ) {

        var shouldAnimate by remember {
            mutableStateOf(false)
        }
        val tileCount by animateIntAsState(
            targetValue = if (shouldAnimate) 100 else 0,
            tween(5000),
            finishedListener = {
                shouldAnimate = false
            }
        )

        Layout(
            modifier = Modifier.clickable {
                shouldAnimate = true
            },
            content = {
            repeat(tileCount) {
                Spacer(
                    modifier = Modifier
                        .width(30.dp)
                        .height(10.dp)
                        .background(Color.Black)
                )
            }
        }, measurePolicy = { measurables, constraints ->
            val placeables = measurables.map { measurable ->
                measurable.measure(constraints)
            }
            layout(constraints.maxWidth, constraints.maxHeight) {
                var isGoingRight = true
                var startFromLeft = true
                var y = 0
                var x = 0
                placeables.forEach { placeable ->
                    placeable.placeRelative(if (startFromLeft) x else constraints.maxWidth - x, y)
                    y += placeable.height
                    if (isGoingRight) {
                        if (x + placeable.width <= constraints.maxWidth) {
                            x += placeable.width
                        } else {
                            isGoingRight = false
                            x -= placeable.width
                            y -= 2 * placeable.height
                        }
                    } else {
                        if (x - placeable.width > constraints.minWidth) {
                            x -= placeable.width
                        } else {
                            isGoingRight = true
                            x -= placeable.width
                        }
                    }
                    startFromLeft = startFromLeft.not()
                }
            }
        }
        )
    }
}