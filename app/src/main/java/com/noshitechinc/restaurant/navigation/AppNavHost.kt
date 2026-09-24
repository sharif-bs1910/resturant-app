package com.noshitechinc.restaurant.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noshitechinc.restaurant.feature.home.HomeRoute
import kotlinx.coroutines.flow.Flow

@Composable
fun AppNavHost(sessionEnded: Flow<Unit>, modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    LaunchedEffect(sessionEnded, navController) {
        sessionEnded.collect {
            navController.navigate(HomeDestination) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    NavHost(navController = navController, startDestination = HomeDestination, modifier = modifier) {
        composable<HomeDestination> { HomeRoute() }
    }
}
