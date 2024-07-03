package com.example.composeplaygrounds.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.composeplaygrounds.toPx
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
@Preview (showBackground = true)
fun ScrollableLazyRow(modifier: Modifier = Modifier) {
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var totalHeight by remember {
        mutableIntStateOf(0)
    }
    var offset by remember {
        mutableIntStateOf(0)
    }
    val progress by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex + 1 / 100
        }
    }
    val density = LocalDensity.current
    LaunchedEffect(key1 = Unit) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .collectLatest {
                offset = progress * totalHeight + it
            }
    }
    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(24.dp)
                .onSizeChanged {
                    totalHeight = it.height
                },
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(100) {
                Card (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(32.dp),
                        text = "Item $it"
                    )
                }
            }
        }
    }
    Canvas(modifier = Modifier
        .padding(start = 4.dp, top = 24.dp)
        .height(80.dp)
        .width(12.dp)
        .offset {
            IntOffset(y = offset, x = 0)
        }
        .clip(CircleShape)
        .background(Color.Black)
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress { change, dragAmount ->
                scope.launch {
                    lazyListState.scrollBy(dragAmount.y)

                }
            }
        }
    ) {

    }
}