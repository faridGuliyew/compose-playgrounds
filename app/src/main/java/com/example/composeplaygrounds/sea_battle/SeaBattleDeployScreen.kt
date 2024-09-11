package com.example.composeplaygrounds.sea_battle

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.composeplaygrounds.R
import kotlin.math.round
import kotlin.math.roundToInt

enum class Team (val colorPrimary: Color, val colorSecondary: Color, @DrawableRes val cannonRes: Int) {
    BLUE(
        Color(0xff292bda),
        Color(0xff0067ff),
        R.drawable.cannon_blue
    ),
    RED(
        Color(0xFFE91E63),
        Color(0xffe64f83),
        R.drawable.cannon_red
        );
}

@Composable
fun SeaBattleDeployScreen(
    team: Team,
    onNext: (SeaBattleDeployScreenState) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Deploy your ships",
            color = Color.White,
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 24.dp),
            text = "Drag to move and tap to rotate or try random placement",
            color = Color.White,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(48.dp))
        PlacementContent(team = team, onDoneClicked = onNext)
    }
}

@Composable
fun rememberSeaBattleDeployScreenState(team: Team): SeaBattleDeployScreenState {
    val density = LocalDensity.current
    return remember {
        SeaBattleDeployScreenState(
            density = density,
            team = team
        )
    }
}

@Immutable
class SeaBattleDeployScreenState(
    val placementContentTopPadding: Dp = 64.dp,
    val placementRowCount: Int = 7,
    val placementColumnCount: Int = 9,
    private val density: Density,
    val cellSpacing : Float = with(density) { 4.dp.toPx() },
    val team: Team
) {

    val ships = listOf(
        BattleShipState(this,2),
        BattleShipState(this, 3),
        BattleShipState(this, 3),
        BattleShipState(this, 4),
        BattleShipState(this, 5)
    )
    var shipsFlowRowHeight by mutableIntStateOf(0)
    var shipsFlowRowPositionInRoot by mutableStateOf(Offset.Zero)
    var cellHeight by mutableStateOf(0.dp)

    var currentlySelectedCellsOnDrag by mutableStateOf<List<IntOffset>>(emptyList())

    val placementContentPositionInRoot
        get() = shipsFlowRowPositionInRoot.copy(
            y = shipsFlowRowPositionInRoot.y + shipsFlowRowHeight + with(
                density
            ) { placementContentTopPadding.toPx() })

    val _deployedShips = mutableStateListOf<BattleShipState>()
    val deployedShips get() = _deployedShips
    val isAllShipsDeployed get() = _deployedShips.size == ships.size

    fun addDeployedShip(ship: BattleShipState) {
        _deployedShips.remove(ship)
        _deployedShips.add(ship)
    }
    fun toPx(value: Dp) = with(density) {value.toPx()}
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColumnScope.PlacementContent(team : Team, onDoneClicked: (SeaBattleDeployScreenState) -> Unit) {
    val screenState = rememberSeaBattleDeployScreenState(team).also {
        println("here bitch!!!!")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = team.colorPrimary,
                RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(12.dp)
    ) {

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(10f)
                .onGloballyPositioned {
                    screenState.shipsFlowRowPositionInRoot = it.positionInRoot()
                    screenState.shipsFlowRowHeight = it.size.height
                },
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            screenState.ships.forEach {
                Ship(shipState = it, cellHeight = screenState.cellHeight, boatColor = team.colorSecondary)
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val cellWidth =
                        ((size.width - ((screenState.placementColumnCount - 1) * screenState.cellSpacing)) / screenState.placementColumnCount)
                            .also {
                                screenState.cellHeight = it.toDp()
                            }

                    onDrawBehind {
                        translate(
                            top = screenState.shipsFlowRowHeight.toFloat() + screenState.placementContentTopPadding.toPx() / 2
                        ) {
                            drawLine(
                                color = Color.White,
                                start = Offset.Zero,
                                end = Offset(size.width, 0f)
                            )
                        }

                        translate(top = screenState.shipsFlowRowHeight.toFloat() + screenState.placementContentTopPadding.toPx()) {
                            for (y in 0 until screenState.placementRowCount) {
                                for (x in 0 until screenState.placementColumnCount) {
                                    drawRoundRect(
                                        color = if (screenState.deployedShips.any {
                                                it.deployedCoordinates.contains(
                                                    IntOffset(x, y)
                                                )
                                            }) Color.Red else if (IntOffset(
                                                x,
                                                y
                                            ) in screenState.currentlySelectedCellsOnDrag
                                        ) Color.Green else Color.White,
                                        topLeft = Offset(
                                            x = x * (cellWidth + screenState.cellSpacing),
                                            y = y * (cellWidth + screenState.cellSpacing)
                                        ),
                                        size = Size(cellWidth, cellWidth),
                                        cornerRadius = CornerRadius(12f, 12f)
                                    )
                                }
                            }
                        }
                    }
                }
        ) {
                if (screenState.isAllShipsDeployed) {
                    ActionButton(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        primaryColor = team.colorSecondary,
                        secondaryColor = team.colorSecondary.copy(0.5f),
                        text = "Done"
                    ) {
                        onDoneClicked(screenState)
                    }
                }
//            }
        }
    }
}

