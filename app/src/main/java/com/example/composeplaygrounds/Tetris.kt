package com.example.composeplaygrounds

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.util.fastFilter
import com.example.composeplaygrounds.ui.theme.TetrisBoardPrimary
import com.example.composeplaygrounds.ui.theme.TetrisNodePrimary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.reflect.full.primaryConstructor

sealed class TetrisBlockType(
    val scope: CoroutineScope,
    override val units: List<TetrisUnit>,
    override val occupiedNodes : List<Pair<Int, Int>>,
    override val limit: Pair<Int, Int>,
    onPlaced: (TetrisBlockState) -> Unit
) : TetrisBlockState(scope, units, occupiedNodes, limit, onPlaced) {
    class L(scope: CoroutineScope, occupiedNodes :List<Pair<Int, Int>>, limit: Pair<Int, Int>, onPlaced: (TetrisBlockState) -> Unit) : TetrisBlockType(
        scope = scope,
        units = listOf(
            TetrisUnit(0, 0),
            TetrisUnit(1, 0),
            TetrisUnit(2, 0),
            TetrisUnit(2, 1)
        ),
        occupiedNodes = occupiedNodes,
        onPlaced = onPlaced,
        limit = limit
    )

    class J(scope: CoroutineScope, occupiedNodes :List<Pair<Int, Int>>, limit: Pair<Int, Int>,onPlaced: (TetrisBlockState) -> Unit) : TetrisBlockType(
        scope = scope,
        units = listOf(
            TetrisUnit(0, 0),
            TetrisUnit(1, 0),
            TetrisUnit(2, 0),
            TetrisUnit(0, 1)
        ),
        occupiedNodes = occupiedNodes,
        onPlaced = onPlaced,
        limit = limit
    )

    class I(scope: CoroutineScope,occupiedNodes :List<Pair<Int, Int>>, limit: Pair<Int, Int>,onPlaced: (TetrisBlockState) -> Unit) : TetrisBlockType(
        scope = scope,
        units = listOf(
            TetrisUnit(0, 0),
            TetrisUnit(1, 0),
            TetrisUnit(2, 0),
            TetrisUnit(3, 0)
        ),
        occupiedNodes = occupiedNodes,
        onPlaced = onPlaced,
        limit = limit
    )

    class O(scope: CoroutineScope, occupiedNodes :List<Pair<Int, Int>>,limit: Pair<Int, Int>, onPlaced: (TetrisBlockState) -> Unit) : TetrisBlockType(
        scope = scope,
        units = listOf(
            TetrisUnit(0, 0),
            TetrisUnit(1, 0),
            TetrisUnit(1, 1),
            TetrisUnit(0, 1)
        ),
        occupiedNodes = occupiedNodes,
        onPlaced = onPlaced,
        limit = limit
    )

    class S(scope: CoroutineScope,occupiedNodes :List<Pair<Int, Int>>,limit: Pair<Int, Int>, onPlaced: (TetrisBlockState) -> Unit) : TetrisBlockType(
        scope = scope,
        units = listOf(
            TetrisUnit(1, 0),
            TetrisUnit(2, 0),
            TetrisUnit(0, 1),
            TetrisUnit(1, 1)
        ),
        occupiedNodes = occupiedNodes,
        onPlaced = onPlaced,
        limit = limit
    )

    class T(scope: CoroutineScope, occupiedNodes :List<Pair<Int, Int>>,limit: Pair<Int, Int>, onPlaced: (TetrisBlockState) -> Unit) : TetrisBlockType(
        scope = scope,
        units = listOf(
            TetrisUnit(0, 0),
            TetrisUnit(1, 0),
            TetrisUnit(2, 0),
            TetrisUnit(1, 1)
        ),
        occupiedNodes = occupiedNodes,
        onPlaced = onPlaced,
        limit = limit
    )

    class Z(scope: CoroutineScope, occupiedNodes :List<Pair<Int, Int>>, limit: Pair<Int, Int>,onPlaced: (TetrisBlockState) -> Unit) : TetrisBlockType(
        scope = scope,
        units = listOf(
            TetrisUnit(0, 0),
            TetrisUnit(1, 0),
            TetrisUnit(1, 1),
            TetrisUnit(2, 1)
        ),
        occupiedNodes = occupiedNodes,
        onPlaced = onPlaced,
        limit = limit
    )
}

data class TetrisUnit(
    val initialX: Int,
    val initialY: Int,
) {
    var isVisible by mutableStateOf(true)
    var x by mutableIntStateOf(initialX)
    var y by mutableIntStateOf(initialY)
}

open class TetrisBlockState(
    scope: CoroutineScope,
    open val units: List<TetrisUnit>,
    open val occupiedNodes : List<Pair<Int, Int>>,
    open val limit : Pair<Int, Int>,
    onPlaced : (TetrisBlockState) -> Unit
) {
    //inside one block we have multiple tetris units combined
    init {
        scope.launch {
            while (true) {
                delay(400)
                Log.e("limits","Limit: $limit")
                val maxValue = units.maxBy { it.y }
                val maxValues = units.filter { it.y == maxValue.y }
                if (maxValue.y == limit.second - 1 || maxValues.any { occupiedNodes.contains(Pair(it.x, it.y + 1))}) {
                    onPlaced(this@TetrisBlockState)
                    this.cancel("Reached bounds")
                }
                ensureActive()
                units.forEach { unit->
                    unit.y += 1
                }
            }
        }
    }

    fun rotate() {
        val pivot = units[1]
        units.forEach { unit->
            val oldX = unit.x
            unit.x = (unit.y + pivot.x - pivot.y)
            unit.y = (pivot.x + pivot.y - oldX)

            Log.d("Rotation", "x: ${unit.x}, y: ${unit.y}")
        }
    }
    fun moveLeft() {
        val minValue = units.minBy { it.x }
        val minValues = units.filter { it.x == minValue.x }
        if (minValue.x == 0 || minValues.any { occupiedNodes.contains(Pair(it.x - 1, it.y)) }) return
        units.forEach {
            it.x--
        }
    }
    fun moveRight() {
        val maxValue = units.maxBy { it.x }
        val maxValues = units.filter { it.x == maxValue.x }
        if (maxValue.x  == limit.first - 1 || maxValues.any { occupiedNodes.contains(Pair(it.x + 1, it.y)) }) return
        units.forEach {
            it.x++
        }
    }
}

