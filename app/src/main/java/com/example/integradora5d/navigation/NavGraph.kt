package com.example.integradora5d.navigation

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.ui.screen.*
import com.example.integradora5d.ui.screen.bienes.*
import com.example.integradora5d.ui.screen.historial.HistorialScreen
import com.example.integradora5d.ui.screen.login.*
import com.example.integradora5d.ui.screen.perfil.*
import com.example.integradora5d.ui.screen.reporte.CrearReporte
import com.example.integradora5d.viewmodel.BienRegistradoViewModel

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
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable("bienes") {
                BienRegistradoScreen(navController = navController, userRole = rol)
        }

        // Dentro de tu NavHost...
        composable(
            route = "bien_detalle/{bienId}", // Definimos que la ruta lleva un parámetro
            arguments = listOf(navArgument("bienId") { type = NavType.LongType })
        ) { backStackEntry ->
            // Extraemos el ID de los argumentos de la ruta
            val id = backStackEntry.arguments?.getLong("bienId") ?: 0L

            // Se lo pasamos a la pantalla (que ahora recibe bienId: Long)
            BienDetalleScreen(navController = navController, bienId = id)
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

        composable("edit_profile") {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

            val userId = prefs.getLong("idUsuario", 0L)

            EditProfileScreen(navController = navController, idUsuario = userId)
        }

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