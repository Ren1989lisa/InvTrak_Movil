package com.example.integradora5d.navigation

sealed class Screen(val route: String) {

    object Login : Screen("login")
    object Bienes : Screen("bienes")
    object BienDetalle : Screen("bien_detalle")

}