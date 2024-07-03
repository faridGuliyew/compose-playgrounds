package com.example.composeplaygrounds.meta.facebook

import android.util.Log
import androidx.annotation.RawRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.composeplaygrounds.R
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
@Preview(showBackground = true)
fun EmojiPane() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        EmojiContainer(
            emojis = listOf(
                R.raw.anim_wow,
                R.raw.lottie_like,
                R.raw.anim_wow,
                R.raw.lottie_like,
                R.raw.anim_wow,
                R.raw.lottie_like,
            )
        )
    }
}


@Composable
fun EmojiContainer(
    modifier: Modifier = Modifier,
    @RawRes emojis : List<Int>
) {
    var totalWidth by remember {
        mutableFloatStateOf(0f)
    }
    var isDragActive by remember {
        mutableStateOf(false)
    }
    var currentOffsetX by remember {
        mutableFloatStateOf(0f)
    }
    var selectedEmojiIndex by remember {
        mutableIntStateOf(-1)
    }
    Box(
        modifier = Modifier
            .onSizeChanged {
                totalWidth = it.width.toFloat()
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { it ->
                        isDragActive = true
                        currentOffsetX = it.x
                        Log.e("Drag", "onDragStart: $it")
                    }, onDragEnd = {
                        isDragActive = false
                        selectedEmojiIndex = -1
                        Log.e("Drag", "onDragEnd")
                    }
                ) { change, dragAmount ->
                    val fractionWidth = totalWidth / emojis.size
                    currentOffsetX += dragAmount.x
                    selectedEmojiIndex = (currentOffsetX / fractionWidth).toInt()
                    Log.e("Drag", "selected index: $selectedEmojiIndex")
                }
            }
            .fillMaxWidth(0.95f)
            .background(Color.Gray.copy(0.4f), shape = CircleShape)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            emojis.forEachIndexed { index, res->
                Emoji(
                    modifier = Modifier.size(64.dp),
                    anim = res,
                    scale = if (index == selectedEmojiIndex) 4f  else 1f,
                    multiplier = if (res == R.raw.lottie_like) 2f else 1f
                )
            }
        }
    }
}
@Composable
fun Emoji(
    modifier: Modifier = Modifier,
    @RawRes anim : Int = R.raw.anim_wow,
    scale : Float = 1f,
    multiplier : Float = 1f
) {
    val animatedScale by animateFloatAsState(targetValue = scale * multiplier, animationSpec = tween(500))
    val preloaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            anim
        )
    )

    val preloaderProgress by animateLottieCompositionAsState(
        preloaderLottieComposition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true
    )


    LottieAnimation(
        composition = preloaderLottieComposition,
        progress = preloaderProgress,
        modifier = modifier
            .graphicsLayer {
                this.scaleY = animatedScale
                this.scaleX = animatedScale
            }
            .zIndex(scale)
    )
}