package com.example.integradora5d.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.ui.screen.bienes.BienDetalleScreen
import com.example.integradora5d.ui.screen.bienes.BienRegistradoScreen
import com.example.integradora5d.ui.screen.login.LoginScreen
import com.example.integradora5d.ui.screen.login.ResetPasswordScreen

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("bienes") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPassword = {
                    navController.navigate("resetPassword")
                }
            )
        }

        composable("resetPassword") {
            ResetPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onSendReset = { correo ->

                }
            )
        }

        composable("bienes") {
            BienRegistradoScreen(navController)
        }

        composable("bien_detalle") {
            val bien = navController
                .previousBackStackEntry
                ?.savedStateHandle
                ?.get<BienRegistrado>("bienSeleccionado")

            bien?.let {
                BienDetalleScreen(navController, it)
            }
        }
    }
}
