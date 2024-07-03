package com.example.composeplaygrounds.graphics

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import org.jetbrains.annotations.Range

@Composable
@Preview
fun BlurredImage() {
    val textMeasurer = rememberTextMeasurer()
    Canvas(modifier = Modifier.fillMaxSize().alpha(0.99f)) {

        with(drawContext.canvas.nativeCanvas) {
            //drawRect(Color.White, blendMode = BlendMode.Clear)
            // Destination
            drawText(
                textMeasurer = textMeasurer,
                text = "B",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 14.sp
                ),
                topLeft = Offset(
                    x = 100f,
                    y = 100f
                ))

            // Source
            drawCircle(
                color = Color.Magenta,
                radius = 50f,
                center = Offset(
                    x = 100f,
                    y = 100f
                ),
                blendMode = BlendMode.SrcOut
            )
        }
    }
}