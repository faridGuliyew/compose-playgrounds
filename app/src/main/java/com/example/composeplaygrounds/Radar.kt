package com.example.composeplaygrounds

import android.graphics.Paint
import android.graphics.drawable.VectorDrawable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.composeplaygrounds.ui.theme.RadarColor
import com.example.composeplaygrounds.ui.theme.RadarColorGreen
import kotlinx.coroutines.launch

class ItemState(
    offsetX: Int,
    offsetY: Int,
    val isEnemy: Boolean,
) {
    var offsetX by mutableIntStateOf(offsetX)
    var offsetY by mutableIntStateOf(offsetY)
}

@Composable
fun rememberItemState(
    offsetX: Int,
    offsetY: Int,
    isEnemy: Boolean,
): ItemState {
    return remember {
        ItemState(
            offsetX, offsetY, isEnemy
        )
    }
}

@Composable
fun Radar() {

    val items = remember {
        mutableStateListOf<ItemState>(
            ItemState(
                100,
                100,
                false
            )
        )
    }
    //RadarView
    var isRadarViewFocused by remember {
        mutableStateOf(false)
    }
    AnimatedContent(targetState = isRadarViewFocused) { focused ->
        if (focused) {
            RadarView(
                items = items
            ) {
                isRadarViewFocused = false
            }
        } else {
            FieldView(
                items = items
            ) {
                isRadarViewFocused = true
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldView(
    items: MutableList<ItemState>,
    onSwitchMode: () -> Unit,
) {
    val density = LocalDensity.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {

                },
                actions = {
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red
                        ),
                        onClick = {
                            items.add(
                                ItemState(
                                    offsetX = (0..200).random(),
                                    offsetY = (0..200).random(),
                                    isEnemy = true
                                )
                            )
                        }
                    ) {
                        Text(text = "Enemy")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray
                        ),
                        onClick = {
                            items.add(
                                ItemState(
                                    offsetX = (0..200).random(),
                                    offsetY = (0..200).random(),
                                    isEnemy = false
                                )
                            )
                        }
                    ) {
                        Text(text = "Warrior")
                    }
                }, navigationIcon = {
                    IconButton(onClick = onSwitchMode) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "radar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items.forEach { item ->
                ItemView(
                    item = item,
                    onSwordClicked = {
                        val enemy = items.find { it.isEnemy }
                        enemy?.let {
                            item.offsetY = it.offsetY
                            item.offsetX = (it.offsetX + with(density) {100.dp.toPx()}).toInt()
                        }
                        return@ItemView enemy
                    }
                )
            }
        }
    }

}

@Composable
fun ItemView(
    item: ItemState,
    onSwordClicked : () -> ItemState?
) {
    var isDialogActive by remember {
        mutableStateOf(true)
    }

    var isAttacking by remember {
        mutableStateOf(false)
    }
    var targetedEnemy by remember {
        mutableStateOf<ItemState?>(null)
    }
    val swordRotation by animateFloatAsState(
        targetValue = if (isAttacking) -360f else 0f,
        animationSpec = tween(200),
        finishedListener = {
            isAttacking = false
            //enemy is sworded
            targetedEnemy?.offsetX = -1000
        }
    )
    val scope = rememberCoroutineScope()
    val animatedX by animateIntAsState(
        targetValue = item.offsetX,
        animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessVeryLow),
        finishedListener = {
            isAttacking = true
        }
    )

    val animatedY by animateIntAsState(
        targetValue = item.offsetY,
        animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessVeryLow)
    )


    Column(
        modifier = Modifier
            .offset {
                IntOffset(
                    animatedX,
                    animatedY
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    item.offsetX += dragAmount.x.toInt()
                    item.offsetY += dragAmount.y.toInt()
                }
            }
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                }, indication = null
            ) {
                isDialogActive = isDialogActive.not()
            },
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .width(120.dp)
                .graphicsLayer {
                    scaleX = if (isDialogActive) 1f else 0f
                    scaleY = scaleX
                }
        ) {
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {
                targetedEnemy = onSwordClicked()
                isDialogActive=false
            }
            ) {
                Image(
                    modifier = Modifier.size(32.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_sword),
                    contentDescription = null
                )
            }
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { ;isDialogActive=false }
            ) {
                Image(
                    modifier = Modifier.size(32.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_crossbow),
                    contentDescription = null
                )
            }
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { ;isDialogActive=false }
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_aid),
                    contentDescription = null
                )
            }
        }
        val swordPainter = rememberVectorPainter(
            image = ImageVector.vectorResource(id = R.drawable.ic_sword)
        )
        Image(
            modifier = Modifier
                .size(120.dp)
                .drawBehind {
                    scale(1f - swordRotation / 360f) {
                        rotate(swordRotation) {
                            swordPainter.apply {
                                draw(
                                    size
                                )
                            }
                        }
                    }
                },
            painter = painterResource(id = if (item.isEnemy) R.drawable.ic_enemy else R.drawable.ic_warrior),
            contentDescription = "item"
        )
    }

}

