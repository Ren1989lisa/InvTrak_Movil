package com.example.integradora5d.ui.components

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController

//  IMPORTS OBLIGATORIOS
import com.example.integradora5d.ui.components.DrawerMenu
import com.example.integradora5d.ui.components.DrawerMenuTecnico
import com.example.integradora5d.ui.components.DrawerMenuUsuario

@Composable
fun DrawerSelector(
    navController: NavController,
    currentRoute: String
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val rol by remember {
        mutableStateOf(prefs.getString("rol", "") ?: "")
    }

    when (rol) {
        "ADMIN" -> DrawerMenu(navController, currentRoute)
        "TECNICO" -> DrawerMenuTecnico(navController, currentRoute)
        "USER" -> DrawerMenuUsuario(navController, currentRoute)
        else -> DrawerMenuUsuario(navController, currentRoute)
    }
}