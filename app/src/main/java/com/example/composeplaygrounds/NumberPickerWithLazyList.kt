package com.example.composeplaygrounds

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NumberPickerWithLazyList () {

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val state = rememberLazyListState(50)
        val isScrollingUp = state.isScrollingUp()

        val rotation by animateFloatAsState(
            targetValue = if (state.isScrollInProgress.not()) 0f else if (isScrollingUp) 20f else -20f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )

        val firstIndex by remember {
            derivedStateOf {
                state.firstVisibleItemIndex
            }
        }
        val visibleCount by remember {
            derivedStateOf {
                state.layoutInfo.visibleItemsInfo.size
            }
        }

        LaunchedEffect(key1 = visibleCount, key2 = firstIndex) {
            Log.e("NUMBERS!", "visibleCount: $visibleCount, firstIndex: $firstIndex")
        }

        val midIndex by remember {
            derivedStateOf {
                visibleCount / 2 + firstIndex
            }
        }

        Canvas(modifier = Modifier.height(30.dp).fillMaxWidth()) {
            drawLine(
                color = Color.Black.copy(0.2f),
                start = Offset.Zero,
                end = Offset(size.width, 0f),
                strokeWidth = 5.dp.toPx()
            )
            drawLine(
                color = Color.Green,
                start = Offset.Zero,
                end = Offset(size.width * (state.firstVisibleItemIndex / 100f), 0f),
                strokeWidth = 5.dp.toPx()
            )
            val verticalPath = Path().apply {
                moveTo(size.width/2,0f)
                lineTo(size.width/2,size.height)
            }
            drawPath(verticalPath, Color.Black, style = Stroke(2.dp.toPx()))
        }

        Canvas(modifier = Modifier.size(64.dp)) {
            val path = Path().apply {
                lineTo(size.width, 0f)
                lineTo(size.width/2,size.height)
                lineTo(0f,0f)
            }
            rotate(rotation) {
                drawPath(
                    path,
                    color = Color.Blue
                )
            }
        }
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            state = state,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = state)
        ) {
            items(100) {
                val deviation =  abs(midIndex - it)
                val style = TextStyle(
                    fontSize = (32-deviation * 5).sp,
                    color = Color.Black.copy(1f - if (deviation <= 5) deviation * 0.2f else 0f)
                )
                Text(
                    modifier = Modifier.graphicsLayer {
                        translationY = deviation*deviation * (-10f)
                    },
                    text = "${it+100}",
                    style = style
                )
            }
        }
    }
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

@Preview (showBackground = true)
@Composable
fun NumberPickerWithLazyListPrev () {
    NumberPickerWithLazyList()
}