class BattleShipState(
    val screenState: SeaBattleDeployScreenState,
    val cellCount: Int
) {
    var positionInRoot by mutableStateOf(Offset.Zero)
    //initially it is the starting position, but when placed somewhere, it will be inside placement content
    private var idleTranslation = Offset.Zero

    var translationX by mutableFloatStateOf(0f)
    var translationY by mutableFloatStateOf(0f)
    val isDeployed get() = deployedCoordinates.isNotEmpty()
    var deployedCoordinates by mutableStateOf(emptyList<IntOffset>())

    private var latestValidSelectedCoordinate : IntOffset? = null


    fun onDrag(change: PointerInputChange, dragAmount: Offset) {
        translationX += dragAmount.x
        translationY += dragAmount.y

        //check new position in root to identify which blocks need to be painted green
        if (positionInRoot.y > screenState.placementContentPositionInRoot.y) {
            //calculate depth to find corresponding squares
            val verticalDepth = positionInRoot.y - screenState.placementContentPositionInRoot.y
            val horizontalDepth = positionInRoot.x - screenState.placementContentPositionInRoot.x
            //based on cell side in pixels
            val cellSide = screenState.toPx(screenState.cellHeight)

            //round to find the closest cell that is selected
            val xCoordinateOfSelectedCell = round(horizontalDepth / (cellSide + screenState.cellSpacing)).roundToInt()
            val yCoordinateOfSelectedCell = round(verticalDepth / (cellSide + screenState.cellSpacing)).roundToInt()
            //case to prevent getting out of the placement area
            val currentlySelectedCellsOnDrag = selectedStartCoordinateToAllCoordinates(
                IntOffset(xCoordinateOfSelectedCell,yCoordinateOfSelectedCell)
            )
            if (xCoordinateOfSelectedCell + cellCount - 1 >= screenState.placementColumnCount
                || yCoordinateOfSelectedCell >= screenState.placementRowCount
                || xCoordinateOfSelectedCell !in (0 until screenState.placementColumnCount)
                || yCoordinateOfSelectedCell !in (0 until screenState.placementRowCount)
                || screenState.deployedShips.any { deployedShip-> currentlySelectedCellsOnDrag.any { coords-> coords in  deployedShip.deployedCoordinates } }
                ) return
            //calculate other selected ones based on the cell count
            screenState.currentlySelectedCellsOnDrag = currentlySelectedCellsOnDrag
            latestValidSelectedCoordinate = IntOffset(xCoordinateOfSelectedCell, yCoordinateOfSelectedCell)
            println("INSIDE PLACEMENT CONTENT. x: $xCoordinateOfSelectedCell, y: $yCoordinateOfSelectedCell")
        }
    }

    fun onDragEnd() {
        if (latestValidSelectedCoordinate == null) deployedCoordinates = emptyList()
        else {
            deployedCoordinates = selectedStartCoordinateToAllCoordinates(latestValidSelectedCoordinate!!)
            screenState.addDeployedShip(this)
            //calculate new accurate position
            val cellSide = screenState.toPx(screenState.cellHeight)
            val x = positionInRoot.x - screenState.placementContentPositionInRoot.x
            //magic formula to calculate x
            val xPositionInRootBasedOnCoordinate = screenState.placementContentPositionInRoot.x + latestValidSelectedCoordinate!!.x * cellSide + screenState.cellSpacing * latestValidSelectedCoordinate!!.x - 1
            //magic formula to calculate y
            val yPositionInRootBasedOnCoordinate = screenState.placementContentPositionInRoot.y + latestValidSelectedCoordinate!!.y * cellSide + screenState.cellSpacing * latestValidSelectedCoordinate!!.y - 1

            //change in translation x
            val xTranslationChange = positionInRoot.x - xPositionInRootBasedOnCoordinate
            //change in translation y
            val yTranslationChange = positionInRoot.y - yPositionInRootBasedOnCoordinate
            //final translation to move to
            idleTranslation = Offset(
                x = translationX - xTranslationChange,
                y = translationY - yTranslationChange
            )
            println(
                "CELL => cell side: $cellSide," +
                        " placement content position in root: ${screenState.placementContentPositionInRoot}, " +
                        "currentPositionInRoot: ${positionInRoot}," +
                        " x: ${latestValidSelectedCoordinate?.x}, y: ${latestValidSelectedCoordinate?.y}" +
                        "intended coordinate x: $xPositionInRootBasedOnCoordinate, intended coordinate y: $yPositionInRootBasedOnCoordinate" +
                        "xTranslationChange: $xTranslationChange" +
                        "yTranslation Change: $yTranslationChange"
            )
        }
        translationX = idleTranslation.x
        translationY = idleTranslation.y
    }

    private fun selectedStartCoordinateToAllCoordinates(coordinate: IntOffset) : List<IntOffset> {
        return (0 until cellCount).map { index ->
            IntOffset(coordinate.x + index, coordinate.y)
        }
    }
}

@Composable
fun Ship(shipState: BattleShipState, cellHeight: Dp, boatColor: Color) {
    Row(
        modifier = Modifier
            .graphicsLayer {
                this.translationX = shipState.translationX
                this.translationY = shipState.translationY
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = shipState::onDragEnd,
                    onDrag = shipState::onDrag
                )
            }
            .onGloballyPositioned {
                shipState.positionInRoot = it.positionInRoot()
            }.border(width = 2.dp, color = Color.Black, RoundedCornerShape(6.dp)),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(shipState.cellCount) {
            Box(
                modifier = Modifier
                    .size(cellHeight)
                    .background(boatColor, RoundedCornerShape(6.dp))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SeaBattleDeployScreenPrev() {
    SeaBattleDeployScreen(Team.BLUE) {}
}