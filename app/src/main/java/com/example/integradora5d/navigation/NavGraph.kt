package com.example.integradora5d.navigation

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*

import com.example.integradora5d.data.model.BienRegistrado

import com.example.integradora5d.ui.screen.ReportInfoScreen
import com.example.integradora5d.ui.screen.ScanQrScreen
import com.example.integradora5d.ui.screen.bienes.BienDetalleScreen
import com.example.integradora5d.ui.screen.bienes.BienRegistradoScreen
import com.example.integradora5d.ui.screen.historial.HistorialScreen
import com.example.integradora5d.ui.screen.login.LoginScreen
import com.example.integradora5d.ui.screen.login.ResetPasswordScreen
import com.example.integradora5d.ui.screen.perfil.EditProfileScreen
import com.example.integradora5d.ui.screen.perfil.ProfileScreen
import com.example.integradora5d.ui.screen.reporte.CrearReporte

@Composable
fun NavGraph() {

    val navController = rememberNavController()

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // estado reactivo del rol
    var rol by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {

                    //  actualizar rol DESPUÉS del login
                    rol = prefs.getString("rol", "") ?: ""

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
                onBackToLogin = {
                    navController.popBackStack()
                },
                onSendReset = {}
            )
        }

        //  TODOS
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

        composable("scanqr") {
            ScanQrScreen(navController)
        }

        // 🔥 SOLO ADMIN
        composable("reportinfo") {
            if (rol == "ADMIN") {
                ReportInfoScreen()
            } else {
                navController.popBackStack()
            }
        }

        composable("historial") {
            if (rol == "ADMIN") {
                HistorialScreen(navController)
            } else {
                navController.popBackStack()
            }
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable("edit_profile") {
            EditProfileScreen(navController)
        }

        // SOLO USUARIO
        composable("crear_reporte") {
            if (rol == "USER") {
                CrearReporte(navController)
            } else {
                navController.popBackStack()
            }
        }

    }
}