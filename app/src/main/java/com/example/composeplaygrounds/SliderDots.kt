package com.example.composeplaygrounds

import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.provider.FontsContractCompat.Columns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun SliderDots() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState {
            6
        }
        var offsetX by remember {
            mutableFloatStateOf(0f)
        }
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(0.5f),
            state = pagerState) {
            Image(
                modifier = Modifier.border(1.dp, Color.Black),
                painter = painterResource(id = R.drawable.ic_enemy),
                contentDescription = null
            )
        }
        Row(modifier = Modifier
            .padding(vertical = 6.dp)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress (
                    onDragStart = {
                        offsetX = it.x
                    }
                ) { change, dragAmount ->
                    offsetX += dragAmount.x
                    val selectedDot = (offsetX / 22.dp.toPx()).roundToInt()
                    scope.launch {
                        pagerState.animateScrollToPage(
                            selectedDot,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                        )
                    }
                }
            }.pointerInput(Unit) {
                detectTapGestures {
                    val offsetX = it.x
                    val selectedDot = (offsetX / 22.dp.toPx()).roundToInt()
                    scope.launch {
                        pagerState.animateScrollToPage(
                            selectedDot,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                        )
                    }
                }
            }
            ,horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (0 until pagerState.pageCount).forEach {
                val progress = pagerState.getOffsetFractionForPage(it)
                Dot(progress = abs(progress), progress >= 0)
            }
        }
    }
}

@Composable
fun Dot(progress: Float, isGoingUp : Boolean = false) {
    Box(modifier = Modifier
        .size(16.dp)
        .clip(CircleShape)
        .background(Color.Gray)
        .drawBehind {
            val offset = (16.dp.toPx()) * progress.coerceIn(0f, 1f)
            translate(
                left = offset * if (isGoingUp) 1f else -1f
            ) {
                drawRect(color = Color.Green)
            }
        })
}