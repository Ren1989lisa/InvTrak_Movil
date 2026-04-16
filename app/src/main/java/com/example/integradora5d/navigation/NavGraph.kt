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
    
    // El ViewModel se define aquí para que sea compartido entre la lista y el detalle
    val bienViewModel: BienRegistradoViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("bienes_principal") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onForgotPassword = { navController.navigate("resetPassword") }
            )
        }

        composable("bienes_principal") {
            val prefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
            val rol = prefs.getString("rol", "") ?: ""
            BienRegistradoScreen(
                navController = navController,
                viewModel = bienViewModel,
                userRole = rol
            )
        }

        // Definición de ruta para Long (idOriginal)
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

        // Compatibilidad con rutas externas
        composable("bienes") {
            LaunchedEffect(Unit) {
                navController.navigate("bienes_principal") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        composable("scanqr") { ScanQrScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("resetPassword") { ResetPasswordScreen(onBackToLogin = { navController.popBackStack() }) }
        
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

        composable("edit_profile") {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = prefs.getLong("idUsuario", 0L)
            EditProfileScreen(navController = navController, idUsuario = userId)
        }

        composable("crear_reporte") {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = prefs.getLong("idUsuario", 0L).toInt()
            CrearReporte(navController = navController, usuarioId = userId)
        }
    }
}
