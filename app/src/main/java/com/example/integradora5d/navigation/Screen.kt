package com.example.integradora5d.navigation

sealed class Screen(val route: String) {

    object Login : Screen("login")
    object Home : Screen("home")
    object AdminHome : Screen("admin_home")
    object TecnicoHome : Screen("tecnico_home")
    object UsuarioHome : Screen("usuario_home")
    object Bienes : Screen("bienes")
    object BienDetalle : Screen("bien_detalle")
    object ScanQr : Screen("scanqr")
    object Checklist : Screen("checklist")
    object MisBienes : Screen("mis_bienes")

}