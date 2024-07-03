package com.example.composeplaygrounds

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefresh(modifier: Modifier = Modifier) {
    val state = rememberPullToRefreshState()
    Box (
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .nestedScroll(state.nestedScrollConnection)
    ) {

        LazyColumn (modifier = Modifier.fillMaxWidth()) {
            items(100) {
                Text(text = "Hi")
            }
        }
        PullToRefreshContainer(modifier = Modifier, state = state)
    }
}

@Composable
@Preview(showBackground = true)
fun PullToRefreshPrev(modifier: Modifier = Modifier) {
    Column (modifier = Modifier.fillMaxSize()) {
        Text(text = "This is title")
        Text(text = "This is title")
        Text(text = "This is title")
        Text(text = "This is title")

        PullToRefresh()
    }
}