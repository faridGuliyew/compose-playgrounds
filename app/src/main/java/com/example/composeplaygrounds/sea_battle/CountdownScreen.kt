package com.example.composeplaygrounds.sea_battle

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CountdownScreen() {
    var isVisible by remember {
        mutableStateOf(true)
    }

    AnimatedContent(targetState = isVisible) { visible->
        if (visible) {
            val countDown by produceState(initialValue = 3) {
                while (value >= 0) {
                    delay(1000)
                    value--
                }
                isVisible = false
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.2f)), contentAlignment = Alignment.Center) {
                AnimatedContent(targetState = countDown, transitionSpec = {
                    slideInVertically { -it } togetherWith slideOutVertically { it }
                }) { count->
                    Text(text = count.toString(), fontSize = 72.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
private fun CountdownScreenPRev() {
    CountdownScreen()
}