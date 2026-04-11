package com.example.integradora5d.navigation

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*
import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.ui.screen.*
import com.example.integradora5d.ui.screen.bienes.*
import com.example.integradora5d.ui.screen.historial.HistorialScreen
import com.example.integradora5d.ui.screen.login.*
import com.example.integradora5d.ui.screen.perfil.*
import com.example.integradora5d.ui.screen.reporte.CrearReporte

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val prefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    var rol by remember { mutableStateOf(prefs.getString("rol", "") ?: "") }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    val nuevoRol = prefs.getString("rol", "") ?: ""
                    rol = nuevoRol
                    navController.navigate("bienes") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPassword = { navController.navigate("resetPassword") }
            )
        }

        composable("resetPassword") {
            ResetPasswordScreen(
                onBackToLogin = { navController.popBackStack() },
                onSendReset = { /* Lógica de reset */ }
            )
        }

        composable("bienes") {
            // Pasamos el rol actual para que la pantalla sepa qué cargar
            BienRegistradoScreen(navController = navController, userRole = rol)
        }

        composable("bien_detalle") {
            val bien = navController.previousBackStackEntry?.savedStateHandle?.get<BienRegistrado>("bienSeleccionado")
            if (bien != null) {
                BienDetalleScreen(navController, bien)
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }

        composable("scanqr") { ScanQrScreen(navController) }

        composable("reportinfo") {
            if (rol.uppercase() == "ADMIN") ReportInfoScreen()
            else LaunchedEffect(Unit) { navController.popBackStack() }
        }

        composable("historial") {
            if (rol.uppercase() == "ADMIN") HistorialScreen(navController)
            else LaunchedEffect(Unit) { navController.popBackStack() }
        }

        composable("profile") { ProfileScreen(navController) }

        composable("edit_profile") { EditProfileScreen(navController) }

        composable("crear_reporte") {
            val userId = prefs.getLong("userId", 0L).toInt()
            val rolActual = rol.uppercase()
            if (rolActual == "USER" || rolActual == "USUARIO" || rolActual == "ADMIN") {
                CrearReporte(navController = navController, usuarioId = userId)
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}