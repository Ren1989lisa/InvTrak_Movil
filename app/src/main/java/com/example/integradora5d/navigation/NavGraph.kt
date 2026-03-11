package com.example.integradora5d.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.integradora5d.ui.screen.bienes.BienRegistradoScreen
import com.example.integradora5d.ui.screen.home.HomeScreen
import com.example.integradora5d.ui.screen.login.LoginScreen

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {

            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Bienes.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Bienes.route) {
            BienRegistradoScreen()
        }

    }
}