package com.example.composeplaygrounds.sea_battle

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeplaygrounds.R

@Composable
fun SeaBattleOnboardingScreen(
    onPlay: () -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(bottom = 32.dp),
        bottomBar = {
            Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                ActionButton(text = "Back") {}
            }
        }
    ) {
        Box(
            modifier = Modifier
                .padding(it)
        ) {
            SeaBattleHeader()
            SeaBattleStartContent(onPlay = onPlay)
        }
    }
}

@Composable
fun BoxScope.SeaBattleStartContent(
    onPlay: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(horizontal = 12.dp)
            .graphicsLayer {
                translationY = size.height * 0.37f
            }, colors = CardDefaults.elevatedCardColors(
            containerColor = Color.White
        ), elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column {
                Text(
                    text = "Place your ships without showing your opponent and take turns guessing your opponent's ship location".replace(
                        " ",
                        "\t\t"
                    ),
                    style = TextStyle(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                )
                Spacer(modifier = Modifier.fillMaxHeight(0.08f))
                HowToPlayButton()
            }

            Column {
                PlayButton("FRIEND", R.drawable.img_friend, onClick = onPlay)
                Spacer(modifier = Modifier.height(8.dp))
                PlayButton("BOT", R.drawable.img_robot, onClick = onPlay)
            }
        }
    }
}

@Composable
fun ColumnScope.HowToPlayButton(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier.align(
            Alignment.End
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "How to play",
            color = Color(0xFF00B600),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Icon(
            modifier = Modifier
                .background(Color(0xFF00B600), CircleShape)
                .padding(2.dp),
            imageVector = Icons.Default.PlayArrow,
            tint = Color.White,
            contentDescription = "play"
        )
    }
}

@Composable
fun ColumnScope.PlayButton(versusText: String, @DrawableRes icon: Int, onClick: () -> Unit) {
    var scale by remember {
        mutableFloatStateOf(1f)
    }
    val scaleTransition = updateTransition(targetState = scale)
    val animatedScale by scaleTransition.animateFloat(label = "scale") {
        it
    }
    val animatedRotation by scaleTransition.animateFloat(
        label = "scale",
        transitionSpec = {
            spring(dampingRatio = Spring.DampingRatioHighBouncy)
        }
    ) {
        if (it == 1f) 0f else -20f
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null) {
                onClick()
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown()
                        scale = 1.2f
                        waitForUpOrCancellation()
                        scale = 1f
                    }
                }
            }
            .background(Color(0x1a0067ff), RoundedCornerShape(16.dp))
            .padding(bottom = 6.dp)
            .background(Color(0xff032760), RoundedCornerShape(16.dp))
            .padding(bottom = 6.dp)
            .background(Color(0xff0354cd), RoundedCornerShape(16.dp))
            .align(Alignment.CenterHorizontally)
            .padding(16.dp)
    ) {
        Image(
            modifier = Modifier
                .height(64.dp)
                .width(60.dp)
                .rotate(animatedRotation),
            colorFilter = ColorFilter.tint(Color.White),
            painter = painterResource(id = icon),
            contentDescription = "friend",
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "PLAY VS.",
                color = Color(0xff07d9ba),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = versusText,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 40.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String,
    primaryColor: Color = Color(0xffb6510a),
    secondaryColor: Color = Color(0xffcd7537),
    onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(secondaryColor, RoundedCornerShape(16.dp))
            .clickable(interactionSource = remember {
                MutableInteractionSource()
            }, indication = null) {
                onClick()
            }
            .padding(bottom = 6.dp)
            .background(primaryColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = text,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun SeaBattleHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.33f)
            .background(Color.Blue),
        contentAlignment = Alignment.Center,
    ) {
        SeaBattleBgImage()
        SeaBattleBgTitle()
        Icon(
            modifier = Modifier
                .padding(end = 12.dp)
                .align { size, space, layoutDirection ->
                    IntOffset(
                        x = space.width - size.width,
                        y = size.height
                    )
                }
                .background(Color.White, CircleShape)
                .padding(6.dp),
            imageVector = Icons.Default.Star,
            tint = Color.LightGray,
            contentDescription = "star"
        )
    }
}

@Composable
fun SeaBattleBgImage() {
    Box {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.75f),
            painter = painterResource(id = R.drawable.sea_battle_bg),
            contentScale = ContentScale.FillWidth,
            contentDescription = "bg"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(0.1f),
                            Color.Black.copy(0.5f)
                        )
                    )
                )
        )
    }
}

@Composable
fun SeaBattleBgTitle() {
    Text(
        text = "SEA\nBATTLE",
        style = TextStyle(
            color = Color.Black,
            fontSize = 32.sp,
            drawStyle = Stroke(
                width = 15f
            ),
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            letterSpacing = 2.sp
        ),
    )
    Text(
        text = "SEA\nBATTLE",
        style = TextStyle(
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            letterSpacing = 3.sp
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun SeaBattlePrev() {
    SeaBattleOnboardingScreen {

    }
}