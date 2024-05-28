package com.example.composeplaygrounds

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview(showBackground = true)
fun PutTheDotInTheBox(modifier: Modifier = Modifier) {

    val config = LocalConfiguration.current
    val density = LocalDensity.current


    var absoluteOffsetX by remember {
        mutableFloatStateOf(0f)
    }

    var absoluteOffsetY by remember {
        mutableFloatStateOf(0f)
    }

    val difference = with(density) {
        (config.screenWidthDp.dp.toPx() / 2 - 90.dp.toPx()) - absoluteOffsetY
    }
    val animatedOffsetX by animateFloatAsState(targetValue = absoluteOffsetX, spring(), label = "")
    val animatedOffsetY by animateFloatAsState(targetValue = absoluteOffsetY, spring(), label = "")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {

        Row(
            modifier = Modifier
                .background(Color.Black)
        ) {
            Box(modifier = Modifier.run {
                weight(1f)
                    .height(180.dp)
                    .padding(12.dp)
                    .background(if (difference in 0f..100f) Color.Green else Color.Gray)
            })
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
                    .padding(12.dp)
                    .background(Color.Gray)
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        detectDragGestures { change, dragAmount ->
            absoluteOffsetX += dragAmount.x
            absoluteOffsetY += dragAmount.y
        }
    }, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer {
                    translationX = animatedOffsetX
                    translationY = animatedOffsetY
                }
                .clip(CircleShape)
                .background(Color.Black)
        )
    }
}