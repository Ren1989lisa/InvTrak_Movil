package com.example.integradora5d.navigation

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.integradora5d.ui.screen.*
import com.example.integradora5d.ui.screen.bienes.*
import com.example.integradora5d.ui.screen.historial.HistorialScreen
import com.example.integradora5d.ui.screen.login.*
import com.example.integradora5d.ui.screen.mantenimiento.AtenderMantenimientoScreen
import com.example.integradora5d.ui.screen.mantenimiento.MantenimientosScreen
import com.example.integradora5d.ui.screen.perfil.*
import com.example.integradora5d.ui.screen.reporte.CrearReporte
import com.example.integradora5d.ui.screen.resguardo.ConfirmarResguardoScreen
import com.example.integradora5d.ui.screen.resguardo.ResguardosPendientesScreen

@Composable
fun NavGraph(startRoute: String? = null) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    var rol by remember { mutableStateOf(prefs.getString("rol", "") ?: "") }

    // Si viene de notificación y ya tiene sesión activa, navegar directamente
    LaunchedEffect(startRoute) {
        if (startRoute != null && prefs.getString("token", null) != null) {
            navController.navigate(startRoute) {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    val nuevoRol = prefs.getString("rol", "") ?: ""
                    val primerAcceso = prefs.getBoolean("primerAcceso", false)
                    rol = nuevoRol
                    if (primerAcceso) {
                        val destino = when (nuevoRol.uppercase()) {
                            "TECNICO" -> "mantenimientos"
                            else -> "bienes"
                        }
                        navController.navigate("cambiar_contrasena/$destino") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        val destino = when (nuevoRol.uppercase()) {
                            "TECNICO" -> "mantenimientos"
                            else -> "bienes"
                        }
                        navController.navigate(destino) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onForgotPassword = { navController.navigate("resetPassword") }
            )
        }

        composable("resetPassword") {
            ResetPasswordScreen(onBackToLogin = { navController.popBackStack() })
        }

        composable(
            route = "cambiar_contrasena/{destino}",
            arguments = listOf(navArgument("destino") { type = NavType.StringType })
        ) { backStackEntry ->
            val destino = backStackEntry.arguments?.getString("destino") ?: "bienes"
            CambiarContrasenaScreen(navController = navController, destinoTrasExito = destino)
        }

        composable("bienes") {
            BienRegistradoScreen(navController = navController, userRole = rol)
        }

        composable(
            route = "bien_detalle/{bienId}",
            arguments = listOf(navArgument("bienId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("bienId") ?: 0L
            BienDetalleScreen(navController = navController, bienId = id)
        }

        composable("scanqr") { ScanQrScreen(navController) }

        composable(
            route = "historial/{etiqueta}",
            arguments = listOf(navArgument("etiqueta") { type = NavType.StringType })
        ) { backStackEntry ->
            val etiqueta = backStackEntry.arguments?.getString("etiqueta") ?: "0"
            HistorialScreen(navController = navController, etiquetaActivo = etiqueta)
        }

        composable("profile") { ProfileScreen(navController) }

        composable("edit_profile") {
            val userId = prefs.getLong("idUsuario", 0L)
            EditProfileScreen(navController = navController, idUsuario = userId)
        }

        composable("crear_reporte") {
            val userId = prefs.getLong("idUsuario", 0L).toInt()
            val rolActual = rol.uppercase()
            if (rolActual == "USER" || rolActual == "USUARIO" || rolActual == "ADMIN") {
                CrearReporte(navController = navController, usuarioId = userId)
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }

        // --- RESGUARDOS ---
        composable("resguardos") {
            ResguardosPendientesScreen(navController = navController)
        }

        composable(
            route = "confirmar_resguardo/{resguardoId}",
            arguments = listOf(navArgument("resguardoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val resguardoId = backStackEntry.arguments?.getLong("resguardoId") ?: 0L
            ConfirmarResguardoScreen(navController = navController, resguardoId = resguardoId)
        }

        // --- MANTENIMIENTOS (TÉCNICO) ---
        composable("mantenimientos") {
            MantenimientosScreen(navController = navController)
        }

        composable(
            route = "atender_mantenimiento/{mantenimientoId}",
            arguments = listOf(navArgument("mantenimientoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val mantenimientoId = backStackEntry.arguments?.getLong("mantenimientoId") ?: 0L
            AtenderMantenimientoScreen(navController = navController, mantenimientoId = mantenimientoId)
        }
    }
}
