package com.example.integradora5d.navigation

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
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

    // Unificamos el acceso a SharedPreferences
    val prefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    var rol by remember { mutableStateOf(prefs.getString("rol", "") ?: "") }

    // El ViewModel se define aquí para que persista durante toda la navegación de bienes
    val bienViewModel: BienRegistradoViewModel = viewModel()

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
            BienRegistradoScreen(
                navController = navController, 
                viewModel = bienViewModel, 
                userRole = rol
            )
        }

        composable(
            route = "bien_detalle/{bienId}",
            arguments = listOf(navArgument("bienId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("bienId") ?: 0L
            BienDetalleScreen(
                navController = navController, 
                bienId = id, 
                viewModel = bienViewModel
            )
        }

        composable("scanqr") { ScanQrScreen(navController) }

        composable("reportinfo") {
            if (rol.uppercase() == "ADMIN") ReportInfoScreen()
            else LaunchedEffect(Unit) { navController.popBackStack() }
        }

        composable(
            route = "historial/{etiqueta}",
            arguments = listOf(navArgument("etiqueta") { 
                type = NavType.StringType
                defaultValue = "0"
            })
        ) { backStackEntry ->
            val etiqueta = backStackEntry.arguments?.getString("etiqueta") ?: "0"
            HistorialScreen(navController = navController, etiquetaActivo = etiqueta)
        }

        composable("profile") { ProfileScreen(navController) }

        composable("edit_profile") {
            // CORREGIDO: Usamos "idUsuario" que es la clave que usas en el Login
            val userId = prefs.getLong("idUsuario", 0L)
            EditProfileScreen(navController = navController, idUsuario = userId)
        }

        composable("crear_reporte") {
            // CORREGIDO: Usamos "idUsuario" consistentemente
            val userId = prefs.getLong("idUsuario", 0L).toInt()
            val rolActual = rol.uppercase()
            if (rolActual == "USER" || rolActual == "USUARIO" || rolActual == "ADMIN") {
                CrearReporte(navController = navController, usuarioId = userId)
            } else {
                LaunchedEffect(Unit) { navController.popBackStack() }
            }
        }
    }
}
