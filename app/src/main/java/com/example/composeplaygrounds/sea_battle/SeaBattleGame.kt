package com.example.composeplaygrounds.sea_battle

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.composeplaygrounds.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SeaBattleGameScreenState (
    startingTeam: SeaBattleDeployScreenState
) {
    var attackingTeam : SeaBattleDeployScreenState by mutableStateOf(startingTeam)
    var blueCannonPositionInRoot by mutableStateOf(Offset.Zero)
    var blueCannonSize by mutableStateOf(IntSize.Zero)
    var redCannonPositionInRoot by mutableStateOf(Offset.Zero)
    var cellWidth by mutableFloatStateOf(0f)

    var attackAreaTopPadding by mutableFloatStateOf(0f)
    var attackingAreaCellsHeight by mutableFloatStateOf(0f)
    var divisionHeight by mutableFloatStateOf(0f)

    var selectedShootingTarget : IntOffset by mutableStateOf(IntOffset(4,4))

    var isBulletFlying by mutableStateOf(false)

    var revealedBlueCoordinates = mutableStateListOf<IntOffset>()
    var shotBlueCoordinates = mutableStateListOf<IntOffset>()
    var revealedRedCoordinates = mutableStateListOf<IntOffset>()
    var shotRedCoordinates = mutableStateListOf<IntOffset>()

    var didInteract by mutableStateOf(false)
}

