package com.example.composeplaygrounds

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun MickeyMouse() {

    val config = LocalConfiguration.current
    val mouthRotation = remember {
       Animatable(0f)
    }
    var headRotationX by remember {
        mutableStateOf(0f)
    }
    var headRotationY by remember {
        mutableStateOf(0f)
    }
    val scope = rememberCoroutineScope()
    val huseynBitmap = ImageBitmap.imageResource(id = R.drawable.img_husain_cat)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(config.screenHeightDp.dp * 0.5f)
            .graphicsLayer {
                rotationX = -headRotationY
                rotationY = headRotationX
            }
            .zIndex(if (abs(headRotationX % 360f) > 90f && abs(headRotationX % 360f) < 270f) 1f else 0f)
        ) {

            translate(left = 25.dp.toPx()) {
                drawImage(
                    image = huseynBitmap,
                    srcOffset = IntOffset.Zero,
                    dstOffset = IntOffset.Zero,
                    srcSize = IntSize(size.width.toInt(), size.height.toInt()),
                    dstSize = IntSize(size.width.toInt(), size.height.toInt()),
                )
            }
        }

        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(config.screenHeightDp.dp * 0.5f)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    headRotationX += dragAmount.x
                    headRotationY += dragAmount.y
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    scope.launch {
                        mouthRotation.animateTo(targetValue = -50f)
                        mouthRotation.animateTo(targetValue = 0f)
                    }
                }
            }
            .graphicsLayer {
                rotationX = -headRotationY
                rotationY = headRotationX
            }.background(Color.White)
        ) {

            //sol qulaq
            drawCircle(
                color = Color.Black,
                radius = 72.dp.toPx(),
                center = Offset(x = 72.dp.toPx(), y = 12.dp.toPx())
            )
            //sag qulaq
            drawCircle(
                color = Color.Black,
                radius = 72.dp.toPx(),
                center = Offset(x = size.width - 72.dp.toPx(), y = 12.dp.toPx())
            )

            //sol goz
            drawCircle(
                color = Color.Black,
                radius = 64.dp.toPx(),
                center = Offset(x = size.width * 0.30f, y = size.height * 0.38f),
                style = Stroke(width = 2.5.dp.toPx())
            )

            //sol goz inner
            drawCircle(
                color = Color.Black,
                radius = 48.dp.toPx(),
                center = Offset(x = size.width * 0.30f, y = size.height * 0.36f)
            )

            //sag goz
            drawCircle(
                color = Color.Black,
                radius = 64.dp.toPx(),
                center = Offset(x = size.width * 0.70f, y = size.height * 0.38f),
                style = Stroke(width = 2.5.dp.toPx())
            )

            //sag goz inner
            drawCircle(
                color = Color.Black,
                radius = 48.dp.toPx(),
                center = Offset(x = size.width * 0.70f, y = size.height * 0.36f)
            )

            //bas
            drawCircle(
                color = Color.Black,
                style = Stroke(width = 2.dp.toPx())
            )

            //burun
            drawCircle(
                color = Color.Black,
                radius = 28.dp.toPx(),
                center = size.center.copy(y = size.center.y + 32.dp.toPx())
            )

            //agiz
            val path = Path().apply {
                this.moveTo(x = size.width * 0.30f, y = size.height * 0.60f)
                this.quadraticBezierTo(x1 = size.width * 0.30f, y1 = size.height * 0.90f, x2 = size.width * 0.60f, y2 = size.height * 0.75f)
            }
            rotate(mouthRotation.value) {
                drawPath(path = path, color = Color.Black)
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
private fun MickeyMousePrev() {
    MickeyMouse()
}