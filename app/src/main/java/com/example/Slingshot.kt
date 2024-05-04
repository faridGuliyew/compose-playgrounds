package com.example

import android.graphics.Paint
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeplaygrounds.R

@Composable
fun Slingshot() {

    val buttonInteractionSource = remember {
         MutableInteractionSource()
    }
    val isButtonPressed by buttonInteractionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isButtonPressed) 0.5f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium)
    )

    var isStonePlaced by remember {
        mutableStateOf(false)
    }

    Image(
        painter = painterResource(id = R.drawable.img_grass),
        contentDescription = "field",
        contentScale = ContentScale.FillBounds
    )
    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val density = LocalDensity.current
            val config = LocalConfiguration.current

            val initialControlX by remember {
                mutableFloatStateOf(with(density) { 100.dp.toPx() })
            }

            var controlX by remember {
                mutableFloatStateOf(initialControlX)
            }
            var controlY by remember {
                mutableFloatStateOf(150f)
            }
            var isReleased by remember {
                mutableStateOf(true)
            }




            val animatedControlX by animateFloatAsState(
                targetValue = if (isReleased) initialControlX else controlX,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            val animatedControlY by animateFloatAsState(
                targetValue = if (isReleased) 150f else controlY,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            val stoneTranslation by animateFloatAsState(
                targetValue = if (isStonePlaced) 0f else -1000f,
                animationSpec = tween(1000)
            )

            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        rotationX = -15f - animatedControlY / size.height
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = { isReleased = true; isStonePlaced = false },
                            onDragStart = { isReleased = false }
                        ) { change, dragAmount ->
                            val position = change.position
                            controlX = position.x
                            controlY = position.y
                        }
                    }
            ) {

                val slingshot = Path().apply {
                    lineTo(size.center.x, size.center.y)
                    lineTo(size.width, 0f)
                    moveTo(size.center.x, size.center.y)
                    lineTo(size.center.x, size.height * 2)
                }

                //draw shadow
                drawContext.canvas.nativeCanvas.apply {
                    this.drawRect(
                        size.center.x - 12.dp.toPx(),
                        size.height * 2,
                        size.center.x + 12.dp.toPx(),
                        size.height * 3f,
                        Paint().apply {
                            this.setColor(Color.Transparent.toArgb())
                            this.setShadowLayer(
                                18.dp.toPx(),
                                0f,
                                0f,
                                Color.Black.toArgb()
                            )
                        }
                    )
                }

                translate(
                    left = 210.dp.toPx(),
                    top = 250.dp.toPx()
                ) {
                    rotate(45f) {
                        drawContext.canvas.nativeCanvas.apply {
                            this.drawRect(
                                size.center.x - 12.dp.toPx(),
                                size.height * 2,
                                size.center.x + 12.dp.toPx(),
                                size.height * 4.5f,
                                Paint().apply {
                                    this.setColor(Color.Transparent.toArgb())
                                    this.setShadowLayer(
                                        18.dp.toPx(),
                                        0f,
                                        0f,
                                        Color.Black.toArgb()
                                    )
                                }
                            )
                        }
                    }
                }

                translate(
                    left = -210.dp.toPx(),
                    top = 250.dp.toPx()
                ) {
                    rotate(-45f) {
                        drawContext.canvas.nativeCanvas.apply {
                            this.drawRect(
                                size.center.x - 12.dp.toPx(),
                                size.height * 2,
                                size.center.x + 12.dp.toPx(),
                                size.height * 4.5f,
                                Paint().apply {
                                    this.setColor(Color.Transparent.toArgb())
                                    this.setShadowLayer(
                                        24.dp.toPx(),
                                        0f,
                                        0f,
                                        Color.Black.toArgb()
                                    )
                                }
                            )
                        }
                    }
                }

                // Calculate the parameter 't' at the steepest part
                val t = 0.5f // For quadratic Bézier curve, steepest part is always at t = 0.5

                // Calculate the position of the steepest part (vertex) using the Bézier curve equation
                val vertexX = t * animatedControlX + t * t * size.width
                val vertexY = animatedControlY

                val slingshotRope = Path().apply {
                    quadraticBezierTo(
                        x1 = animatedControlX, // X-coordinate of the control point (middle of the canvas)
                        y1 = animatedControlY, // Y-coordinate of the control point (adjust as needed)
                        x2 = size.width, // X-coordinate of the end point (end of the canvas)
                        y2 = 0f // Y-coordinate of the end point (top of the canvas)
                    )
                }
                val slingshotHolder = Path().apply {
                    addRoundRect(
                        roundRect = RoundRect(
                            left = vertexX - 16.dp.toPx(),
                            right = vertexX + 16.dp.toPx(),
                            top = vertexY / 2 - 6.dp.toPx(),
                            bottom = vertexY / 2 + 6.dp.toPx(),
                            radiusX = 6.dp.toPx(),
                            radiusY = 6.dp.toPx()
                        )
                    )
                }

                drawPath(
                    path = slingshot,
                    color = Color(0xFF9bc0ff),
                    style = Stroke(width = 8.dp.toPx())
                )
                drawPath(
                    path = slingshotRope,
                    color = Color.White,
                    style = Stroke(width = 3.dp.toPx())
                )

                drawCircle(
                    color = Color(0xFF9bc0ff),
                    radius = 4.dp.toPx(),
                    center = Offset.Zero
                )
                drawCircle(
                    color = Color(0xFF9bc0ff),
                    radius = 4.dp.toPx(),
                    center = Offset(size.width, 0f)
                )

                drawPath(
                    path = slingshotHolder,
                    color = Color.Black
                )
                //ball
                translate (
                    top = stoneTranslation * 5
                ) {
                    drawCircle(
                        color = Color.White,
                        radius = 8.dp.toPx(),
                        center = Offset(vertexX, vertexY / 2 - 12.dp.toPx())
                    )
                }

            }
        }
        OutlinedButton(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    scaleY = buttonScale
                    scaleX = buttonScale
                }
                .drawBehind {
                    //draw some stones
                    drawCircle(
                        color = Color.Black,
                        radius = 12.dp.toPx(),
                        center = Offset(size.center.x, size.height - 12.dp.toPx())
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = 12.dp.toPx(),
                        center = Offset(size.center.x - 24.dp.toPx(), size.height - 18.dp.toPx())
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = 12.dp.toPx(),
                        center = Offset(size.center.x + 24.dp.toPx(), size.height - 18.dp.toPx())
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = 12.dp.toPx(),
                        center = Offset(size.center.x - 6.dp.toPx(), size.height - 36.dp.toPx())
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = 12.dp.toPx(),
                        center = Offset(size.center.x + 18.dp.toPx(), size.height - 42.dp.toPx())
                    )
                },
            interactionSource = buttonInteractionSource,
            onClick = { isStonePlaced = true },
            shape = CircleShape,
            border = BorderStroke(
                width = 2.dp,
                color = Color.Green
            )
        ) {
            Text(
                text = "Add a stone!",
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SlingshotPrev() {
    Slingshot()
}