package com.example.composeplaygrounds

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

enum class WaveState {
    IDLE, REGULAR
}

enum class PointsState {
    POSITIVE_FOCUSED, NEGATIVE_FOCUSED
}

enum class BoatState {
    IDLE, REGULAR, FAST, FASTER, SUBMARINE
}

@Composable
fun Fishing() {
    val config = LocalConfiguration.current
    val screenWidthPx = config.screenWidthDp.dp.toPx()
    var state by remember {
        mutableStateOf(WaveState.IDLE)
    }
    LaunchedEffect(key1 = Unit) {
        state = WaveState.REGULAR
    }
    val scope = rememberCoroutineScope()

    val translation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = screenWidthPx,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 5000, easing = LinearEasing
            ), repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val slowerTranslation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = screenWidthPx,
        animationSpec = infiniteRepeatable(
            tween(
                durationMillis = 20000, easing = LinearEasing
            ), repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    val boatRotation = remember {
        Animatable(0f)
    }
    var boatState by remember {
        mutableStateOf(BoatState.IDLE)
    }

    val regularCloudTranslation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = screenWidthPx * 2f,
        animationSpec = infiniteRepeatable(tween(60000, easing = LinearEasing))
    )
    val fastCloudTranslation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = screenWidthPx * 2f,
        animationSpec = infiniteRepeatable(tween(1000, easing = LinearEasing))
    )

    var isFinishing by remember {
        mutableStateOf(false)
    }
    val boatTranslationY = remember {
        Animatable(0f)
    }
    val boatTranslationX by animateFloatAsState(
        targetValue = if (isFinishing) screenWidthPx * 2f else 100.dp.toPx(),
        animationSpec = tween(2000)
    )

    var displayEndText by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = isFinishing) {
        if (isFinishing)  {
            delay(3000)
            displayEndText = true
        }
    }

    LaunchedEffect(key1 = boatState) {
        when (boatState) {
            BoatState.IDLE -> {
                //animate rotation
                launch {
                    while (true) {
                        boatRotation.animateTo(2f, tween(3000))
                        boatRotation.animateTo(-2f, tween(5000))
                    }
                }
                //animate elevation
                launch {
                    while (true) {
                        boatTranslationY.animateTo(-2f * 40f, tween(3000))
                        boatTranslationY.animateTo(40f, tween(5000))
                    }

                }
            }
            BoatState.REGULAR -> {
                //animate rotation
                launch {
                    while (true) {
                        boatRotation.animateTo(10f, tween(1000))
                        boatRotation.animateTo(-3f, tween(1000, easing = LinearEasing))
                    }
                }
                //animate elevation
                launch {
                    while (true) {
                        boatTranslationY.animateTo(-4f * 40f, tween(1000))
                        boatTranslationY.animateTo( 0f, tween(1000))
                    }

                }

            }
            BoatState.FAST -> {
                isFinishing = true
            }
            BoatState.FASTER -> TODO()
            BoatState.SUBMARINE -> TODO()
        }
    }

    val boat = ImageBitmap.imageResource(id = R.drawable.ic_boat)

    //sky
    Image(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(id = R.drawable.img_sky),
        contentDescription = "sky",
        contentScale = ContentScale.FillBounds
    )

    CloudLayer(translation = if (boatState == BoatState.IDLE) regularCloudTranslation else fastCloudTranslation)
    CloudLayer(translation = (if (boatState == BoatState.IDLE) regularCloudTranslation else fastCloudTranslation) - screenWidthPx)
    CloudLayer(translation = (if (boatState == BoatState.IDLE) regularCloudTranslation else fastCloudTranslation) -  2 * screenWidthPx)

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            visible = displayEndText,
            enter = fadeIn() + scaleIn()
        ) {
            Image(painter = painterResource(id = R.drawable.img_end_nobg), contentDescription = "end")
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            SwitchButton("Boat") {
                boatState = BoatState.entries[it]

            }
            Spacer(modifier = Modifier.width(24.dp))
            SwitchButton("Wave") {

            }
        }
    }
    val measurer = rememberTextMeasurer()
    Box(modifier = Modifier
        .fillMaxSize()
        .drawWithCache {
            val segmentCount = 4
            val segmentWidth = size.width / segmentCount
            val startX = -size.width.toInt()
            val startY = size.height * 0.8f
            val endX = size.width.toInt()
            val maxHeight = size.height / 48

            val amplitude = segmentCount * (PI / size.width)

            val smallerAmplitude = amplitude * 2


            val mainWaves = (startX * 3..endX step 5).map { x ->
                Offset(
                    x.toFloat(), (startY - maxHeight * sin(amplitude * x)).toFloat()
                )
            }
            val secondaryWaves = mainWaves.map {
                Offset(
                    x = it.x, y = it.y - 45f
                )
            }

            val backgroundWaves =
                (startX - segmentWidth.toInt()..endX + segmentWidth.toInt() step 5).map { x ->
                    Offset(
                        x.toFloat(), (startY - maxHeight * sin(amplitude * x)).toFloat()
                    )
                }
            onDrawBehind {
                //background waves
                translate(slowerTranslation + segmentWidth) {
                    backgroundWaves.forEach {
                        drawLine(
                            color = Color.Blue.copy(0.1f),
                            start = it.copy(y = it.y - 100f),
                            end = Offset(it.x, size.height),
                            strokeWidth = 6.dp.toPx()
                        )
                    }
                }

                //middle waves
                translate(translation / 2 + segmentWidth / 2) {
                    secondaryWaves.forEach {
                        drawLine(
                            color = Color.Blue.copy(0.2f),
                            start = it,
                            end = Offset(it.x, size.height),
                            strokeWidth = 6.dp.toPx()
                        )
                    }
                }

                //boat
                translate(top = boatTranslationY.value, left = boatTranslationX) {
                    rotate(-boatRotation.value) {
                        drawImage(
                            image = boat,
                            topLeft = Offset(0f, startY / 2.2f),
                            alpha = 0.7f
                        )
                    }
                }


                //main waves
                translate(if (boatState == BoatState.REGULAR) translation * 3f else translation) {
                    mainWaves.forEach {
                        drawLine(
                            color = Color.White,
                            start = it,
                            end = Offset(it.x, it.y + 10f),
                            strokeWidth = 6.dp.toPx()
                        )
                        drawLine(
                            color = Color.Blue.copy(0.4f),
                            start = it,
                            end = Offset(it.x, size.height),
                            strokeWidth = 6.dp.toPx()
                        )
                    }
                }
                if (displayEndText) {
                    translate(translation) {
                        drawText(
                            textMeasurer = measurer,
                            text = "Credits: Farid Guliyev :D",
                            topLeft = Offset(0f, size.height * 0.8f),
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = 32.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }) {

    }
}

@Composable
fun SwitchButton(
    primaryText: String,
    onOption: (Int) -> Unit,
) {

    var isBoatExpanded by remember {
        mutableStateOf(false)
    }

    Button(onClick = { isBoatExpanded = isBoatExpanded.not() }) {
        AnimatedContent(
            targetState = isBoatExpanded,
            transitionSpec = {
                slideInHorizontally() + fadeIn() togetherWith slideOutHorizontally() + fadeOut()
            }
        ) { expanded ->
            if (expanded.not()) {
                Text(text = primaryText)
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) {
                        Text(
                            modifier = Modifier.clickable {
                                onOption(it)
                            },
                            text = "${it + 1}",
                            fontSize = 28.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CloudLayer(translation : Float) {
    Box (
        modifier = Modifier.graphicsLayer {
            translationX = -translation
        }
    ) {
        Image(
            alpha = 0.8f,
            painter = painterResource(id = R.drawable.cloud_one),
            contentDescription = "cloud",
        )

        Image(
            modifier = Modifier.offset(x = 200.dp, y = 100.dp),
            alpha = 0.8f,
            painter = painterResource(id = R.drawable.cloud_two),
            contentDescription = "cloud",
        )
        Image(
            modifier = Modifier.offset(x = 400.dp, y = 70.dp),
            alpha = 0.8f,
            painter = painterResource(id = R.drawable.cloud_three),
            contentDescription = "cloud",
        )
        Image(
            modifier = Modifier.offset(x = 600.dp, y = 20.dp),
            alpha = 0.8f,
            painter = painterResource(id = R.drawable.cloud_four),
            contentDescription = "cloud",
        )
    }
}

@Composable
fun Dp.toPx(): Float {
    val density = LocalDensity.current
    return remember {
        with(density) { this@toPx.toPx() }
    }
}


@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun FishingPrev() {
    Fishing()
}