@Composable
fun RadarView(
    items: MutableList<ItemState>,
    onSwitchMode: () -> Unit,
) {
    val config = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = remember {
        with(density) { config.screenHeightDp.dp.toPx() }
    }
    val screenWidthPx = remember {
        with(density) { config.screenWidthDp.dp.toPx() }
    }
    val isPanic by remember {
        mutableStateOf(items.filter { it.isEnemy }.size > items.filter { it.isEnemy.not() }.size)
    }
    val colorAnimation by rememberInfiniteTransition().animateColor(
        initialValue = RadarColorGreen,
        targetValue = if (isPanic) Color.Red else RadarColorGreen,
        animationSpec = infiniteRepeatable(
            tween(3000), repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val blinkingColor by rememberInfiniteTransition().animateColor(
        initialValue = Color.Red,
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(
                if (isPanic) 1000 else 3000,
                easing = LinearEasing
            )
        ), label = ""
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RadarColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Canvas(
            modifier = Modifier
                .height(240.dp)
                .fillMaxWidth()
        ) {
            //Draw detected items
            items.forEach {
                drawCircle(
                    color = if (it.isEnemy) blinkingColor.copy(0.7f) else Color.Green,
                    radius = 12.dp.toPx(),
                    center = Offset(
                        x = size.width * ((it.offsetX + 75.dp.toPx()) / (screenWidthPx)),
                        y = size.height * ((it.offsetY + 75.dp.toPx()) / (screenHeightPx))
                    )
                )
            }
            ///Draw base
            repeat(6) {
                drawCircle(
                    color = colorAnimation,
                    radius = size.width / 10 + 32.dp.toPx() * it,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            drawLine(
                color = colorAnimation,
                start = Offset(size.center.x, 0f - 80.dp.toPx()),
                end = Offset(size.center.x, size.height + 80.dp.toPx()),
                strokeWidth = 2.dp.toPx()
            )

            drawLine(
                color = colorAnimation,
                start = Offset(0f, size.center.y),
                end = Offset(size.width, size.center.y),
                strokeWidth = 2.dp.toPx()
            )

            //rotation should appear on top of the lines
            rotate(rotation) {
                drawContext.canvas.nativeCanvas.apply {
                    drawRect(
                        size.center.x,
                        size.center.y + 18.dp.toPx(),
                        size.width * 0.9f,
                        size.center.y - 18.dp.toPx(),
                        Paint().apply {
                            setColor(Color.Transparent.toArgb())
                            setShadowLayer(
                                80.dp.toPx(),
                                0f,
                                -40.dp.toPx(),
                                colorAnimation.toArgb()
                            )
                        }
                    )
                }
                drawLine(
                    color = colorAnimation,
                    start = Offset(size.center.x, size.center.y),
                    end = Offset(size.width, size.center.y),
                    strokeWidth = 2.dp.toPx()
                )
            }

            //bullseye should be on top
            drawCircle(
                color = colorAnimation,
                radius = 10.dp.toPx()
            )

            ///End draw base
        }

    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = RadarColorGreen.copy(0.7f)
            ),
            onClick = onSwitchMode
        ) {
            Text(
                text = "Switch to area",
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RadarPrev() {

    Radar()

}