@Composable
fun SeaBattleGame(
    blueState: SeaBattleDeployScreenState,
    redState: SeaBattleDeployScreenState,
) {
    val gameState = remember {
        //red, because it changes right at the beginning
        SeaBattleGameScreenState(redState)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TeamBattleField(gameState, blueState)
        AnimatedContent(targetState = gameState.didInteract /*gameState.selectedShootingTarget.y < blueState.placementRowCount*/, modifier = Modifier.onSizeChanged {
            gameState.divisionHeight = it.height.toFloat()
        }) { didInteract->
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                if (didInteract.not()) Text(text = "${blueState.ships.sumOf { it.cellCount } - gameState.shotBlueCoordinates.size} VS ${redState.ships.sumOf { it.cellCount } - gameState.shotRedCoordinates.size}", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                else ActionButton(text = "Shoot!", primaryColor = gameState.attackingTeam.team.colorPrimary, secondaryColor = gameState.attackingTeam.team.colorSecondary) {
                    gameState.isBulletFlying = true
                    gameState.didInteract = false
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        TeamBattleField(gameState, redState)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .zIndex(10f)) {
        val density = LocalDensity.current
        val translationY = remember {
            //Animatable((gameState.blueCannonPositionInRoot.y + gameState.blueCannonSize.height + 100))
            Animatable(0f)
        }
        val translationX = remember {
            Animatable((gameState.blueCannonPositionInRoot.x - gameState.blueCannonSize.width / 8f))
        }
        val scale = remember { Animatable(0f) }

        LaunchedEffect(key1 = gameState.selectedShootingTarget) {
            if (gameState.isBulletFlying) return@LaunchedEffect
            val startingXPosition = if (gameState.attackingTeam.team == Team.BLUE)  (gameState.blueCannonPositionInRoot.x - gameState.blueCannonSize.width / 8f) - (blueState.placementColumnCount/ 2 - gameState.selectedShootingTarget.x) * 40f
            else (gameState.redCannonPositionInRoot.x - gameState.blueCannonSize.width / 8f) - (blueState.placementColumnCount/ 2 - gameState.selectedShootingTarget.x) * 40f
            translationX.snapTo(startingXPosition)
        }

        LaunchedEffect(key1 = gameState.isBulletFlying) {
            if (gameState.isBulletFlying.not()) {

                val startingXPosition = if (gameState.attackingTeam.team == Team.BLUE)  (gameState.blueCannonPositionInRoot.x - gameState.blueCannonSize.width / 8f) - (blueState.placementColumnCount/ 2 - gameState.selectedShootingTarget.x) * 40f
                else (gameState.redCannonPositionInRoot.x - gameState.blueCannonSize.width / 8f) - (blueState.placementColumnCount/ 2 - gameState.selectedShootingTarget.x) * 40f

                val startingYPosition = if (gameState.attackingTeam.team == Team.BLUE) (gameState.blueCannonPositionInRoot.y + gameState.blueCannonSize.height) + 100f
                else gameState.redCannonPositionInRoot.y - gameState.blueCannonSize.height - 100f

                translationX.snapTo(startingXPosition)
                translationY.snapTo(startingYPosition)

                return@LaunchedEffect
            }
            launch {
                delay(100)
                scale.animateTo(1f, tween(1000))
                scale.animateTo(0f, tween(900))
            }
            launch {
                val xTranslation = (gameState.selectedShootingTarget.x) * (gameState.cellWidth + blueState.cellSpacing) - with(density) {12.dp.toPx()}
                translationX.animateTo(xTranslation, tween(2000))
            }
            launch {
                val yTranslation = if (gameState.attackingTeam.team == Team.BLUE) gameState.attackingAreaCellsHeight + gameState.attackAreaTopPadding * 2f + gameState.divisionHeight + ((gameState.selectedShootingTarget.y + 1) * (gameState.cellWidth + blueState.cellSpacing))
                else gameState.attackAreaTopPadding + ((gameState.selectedShootingTarget.y ) * (gameState.cellWidth + blueState.cellSpacing))

                translationY.animateTo(yTranslation, tween(2000))
                gameState.isBulletFlying = false
                var isMissed = true
                if (gameState.attackingTeam.team == Team.RED) {
                    gameState.revealedBlueCoordinates.add(gameState.selectedShootingTarget)
                    if (blueState.deployedShips.any { gameState.selectedShootingTarget in it.deployedCoordinates }) gameState.shotBlueCoordinates.add(gameState.selectedShootingTarget).also { isMissed = false }
                }
                else {
                    gameState.revealedRedCoordinates.add(gameState.selectedShootingTarget)
                    if (redState.deployedShips.any { gameState.selectedShootingTarget in it.deployedCoordinates }) gameState.shotRedCoordinates.add(gameState.selectedShootingTarget).also { isMissed = false }
                }

                if (isMissed)
                gameState.attackingTeam = if (gameState.attackingTeam.team == Team.RED) blueState else redState
            }
        }
        Bullet(
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer {
                    this.translationX = translationX.value
                    this.translationY = translationY.value
                    this.scaleX = scale.value
                    this.scaleY = scale.value
                }
        )
    }
}


@Composable
fun ColumnScope.TeamBattleField(gameState: SeaBattleGameScreenState ,state: SeaBattleDeployScreenState) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .background(state.team.colorPrimary)
        .drawWithCache {
            gameState.cellWidth =
                ((size.width - ((state.placementColumnCount - 1) * state.cellSpacing)) / state.placementColumnCount)
            gameState.attackingAreaCellsHeight =
                gameState.cellWidth * state.placementRowCount + state.cellSpacing * (state.placementRowCount - 1)
            gameState.attackAreaTopPadding = (size.height - gameState.attackingAreaCellsHeight) / 2
            onDrawBehind {
                translate(top = gameState.attackAreaTopPadding) {
                    for (y in 0 until state.placementRowCount) {
                        for (x in 0 until state.placementColumnCount) {
                            val currentCoordinate = IntOffset(x, y)
                            drawRoundRect(
                                color = when (state.team) {
                                    Team.BLUE -> {
                                        if (currentCoordinate in gameState.shotBlueCoordinates) Color.Black
                                        else if (currentCoordinate in gameState.revealedBlueCoordinates) Color.White
                                        else state.team.colorSecondary
                                    }
                                    Team.RED -> {
                                        if (currentCoordinate in gameState.shotRedCoordinates) Color.Black
                                        else if (currentCoordinate in gameState.revealedRedCoordinates) Color.White
                                        else state.team.colorSecondary
                                    }
                                },
                                topLeft = Offset(
                                    x = x * (gameState.cellWidth + state.cellSpacing),
                                    y = y * (gameState.cellWidth + state.cellSpacing)
                                ),
                                size = Size(gameState.cellWidth, gameState.cellWidth),
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                            if (currentCoordinate == gameState.selectedShootingTarget && state.team != gameState.attackingTeam.team) {
                                drawCircle(
                                    color = Color.White,
                                    style = Stroke(1.dp.toPx()),
                                    radius = gameState.cellWidth / 2f,
                                    center = Offset(
                                        x = x * (gameState.cellWidth + state.cellSpacing) + gameState.cellWidth / 2f,
                                        y = y * (gameState.cellWidth + state.cellSpacing) + gameState.cellWidth / 2f
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        .pointerInput(Unit) {
            detectTapGestures { tapOffset ->
                gameState.didInteract = true
                val x = (tapOffset.x / (gameState.cellWidth + state.cellSpacing)).toInt()
                val y =
                    ((tapOffset.y - gameState.attackAreaTopPadding) / (gameState.cellWidth + state.cellSpacing)).toInt()
                println("Tapped: $tapOffset. x: $x, y: $y")
                gameState.selectedShootingTarget = IntOffset(x, y)
            }
        },
        contentAlignment = Alignment.Center
    ) {
        Cannon(gameState, state)
    }

}

@Composable
fun Cannon(gameState: SeaBattleGameScreenState, teamState: SeaBattleDeployScreenState) {
    if (gameState.attackingTeam.also { println("attacking team: ${it.team}") } != teamState.also { println("current team: ${it.team}") }) return

    var bulletReleaseTrigger by remember {
        mutableStateOf(false)
    }

    val bulletTriggerDependentAnimation = updateTransition(targetState = bulletReleaseTrigger, label = "shooting_anim")
    val bulletShotCannonScaleAnimation by bulletTriggerDependentAnimation.animateFloat(label = "scale") {
        if (it) 3f else 1.5f
    }

    LaunchedEffect(key1 = gameState.isBulletFlying) {
        if (gameState.isBulletFlying.not()) return@LaunchedEffect
        bulletReleaseTrigger = true
        delay(100)
        bulletReleaseTrigger = false
    }
    val rotation by animateFloatAsState(targetValue =
    (if (teamState.team == Team.BLUE) 180f else 0f) + ((teamState.placementColumnCount / 2) - (gameState.selectedShootingTarget.x)) * 10f
            - (if (teamState.team == Team.BLUE) gameState.selectedShootingTarget.y else teamState.placementColumnCount - 1 - gameState.selectedShootingTarget.y) * ((teamState.placementColumnCount / 2) - (gameState.selectedShootingTarget.x))) // * if (teamState.team == Team.BLUE) -1f else 1f)
    Image(
        modifier = Modifier
            .onGloballyPositioned {
                gameState.blueCannonSize = it.size
                if (teamState.team == Team.BLUE) {
                    gameState.blueCannonPositionInRoot = it.positionInRoot()
                } else {
                    gameState.redCannonPositionInRoot = it.positionInRoot()
                }
            }
            .graphicsLayer {
                rotationZ = rotation * if (teamState.team == Team.BLUE) 1f else -1f
                scaleX = bulletShotCannonScaleAnimation
                scaleY = bulletShotCannonScaleAnimation
            },
        painter = painterResource(id = teamState.team.cannonRes),
        contentDescription = "cannon"
    )
}

@Composable
fun Bullet(modifier: Modifier) {
    val rotation by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Restart)
    )
    Image(
        modifier = modifier.graphicsLayer {
            rotationZ = rotation
        },
        painter = painterResource(id = R.drawable.bullet),
        contentDescription = "bullet"
    )
}


@Preview(showBackground = true)
@Composable
private fun SeaBattleGamePrev() {
    SeaBattleGame(
        blueState = rememberSeaBattleDeployScreenState(Team.BLUE),
        redState = rememberSeaBattleDeployScreenState(Team.RED)
    )
}