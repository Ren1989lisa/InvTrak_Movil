package com.example.integradora5d.ui.screen.home

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    val rol = prefs.getString("rol", "") ?: ""

    LaunchedEffect(rol) {
        when (rol.uppercase()) {
            "ADMIN" -> navController.navigate("admin_home") {
                popUpTo("home") { inclusive = true }
            }
            "TECNICO" -> navController.navigate("tecnico_home") {
                popUpTo("home") { inclusive = true }
            }
            else -> navController.navigate("resguardos") {
                popUpTo("home") { inclusive = true }
            }
        }
    }
}
