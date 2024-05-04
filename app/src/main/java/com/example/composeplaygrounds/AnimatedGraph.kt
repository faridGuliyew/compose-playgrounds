package com.example.composeplaygrounds

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun BarGraph(
    items : List<String>
) {
    val config = BarGraphConfig()

    var totalHeightPx by remember {
        mutableIntStateOf(0)
    }
    val animatedHeight by animateIntAsState(
        targetValue = totalHeightPx,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )
    LazyRow(
        modifier = Modifier.onSizeChanged {
            totalHeightPx = it.height
        },
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(10) {
            BarGraphItem(
                percentage = 1f - 0.1f * it,
                totalHeightPx = animatedHeight,
                config = config
            )
        }
    }
}

data class BarGraphConfig (
    val topRectHeight : Dp = 36.dp,
    val topRectCornerRadius : Dp = 16.dp,
    val barTopPadding : Dp = 20.dp,
    val textHeight : TextUnit = 18.sp,
    val barBottomPadding : Dp = 12.dp,
    val itemData : List<String> = emptyList()
)

@Composable
fun BarGraphItem(
    percentage: Float = 1f,
    totalHeightPx : Int,
    config: BarGraphConfig
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
    ) {

        val density = LocalDensity.current
        val minHeightDp = with(density) {
            ((totalHeightPx * percentage)
                .coerceAtLeast(
                    (config.barTopPadding + config.barBottomPadding + config.textHeight.toDp() + config.topRectHeight + 6.5.dp /*error?*/).toPx()
                ) / density.density).dp
        }

        val textMeasurer = rememberTextMeasurer()

        Spacer(modifier = Modifier.weight(1f))
        Canvas(
            modifier = Modifier
                .height(minHeightDp)
                .width(64.dp)
        ) {

            val topRectHeight = config.topRectHeight.toPx()
            val barTopPadding = config.barTopPadding.toPx()
            val textHeight = config.textHeight
            val barBottomPadding = config.barBottomPadding.toPx()

            val cornerRadius = config.topRectCornerRadius.toPx()

            //Firstly, calculate where the bottom text goes
            val textStyle = TextStyle(color = Color.Black, fontSize = textHeight)
            val measuredSize = textMeasurer.measure("12/09", style = textStyle).size
            val bottomTextOffset =
                Offset(size.center.x - measuredSize.width / 2, size.height - measuredSize.height)
            drawText(
                textMeasurer = textMeasurer,
                text = "12/09",
                topLeft = bottomTextOffset
            )

            //Secondly, calculate where the top background goes
            drawCircle(
                color = Color.Yellow,
                radius = 4.dp.toPx(),
                center = Offset(size.center.x, topRectHeight)
            )
            drawRoundRect(
                color = Color.Yellow,
                size = Size(size.width, topRectHeight),
                cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
            )
            //Thirdly, calculate where to draw top text
            val topTextStyle = TextStyle(color = Color.Black)
            val topMeasuredSize = textMeasurer.measure("120.92", style = topTextStyle).size
            val topTextOffset = Offset(
                size.center.x - topMeasuredSize.width / 2,
                topRectHeight / 2 - topMeasuredSize.height / 2
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "120.92",
                topLeft = topTextOffset
            )

            //Finally, calculate where the bar goes
            val topPadding = topRectHeight + barTopPadding
            drawRoundRect(
                color = Color.Red,
                topLeft = Offset(
                    x = 0f,
                    y = topPadding
                ),
                size = Size(
                    width = size.width,
                    height = size.height - measuredSize.height - topPadding - barBottomPadding
                ),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun AnimatedGraphPrev() {
    BarGraph(items = listOf("hi"))
}