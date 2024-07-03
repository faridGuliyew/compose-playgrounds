package com.example.composeplaygrounds

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
@Preview(showBackground = true)
fun CustomBottomSheetScreen(modifier: Modifier = Modifier) {

    Column(modifier = modifier.fillMaxSize()) {

        CustomBottomSheet()

    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomBottomSheet(modifier: Modifier = Modifier) {
    var state by remember {
        mutableIntStateOf(0)
    }
    val density = LocalDensity.current
    val anchoredState = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Start,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = tween(),
        ).apply {
            updateAnchors(
                DraggableAnchors {
                    DragAnchors.Start at 0f
                    DragAnchors.End at -400f
                }
            )
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        AnimatedContent (targetState = state, transitionSpec = { expandVertically() togetherWith  shrinkVertically() }) { it->
            when (it) {
                0 -> IdleContent (anchoredState) { state++ }
                1 -> IncomingContent {state--}
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IdleContent(state : AnchoredDraggableState<DragAnchors>, onNextState : () -> Unit) {

    Box(modifier = Modifier
        .anchoredDraggable(state, orientation = Orientation.Vertical)
        .fillMaxWidth()
        .height(250.dp)
        .clickable {
            onNextState()
        }
        .background(Color.Magenta), contentAlignment = Alignment.Center) {
        Text(text = "Çatdım", fontSize = 32.sp)
    }
}


@Composable
fun IncomingContent(onNextState: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(400.dp)
        .clickable {
            onNextState()
        }
        .background(Color.Magenta), contentAlignment = Alignment.Center) {
        Text(text = "Çatdım", fontSize = 32.sp)
    }
}
