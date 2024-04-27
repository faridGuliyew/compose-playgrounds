package com.example.composeplaygrounds


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun WeightPicker(
    //Value that decides how separated values are. 0.1f is 10% of the screen
    gap : Float = 0.1f
) {
    var offsetX by remember {
        mutableFloatStateOf(0f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                }
            },
        verticalArrangement = Arrangement.Center
    ) {
        //Text measurer is used to draw text on canvas
        val textMeasurer = rememberTextMeasurer()

        var selectedValue by remember {
            mutableIntStateOf(0)
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "$selectedValue",
            textAlign = TextAlign.Center,
            fontSize = 32.sp
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            repeat(100) {
                //we calculate pixel difference between numbers here
                val difference = size.width * gap
                //that is the offset of the values
                val offset = difference * it + offsetX
                //deviation tell us how close the number is to the center. 0 means it is right in the center
                val centralDeviation = abs(offset - size.center.x)
                //check if the value is selected
                val isSelected = centralDeviation in 0f..difference / 2
                //you can customize your text however you like by using deviation and isSelected values
                val style = TextStyle(
                    fontSize = ((if (isSelected) 5f else 0f) + 24 - centralDeviation / 25).sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.Blue else Color.Black
                )
                //Here we measure the final text in order to determine the offset
                val measuredText = textMeasurer.measure(text = "${it+20}", style = style)
                //We make sure that our text does not overflow the width, otherwise it will crash
                if (offset < size.width) {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "${it + 20}",
                        style = style,
                        topLeft = Offset(
                            x = offset,
                            y = size.height / 2f - measuredText.size.height / 2
                        )
                    )
                }
                //Here we update selectedValue state
                if (isSelected) selectedValue = it + 20
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WeightPickerPrev() {
    WeightPicker()
}