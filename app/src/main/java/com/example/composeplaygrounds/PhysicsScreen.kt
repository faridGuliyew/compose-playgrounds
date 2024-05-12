package com.example.composeplaygrounds

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

data class ParticleState(
    val initialVelocityX: Float,
    val initialVelocityY: Float,
    val initialRadius: Float,
    val initialCenterX: Float,
    val initialCenterY: Float,
    val fps : Int = 100
) {
    val particleCenterX = Animatable(initialCenterX)
    val particleCenterY = Animatable(initialCenterY)

    var velocityX by mutableFloatStateOf(initialVelocityX)
    var velocityY by mutableFloatStateOf(initialVelocityY)

    var radius by mutableFloatStateOf(initialRadius)

    var isVelocityVisible by mutableStateOf(false)
}

@Composable
fun PhysicsScreen() {
    val particles = remember {
        mutableStateListOf(
            ParticleState(
                initialVelocityX = 500f,
                initialVelocityY = 400f,
                initialRadius = 40f,
                initialCenterX = 200f,
                initialCenterY = 400f
            )
        )
    }
    
    var collisionCount by remember {
        mutableIntStateOf(0)
    }

    var isVelocityVisible by remember {
        mutableStateOf(false)
    }

    var isAddDialogActive by remember {
        mutableStateOf(false)
    }

    fun detectCollision() : Boolean {
        for (i in particles.indices) {
            for (j in particles.indices) {
                //ensure they are different particles
                if (i != j) {
                    //check if they collide
                    val mainParticleState = particles[i]
                    val checkParticleState = particles[j]
                    if (
                        abs(mainParticleState.particleCenterX.value - checkParticleState.particleCenterX.value) <= mainParticleState.radius + checkParticleState.radius
                        &&
                        abs(mainParticleState.particleCenterY.value - checkParticleState.particleCenterY.value) <= mainParticleState.radius + checkParticleState.radius
                    ) {
                        Log.e("collision", "detected!")
                        //handle collision
//                        if (mainParticleState.radius > checkParticleState.radius) {
//                            mainParticleState.radius += checkParticleState.radius
//                            particles.remove(checkParticleState)
//                        } else {
//                            checkParticleState.radius += mainParticleState.radius
//                            particles.remove(mainParticleState)
//                        }

                        return true
                    }
                }
            }
        }
        return false
    }

    LaunchedEffect(key1 = Unit) {
        var isColliding = false
        while (true) {
            delay(10) //2 fps
            if (isColliding) {
                collisionCount++
                delay(300)
            }
            isColliding = detectCollision()
        }
    }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = {
            isAddDialogActive = true
        }) {
            Text(text = "Add")
        }
        Text(text = "Collisions: $collisionCount", fontSize = 32.sp)
        Button(onClick = {
            isVelocityVisible = isVelocityVisible.not()
            particles.forEach {
                it.isVelocityVisible = isVelocityVisible
            }
        }) {
            Text(text = "Show route")
        }
    }

    if (isAddDialogActive) {
        var velocityX by remember {
            mutableStateOf("100")
        }
        var velocityY by remember {
            mutableStateOf("100")
        }
        var initialX by remember {
            mutableStateOf("100")
        }
        var initialY by remember {
            mutableStateOf("100")
        }
        var radius by remember {
            mutableStateOf("50")
        }
        Dialog(onDismissRequest = { isAddDialogActive = false }) {
            Column {
                TextField(value = velocityX, onValueChange = { velocityX = it }, label = { Text(text = "Velocity X")})
                TextField(value = velocityY, onValueChange = { velocityY = it }, label = { Text(text = "Velocity Y")})
                TextField(value = initialX, onValueChange = { initialX = it}, label = { Text(text = "Initial X")})
                TextField(value = initialY, onValueChange = { initialY = it }, label = { Text(text = "Initial Y")})
                TextField(value = radius, onValueChange = { radius = it }, label = { Text(text = "Radius")})
                Button(onClick = {
                    particles.add(
                        ParticleState(
                            initialVelocityX = velocityX.toFloatOrNull() ?: 100f,
                            initialVelocityY = velocityY.toFloatOrNull() ?: 100f,
                            initialRadius = radius.toFloatOrNull() ?: 100f,
                            initialCenterX = initialX.toFloatOrNull() ?: 0f,
                            initialCenterY = initialY.toFloatOrNull() ?: 0f
                        ).apply { isVelocityVisible }
                    )
                }) {
                    Text(text = "ADD!")
                }
            }

        }
    }

    particles.forEach {
        PhysicsParticle(it)
    }

}

