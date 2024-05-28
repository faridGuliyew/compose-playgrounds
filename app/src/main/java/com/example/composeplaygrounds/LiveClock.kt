package com.example.composeplaygrounds

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun LiveClock(modifier: Modifier = Modifier) {

    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 180f,
        targetValue = 540f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 60000)), label = ""
    )

    Column (
        modifier = Modifier.fillMaxSize()
    ) {

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)) {
            drawCircle(
                brush = Brush.linearGradient(listOf(Color.White, Color.Gray))
            )
            drawCircle(
                color = Color.White,
                radius = size.width / 3f
            )
            repeat(12) {
                rotate(it * 30f) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(center.x - 1.dp.toPx(), size.width * 1 / 16f),
                        size = Size(width = 2.dp.toPx(), height = 16.dp.toPx())
                    )
                }
            }
            repeat(60) {
                rotate(it * 6f) {
                    drawRect(
                        color = Color.Black,
                        topLeft = Offset(center.x, size.width * 1 / 16f),
                        size = Size(width = 1.dp.toPx(), height = 4.dp.toPx())
                    )
                }
            }
            rotate(rotation) {
                drawRect(color = Color.Black.copy(0.5f), topLeft = Offset(center.x - 1.dp.toPx(), center.y),
                    size = Size(width = 2.dp.toPx(), height = center.y - 48.dp.toPx()))
            }

            rotate(rotation + (rotation / 32f) ) {
                drawRect(color = Color.Black.copy(0.5f), topLeft = Offset(center.x - 1.dp.toPx(), center.y),
                    size = Size(width = 2.dp.toPx(), height = center.y - 72.dp.toPx()))
            }


            drawCircle(color = Color.Black, radius = 4.dp.toPx(), center = center.copy(x = center.x))
        }
    }
}