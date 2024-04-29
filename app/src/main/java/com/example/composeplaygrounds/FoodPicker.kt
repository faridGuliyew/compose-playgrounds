package com.example.composeplaygrounds

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.composeplaygrounds.ui.theme.latinFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FoodPicker() {
    val config = LocalConfiguration.current
    val scope = rememberCoroutineScope()
    var isDraggedUp by remember {
        mutableStateOf(false)
    }
    var selectedName by remember {
        mutableStateOf("Milkshake 1")
    }
    var price by remember {
        mutableIntStateOf(10)
    }
    val state = rememberFoodPickerState(
        items = arrayOf(
            initialValues[0],
            initialValues[1],
            initialValues[2],
            initialValues[3],
            initialValues[4]
        )
    )
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            AnimatedContent(
                modifier = Modifier.weight(1f),
                targetState = selectedName,
                transitionSpec = {
                    slideInHorizontally { -it } + scaleIn() togetherWith slideOutHorizontally { it / 3 } + scaleOut()
                }
            ) { text ->
                Text(
                    text = text,
                    fontSize = 32.sp,
                    fontFamily = latinFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AnimatedContent(
                targetState = price,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { height -> height } + fadeIn() togetherWith
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        slideInVertically { height -> -height } + fadeIn() togetherWith
                                slideOutVertically { height -> height } + fadeOut()
                    }.using(
                        SizeTransform(clip = false)
                    )
                }
            ) { targetCount ->
                Text(
                    text = "${targetCount} AZN",
                    fontSize = 24.sp,
                    fontFamily = latinFamily,
                    fontWeight = FontWeight.Bold
                )
            }


        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        val orderedItems = state.foodItems.sortedBy { it.order }
                        Log.e(
                            "items",
                            orderedItems
                                .map { it.name }
                                .toString()
                        )
                        orderedItems.mapIndexed { index, foodItem ->
                            scope.launch {
                                val nextIndex = if (isDraggedUp) {
                                    if (index == 0) 4 else (index - 1)
                                } else {
                                    if (index == 4) 0 else index + 1
                                }
                                if (nextIndex == 0) {
                                    selectedName = foodItem.name
                                    scope.launch {
                                        price = foodItem.price
                                    }
                                }
                                foodItem.apply {
                                    launch {
                                        size.animateTo(
                                            targetValue = statelessValues[nextIndex].size.value,
                                            animationSpec = tween(durationMillis = 1500)
                                        )
                                    }
                                    launch {
                                        verticalPercentage.animateTo(
                                            targetValue = statelessValues[nextIndex].verticalPercentage.value,
                                            animationSpec = tween(durationMillis = 1500)
                                        )
                                    }
                                    launch {
                                        horizontalOffset.animateTo(
                                            targetValue = statelessValues[nextIndex].horizontalOffset.value,
                                            animationSpec = tween(durationMillis = 1500)
                                        )
                                    }
                                    launch {
                                        delay(600)
                                        blur.animateTo(
                                            targetValue = statelessValues[nextIndex].blur.value,
                                            animationSpec = tween(1500),
                                        )
                                    }
                                }
                                foodItem.order = nextIndex
                            }
                        }
                    }
                ) { change, dragAmount ->
                    isDraggedUp = dragAmount.y > 0
                }
            }

    ) {
        state.foodItems.forEachIndexed { index, foodItem ->
            Image(
                modifier = Modifier
                    .height(foodItem.size.value.dp)
                    .graphicsLayer {
                        val screenWidth = config.screenWidthDp.dp.toPx()
                        val screenHeight = config.screenHeightDp.dp.toPx()
                        translationX = screenWidth * foodItem.horizontalOffset.value
                        translationY = screenHeight / 3 * foodItem.verticalPercentage.value
                    }
                    .zIndex(foodItem.blur.targetValue.times(-1))
                    .blur(foodItem.blur.targetValue.dp),
                painter = painterResource(id = foodItem.resource),
                contentDescription = "donut",
                contentScale = ContentScale.FillHeight
            )
        }
    }
}

val initialValues = listOf(
    FoodItem(
        0,
        0.9f,
        -0.1f,
        600,
        0f,
        10f,
        "Milkshake 1",
        15
    ), FoodItem(
        1,
        0.4f,
        0.1f,
        400,
        1.5f,
        9f,
        "McDonalds Milkshake",
        100,
        R.drawable.img_choco_shake
    ),
    FoodItem(
        2,
        0.2f,
        0.2f,
        200,
        3f,
        8f,
        "Cute Smoothie",
        17,
        R.drawable.img_cute_smoothie
    ),
    FoodItem(
        3,
        0f,
        0f,
        0,
        3f,
        0f,
        "Oreo Milkshake",
        8,
        R.drawable.img_oreo
    ),
    FoodItem(
        4,
        4f,
        -1f,
        0,
        0f,
        0f,
        "Milkshake 5",
        4
    )
)

val statelessValues = listOf(
    FoodItem(
        0,
        0.9f,
        -0.1f,
        600,
        0f,
        10f
    ), FoodItem(
        1,
        0.4f,
        0.2f,
        400,
        1.5f,
        9f
    ),
    FoodItem(
        2,
        0.2f,
        0.2f,
        200,
        3f,
        8f
    ),
    FoodItem(
        3,
        0f,
        0f,
        0,
        3f,
        0f
    ),
    FoodItem(
        4,
        4f,
        -1f,
        0,
        0f,
        0f
    )
)

@Composable
fun rememberFoodPickerState(
    items: Array<FoodItem>,
) = remember {
    FoodPickerState(items)
}

class FoodPickerState(
    items: Array<FoodItem>,
) {
    var foodItems = mutableStateListOf(*items)
}

class FoodItem(
    order: Int,
    verticalPercentage: Float,
    horizontalOffset: Float,
    size: Int,
    blur: Float,
    zIndex: Float,
    val name: String = "",
    val price : Int = 0,
    @DrawableRes val resource : Int = R.drawable.img_donut
) {
    var order by mutableIntStateOf(order)
    var verticalPercentage = Animatable(verticalPercentage)
    var horizontalOffset = Animatable(horizontalOffset)
    var size = Animatable(size, Int.VectorConverter)
    var blur = Animatable(blur)
    var zIndex by mutableFloatStateOf(zIndex)
}

@Preview(showBackground = true)
@Composable
private fun FoodPickerPrev() {
    FoodPicker()
}