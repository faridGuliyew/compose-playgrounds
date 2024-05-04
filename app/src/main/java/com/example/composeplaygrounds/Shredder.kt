package com.example.composeplaygrounds

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.random.Random

@Composable
fun Shredder() {

    val config = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) {config.screenWidthDp.dp.toPx()}

    val animatedTranslation by rememberInfiniteTransition(label = "").animateFloat(
        initialValue = 0f,
        targetValue = 2 * screenWidthPx,
        animationSpec = infiniteRepeatable(tween(durationMillis = 5000, easing = LinearEasing)),
        label = ""
    )

    val startShredding by remember {
        derivedStateOf {
            animatedTranslation >= screenWidthPx / 1.33f
        }
    }

    val shredAnimation by animateFloatAsState(
        targetValue = if (startShredding) with(density) { 200.dp.toPx() } else 0f,
        animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .drawWithCache {
                val firstSchema = (1..150).map {
                    Offset(
                        Random.nextFloat() * size.width,
                        Random.nextFloat() * size.height
                    )
                }

                val secondSchema = (1..150).map {
                    Offset(
                        Random.nextFloat() * size.width,
                        Random.nextFloat() * size.height
                    )
                }

                val secondSchemaMid = (1..50).map {
                    Offset(
                        Random.nextFloat() * size.width,
                        Random.nextFloat() * size.height
                    )
                }

                val secondSchemaBig = (1..20).map {
                    Offset(
                        Random.nextFloat() * size.width,
                        Random.nextFloat() * size.height
                    )
                }

                onDrawWithContent {
                    drawContent()
                    //layer one
                    translate(left = if (animatedTranslation <= screenWidthPx) animatedTranslation else -screenWidthPx * 2 + animatedTranslation) {
                        drawPoints(
                            points = firstSchema,
                            pointMode = PointMode.Points,
                            color = Color.White.copy(0.4f),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    translate(left = if (animatedTranslation <= screenWidthPx) animatedTranslation * 1.2f else -screenWidthPx * 2 + animatedTranslation) {
                        drawPoints(
                            points = secondSchemaMid,
                            pointMode = PointMode.Points,
                            color = Color.White.copy(0.6f),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    translate(left = if (animatedTranslation <= screenWidthPx) animatedTranslation * 1.3f else -screenWidthPx * 2 + animatedTranslation) {
                        drawPoints(
                            points = secondSchemaBig,
                            pointMode = PointMode.Points,
                            color = Color.White.copy(0.4f),
                            strokeWidth = 8.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    //layer two
                    translate(left = -screenWidthPx + animatedTranslation) {
                        drawPoints(
                            points = secondSchema,
                            pointMode = PointMode.Points,
                            color = Color.White,
                            strokeWidth = 1.5.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    translate(left = -screenWidthPx + animatedTranslation * 1.2f) {
                        drawPoints(
                            points = secondSchemaMid,
                            pointMode = PointMode.Points,
                            color = Color.White.copy(0.6f),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    translate(left = -screenWidthPx + animatedTranslation * 1.3f) {
                        drawPoints(
                            points = secondSchemaBig,
                            pointMode = PointMode.Points,
                            color = Color.White.copy(0.4f),
                            strokeWidth = 8.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box (
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val textMeasurer = rememberTextMeasurer()
            Box(modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    translationX = -screenWidthPx + animatedTranslation
                }
                .drawWithCache {

                    val textStyle = TextStyle(
                        color = Color.White.copy(0.5f),
                        fontSize = 22.sp
                    )
                    val finalTextStyle = TextStyle(
                        color = Color.White.copy(0.2f),
                        fontSize = 22.sp
                    )
                    val fullWidth = size.width
                    val measuredSize = textMeasurer.measure("W", textStyle).size
                    val charWidth = measuredSize.width
                    val maxTextHorizontalLength = fullWidth.toInt() / charWidth

                    val fullHeight = size.height
                    val charHeight = measuredSize.height
                    val maxTextVerticalLength = fullHeight.toInt() / charHeight

                    onDrawBehind {
                        repeat(maxTextHorizontalLength) {
                            translate(left = it * charWidth.toFloat()) {
                                repeat(maxTextVerticalLength) {
                                    val randomChar = (48..57)
                                        .plus(65..90)
                                        .random()
                                        .toChar()
                                    translate(top = it * charHeight.toFloat()) {
                                        drawText(
                                            textMeasurer = textMeasurer,
                                            text = "$randomChar",
                                            style = if (shredAnimation == 200.dp.toPx()) finalTextStyle else textStyle
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                .drawBehind {

                    drawRoundRect(
                        color = Color.Red,
                        cornerRadius = CornerRadius(18f, 18f),
                        size = Size(
                            height = size.height,
                            width = if (startShredding) size.width - shredAnimation else size.width
                        )
                    )
                }
            )
            Canvas(modifier = Modifier
                .width(4.dp)
                .height(200.dp)) {
                drawContext.canvas.nativeCanvas.apply {
                    this.drawRoundRect(
                        0f,
                        0f,
                         size.width,
                        size.height,
                        10f,
                        300f,
                        Paint().apply {
                            setColor(
                                Color.White.toArgb()
                            )

                            setShadowLayer(
                                32f,
                                0f,
                                0f,
                                Color.White.toArgb()
                            )
                        }
                    )
                }
            }
        }
    }


}


@Preview  (showBackground = true)
@Composable
private fun ShredderPrev() {
    Shredder()
}