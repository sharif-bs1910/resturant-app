package com.noshitechinc.restaurant.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.noshitechinc.restaurant.feature.checkout.CheckoutRoute
import com.noshitechinc.restaurant.feature.home.HomeRoute
import com.noshitechinc.restaurant.feature.pairing.PairingRoute
import com.noshitechinc.restaurant.feature.role.SelectRoleRoute
import com.noshitechinc.restaurant.feature.signin.SignInRoute
import kotlinx.coroutines.flow.Flow

@Composable
fun AppNavHost(sessionEnded: Flow<Unit>, modifier: Modifier = Modifier, navController: NavHostController = rememberNavController()) {
    LaunchedEffect(sessionEnded, navController) {
        sessionEnded.collect {
            navController.navigate(PairingDestination) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    NavHost(navController = navController, startDestination = PairingDestination, modifier = modifier) {
        composable<PairingDestination> {
            PairingRoute(onPaired = { navController.navigate(SignInDestination) })
        }
        composable<SignInDestination> {
            SignInRoute(
                onSignedIn = { navController.navigate(SelectRoleDestination) },
            )
        }
        composable<SelectRoleDestination> {
            SelectRoleRoute(
                onContinue = {
                    navController.navigate(CheckoutDestination) {
                        popUpTo(PairingDestination) { inclusive = true }
                    }
                },
            )
        }
        composable<CheckoutDestination> { CheckoutRoute() }
        composable<HomeDestination> { HomeRoute() }
    }
}
