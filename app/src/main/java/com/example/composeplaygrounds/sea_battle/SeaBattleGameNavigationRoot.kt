package com.example.composeplaygrounds.sea_battle

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SeaBattleNavigationRoot() {
    val navController = rememberNavController()
    lateinit var blueTeamScreenState: SeaBattleDeployScreenState
    lateinit var redTeamScreenState: SeaBattleDeployScreenState
    NavHost(navController = navController, startDestination = "play") {
        composable(route = "play") {
            SeaBattleOnboardingScreen {
                navController.navigate("deploy_blue")
            }
        }
        composable(route = "deploy_blue") {
            SeaBattleDeployScreen(Team.BLUE) {
                blueTeamScreenState = it
                navController.navigate("deploy_red")
            }
        }
        composable(route = "deploy_red") {
            SeaBattleDeployScreen(Team.RED) {
                redTeamScreenState = it
                navController.navigate("game")
            }
        }
        composable(route = "game") {
            SeaBattleGame(
                blueState = blueTeamScreenState,
                redState = redTeamScreenState
            )
            CountdownScreen()
        }
    }
}