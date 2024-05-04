package com.example.composeplaygrounds

import android.graphics.Paint
import android.graphics.Shader
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeplaygrounds.ui.theme.RadarColor
import com.example.composeplaygrounds.ui.theme.RadarColorGreen

@Composable
fun FlexView() {

    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing))
    )

    Column(
        modifier = Modifier.fillMaxSize()
            .background(RadarColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Canvas(modifier = Modifier.height(240.dp).fillMaxWidth()) {
            repeat(6) {
                drawCircle(
                    color = RadarColorGreen,
                    radius = size.width / 10 + 32.dp.toPx() *it,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            drawLine(
                color = RadarColorGreen,
                start = Offset(size.center.x, 0f - 80.dp.toPx()),
                end = Offset(size.center.x, size.height + 80.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )

            drawLine(
                color = RadarColorGreen,
                start = Offset(0f, size.center.y),
                end = Offset(size.width, size.center.y),
                strokeWidth = 2.dp.toPx()
            )

            //rotation should appear on top of the lines
            rotate(rotation) {
                drawContext.canvas.nativeCanvas.apply {
                    drawRect(
                        size.center.x,
                        size.center.y + 18.dp.toPx(),
                        size.width,
                        size.center.y - 18.dp.toPx(),
                        Paint().apply {
                            setColor(Color.Transparent.toArgb())
                            setShadowLayer(
                                80.dp.toPx(),
                                0f,
                                -40.dp.toPx(),
                                Color.Green.toArgb()
                            )
                        }
                    )
                }
                drawLine(
                    color = Color.Green,
                    start = Offset(size.center.x, size.center.y),
                    end = Offset(size.width, size.center.y),
                    strokeWidth = 2.dp.toPx()
                )
            }

            //bullseye should be on top
            drawCircle(
                color = RadarColorGreen,
                radius = 10.dp.toPx()
            )
        }

    }
}

@Preview (showBackground = true)
@Composable
fun FlexViewPrev() {

    FlexView()

}