@Composable
@Preview(showBackground = true)
fun Tetris() {
    val scope = rememberCoroutineScope()
    val occupiedNodes = remember {
        mutableStateListOf<Pair<Int, Int>>()
    }
    var colCount by remember {
        mutableIntStateOf(0)
    }

    var rowCount by remember {
        mutableIntStateOf(0)
    }

    var activeTetrisBlock by remember {
        mutableStateOf<TetrisBlockState?>(null)
    }

    var nextTetrisBlock by remember {
        mutableStateOf<TetrisBlockState?>(null)
    }

    val blocks = remember {
        mutableStateListOf<TetrisBlockState>()
    }

    fun getRandomChild(onPlaced: (TetrisBlockState) -> Unit) : TetrisBlockType {
        val onPlacedProperty: (TetrisBlockState) -> Unit = onPlaced
        return TetrisBlockType::class.nestedClasses.random().primaryConstructor?.call(scope, occupiedNodes, Pair(colCount, rowCount), onPlacedProperty) as TetrisBlockType
    }

    LaunchedEffect(key1 = activeTetrisBlock) {
        //there is no active tetris
        if (activeTetrisBlock == null) {
            val randomBlock = getRandomChild {
                //on tetris placed
                activeTetrisBlock = null
                it.units.forEach { unit->
                    occupiedNodes.add(Pair(unit.x, unit.y))
                }
                val groupedNodes = occupiedNodes.groupBy { it.second }
                groupedNodes.keys.forEach { key->
                    Log.e("Grouped nodes", "key: $key, elements: ${groupedNodes[key]}")
                    Log.e("Grouped nodes", "key: $key, size: ${groupedNodes[key]?.size}")
                    if (groupedNodes[key]?.size == 9) {
                        Log.e("Grouped nodes", "Path full, remove!")
                        blocks.forEach { block->
                            block.units.forEach { unit->
                                if (unit.y == key) {
                                    unit.isVisible = false
                                }
                            }
                        }
                    }
                }
            }
            blocks.add(randomBlock)
            activeTetrisBlock = randomBlock
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = TetrisBoardPrimary)
    ) {
        TetrisBoard(
            tetrisBlocks = blocks
        ) { col, row ->
            colCount = col
            rowCount = row
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { activeTetrisBlock?.moveLeft() }) {

                Text(text = "<")
            }

            Button(onClick = { activeTetrisBlock?.rotate() }) {
                Text(text = "Rotate")
            }

            Button(onClick = { activeTetrisBlock?.moveRight() }) {
                Text(text = ">")
            }
        }
    }
}

@Composable
fun TetrisBoard(
    tetrisBlocks: List<TetrisBlockState> = emptyList(),
    onCalculated : (colCount : Int, rowCount: Int) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.7f)
        .padding(16.dp)
        .border(2.dp, Color.Black)
        .drawWithCache {
            val edge = 24.dp.toPx()
            val padding = 16.dp.toPx()
            val colCount = (size.width / (edge + padding)).toInt()
            val rowCount = (size.height / (edge + padding)).toInt()
            onCalculated(colCount, rowCount)
            val verticalSpace = size.height - (rowCount * (edge + padding) - padding)
            val horizontalSpace = size.width - (colCount * (edge + padding) - padding)
            val borderPadding = 10.dp.toPx()
            onDrawBehind {
                translate(top = verticalSpace / 2, left = horizontalSpace / 2) {
                    for (column in 0 until colCount) {
                        for (row in 0 until rowCount) {
                            drawRect(
                                color = TetrisNodePrimary,
                                topLeft = Offset(
                                    x = (edge + padding) * column - borderPadding / 2,
                                    y = (edge + padding) * row - borderPadding / 2
                                ),
                                size = Size(
                                    width = edge + borderPadding,
                                    height = edge + borderPadding
                                ),
                                style = Stroke(width = 2.dp.toPx())
                            )
                            drawRect(
                                color = TetrisNodePrimary,
                                topLeft = Offset(
                                    x = (edge + padding) * column,
                                    y = (edge + padding) * row
                                ),
                                size = Size(width = edge, height = edge)
                            )
                        }
                    }
                }
            }
        }
        .drawWithCache {
            val edge = 24.dp.toPx()
            val padding = 16.dp.toPx()
            val colCount = (size.width / (edge + padding)).toInt()
            val rowCount = (size.height / (edge + padding)).toInt()
            val verticalSpace = size.height - (rowCount * (edge + padding) - padding)
            val horizontalSpace = size.width - (colCount * (edge + padding) - padding)
            val borderPadding = 10.dp.toPx()

            onDrawBehind {
                translate(top = verticalSpace / 2, left = horizontalSpace / 2) {
                    tetrisBlocks.forEach { block ->
                        block.units.forEach { unit ->

                            drawRect(
                                color = if (unit.isVisible) Color.Black else Color.Green,
                                topLeft = Offset(
                                    x = (edge + padding) * unit.x,
                                    y = (edge + padding) * unit.y.coerceAtLeast(0)
                                ),
                                size = Size(width = edge, height = edge)
                            )
                        }
                    }
                }

            }
        }
    )
}