@Composable
fun PhysicsParticle(
    state: ParticleState,
) {

    val config = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = remember {
        with(density) { config.screenWidthDp.dp.toPx() }
    }

    val screenHeightPx = remember {
        with(density) { config.screenHeightDp.dp.toPx() }
    }

    val isHeadingRight by remember {
        derivedStateOf {
            state.velocityX > 0f
        }
    }

    val isHeadingDown by remember {
        derivedStateOf {
            state.velocityY > 0f
        }
    }

    LaunchedEffect(key1 = Unit) {
        //check for x collisions
        while (true) {
            delay(1000L / state.fps)
            //check for boundary collisions
            if (
                (isHeadingRight && state.particleCenterX.value >= screenWidthPx - state.radius)
                ||
                (isHeadingRight.not() && state.particleCenterX.value <= state.radius)
            ) {
                state.velocityX = -state.velocityX
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        //check for y collisions
        while (true) {
            delay(1000L / state.fps)
            if (
                (isHeadingDown && state.particleCenterY.value >= screenHeightPx - state.radius)
                ||
                (isHeadingDown.not() && state.particleCenterY.value <= state.radius)
            ) {
                state.velocityY = -state.velocityY
            }
        }
    }


    LaunchedEffect(key1 = state.velocityX, state.velocityY) {
        //recalculate coordinates on collision
        launch {
            val targetX = if (isHeadingRight) screenWidthPx else 0f
            val currentX = state.particleCenterX.value
            val distanceToBounds = abs(targetX - currentX)
            val durationInSeconds = (distanceToBounds / abs(state.velocityX)).toInt()

            state.particleCenterX.animateTo(
                targetValue = targetX,
                animationSpec = tween(
                    durationMillis = durationInSeconds * 1000,
                    easing = LinearEasing
                )
            )
        }
        launch {
            val targetY = if (isHeadingDown) screenHeightPx else 0f
            val currentY = state.particleCenterY.value
            val distanceToBounds = abs(targetY - currentY)
            val durationInSeconds = (distanceToBounds / abs(state.velocityY)).toInt()

            state.particleCenterY.animateTo(
                targetValue = targetY,
                animationSpec = tween(
                    durationMillis = durationInSeconds * 1000,
                    easing = LinearEasing
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                //first circle
                translate(left = state.particleCenterX.value, top = state.particleCenterY.value) {
                    drawCircle(
                        color = Color.Black,
                        radius = state.radius,
                        center = Offset.Zero
                    )
                    if (state.isVelocityVisible) {
                        //x axis
                        drawLine(
                            color = Color.Red,
                            start = Offset.Zero,
                            end = Offset(
                                (12.dp.toPx() + state.radius) * if (isHeadingRight) 1f else -1f,
                                0f
                            ),
                            strokeWidth = 6.dp.toPx()
                        )
                        //y axis
                        drawLine(
                            color = Color.Red,
                            start = Offset.Zero,
                            end = Offset(
                                0f,
                                (12.dp.toPx() + state.radius) * if (isHeadingDown) 1f else -1f
                            ),
                            strokeWidth = 6.dp.toPx()
                        )
                    }

                }
            }
    ) {

    }
}


@Preview(showBackground = true)
@Composable
fun PhysicsScreenPrev() {
    PhysicsScreen()
}