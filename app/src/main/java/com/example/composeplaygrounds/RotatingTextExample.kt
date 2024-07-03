package com.example.composeplaygrounds

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RotatingText() {
    val textMeasurer = rememberTextMeasurer()
    val text = "HELLO WORLD!"
    val translationXPercentage by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 10000)), label = ""
    )
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier
            .align(Alignment.Center)
            .size(300.dp)
            .border(1.dp, color = Color.Black)
        ) {
            val movement = size.width * translationXPercentage
            val left = (if (movement < size.width) movement else if (movement < size.width * 2) size.width else if (movement < size.width * 3) size.width * 3 - movement else 0f).coerceIn(0f, size.width - 140.sp.toPx())
            val top = if (movement <= size.width) 0f else if (movement < size.width * 2) movement- size.width else if (movement < size.width * 3) size.width else size.width * 4 - movement
            translate(left = left, top = top) {
                text.forEachIndexed { index, char->
                    drawText(
                        textMeasurer = textMeasurer,
                        text = char.toString(),
                        topLeft = Offset(y = 0f, x = index * 12.sp.toPx())
                    )
                }
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
private fun RotatingTextPrev() {
    RotatingText()
}