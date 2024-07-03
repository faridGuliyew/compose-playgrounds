package com.example.composeplaygrounds

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

class ItemState(
    val index: Int,
) {
    var offsetX by mutableIntStateOf(0)
    var offsetY by mutableIntStateOf(0)
    var isSelected by mutableStateOf(false)
    var isInDropArea by mutableStateOf(false)

    fun reset() {
        offsetX = 0
        offsetY = 0
        isInDropArea = false
        isSelected = false
    }
}

class ScreenState {
    var selectedItemIndex by mutableStateOf<Int?>(null)
}

@Composable
fun ReleaseToAdd() {

    val screenState = remember { ScreenState() }
    //demo items
    val items = remember {
        mutableStateListOf(
            *(0..10).map {
                ItemState(it)
            }.toTypedArray()
        )
    }
    val config = LocalConfiguration.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        //items
        LazyRow(
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = items) { item ->
                InitialItem(item = item, selectedIndex = screenState.selectedItemIndex,
                    onSelect = {
                        screenState.selectedItemIndex = item.index
                        item.isSelected = true
                    }, onDeselected = {
                        screenState.selectedItemIndex = null
                        item.reset()
                    }, areaToBeDropped = config.screenHeightDp.dp * 0.2f
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
        }
        //drop area
        if (screenState.selectedItemIndex != null) {
            val pathPhase by rememberInfiniteTransition().animateFloat(
                initialValue = 0f,
                targetValue = 100f,
                animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing)), label = ""
            )
            Box(modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.6f)
                .drawBehind {
                    drawRoundRect(
                        color = Color.Red, style = Stroke(
                            width = 2f,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(25f, 25f),
                                pathPhase
                            )
                        )
                    )
                }) {}
        }
    }
}


@Composable
fun InitialItem(
    item: ItemState,
    selectedIndex: Int?,
    areaToBeDropped: Dp,
    onSelect: () -> Unit,
    onDeselected: () -> Unit,
) {

    val translationX by animateFloatAsState(
        targetValue = if (selectedIndex == null || item.index == selectedIndex) 0f
        else if (item.index < selectedIndex) -100f else 100f
    )

    Box(modifier = Modifier
        .graphicsLayer {
            this.translationX = translationX + if (item.isSelected) item.offsetX.toFloat() else 0f
            this.translationY = if (item.isSelected) item.offsetY.toFloat() else 0f
        }
        .clip(CircleShape)
        .background(Color.Blue.copy(0.3f))
        .padding(32.dp)
        .drawBehind {
            if (item.isInDropArea) {
                translate(left = 16.dp.toPx(), top = -16.dp.toPx()) {

                    drawCircle(color = Color.Green, radius = 12.dp.toPx())
                    drawLine(color = Color.Black, start = size.center.copy(x = size.center.x - 12.dp.toPx()), end = size.center.copy(x = size.center.x + 12.dp.toPx()),)
                    drawLine(color = Color.Black, start = size.center.copy(y = size.center.y - 12.dp.toPx()), end = size.center.copy(y = size.center.y + 12.dp.toPx()),)
                }
            }
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(onDragStart = {
                onSelect()
            }, onDrag = { change, dragAmount ->
                item.offsetX += dragAmount.x.roundToInt()
                item.offsetY += dragAmount.y.roundToInt()
                item.isInDropArea = item.offsetY > areaToBeDropped.roundToPx()
            }, onDragEnd = {
                onDeselected()
            })
        }
    ) {
        Text(text = item.index.toString())
    }
}

@Preview(showBackground = true)
@Composable
private fun ReleaseToAddPrev() {
    ReleaseToAdd()
}