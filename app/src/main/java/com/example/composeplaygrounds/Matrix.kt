package com.example.composeplaygrounds

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun Matrix() {
        val textMeasurer = rememberTextMeasurer()
        Canvas(modifier = Modifier.size(120.dp).background(Color.Transparent)) {
            val fullWidth = size.width
            val charWidth = textMeasurer.measure("W").size.width
            val maxTextHorizontalLength  = fullWidth.toInt() / charWidth

            val fullHeight = size.height
            val charHeight = textMeasurer.measure("W").size.height
            val maxTextVerticalLength  = fullHeight.toInt() / charHeight

            repeat(maxTextHorizontalLength) {
                translate(left = it * charWidth.toFloat()) {
                    repeat(maxTextVerticalLength) {
                        val randomChar = (48..57).plus(65..90) .random().toChar()
                        translate(top = it * charHeight.toFloat()) {
                            drawText(
                                textMeasurer = textMeasurer,
                                text = "$randomChar",
                                style = TextStyle(color = Color.White)
                            )
                        }
                    }
                }
            }
        }
}

@Preview (showBackground = true)
@Composable
private fun MatrixPrev() {
    Matrix()
}