package com.golfperformance.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.golfperformance.app.ui.players.PlayerDetailScreen
import com.golfperformance.app.ui.players.PlayerListScreen

@Composable
fun MainNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "players", modifier = modifier.fillMaxSize()) {
        composable("players") {
            PlayerListScreen(onPlayerClick = { playerId ->
                navController.navigate("player/$playerId")
            })
        }
        composable("player/{playerId}", arguments = listOf(navArgument("playerId") { type = NavType.StringType })) { backStack ->
            val playerId = backStack.arguments?.getString("playerId") ?: ""
            PlayerDetailScreen(playerId = playerId)
        }
    }
}

