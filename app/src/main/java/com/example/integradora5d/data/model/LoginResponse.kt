package com.example.integradora5d.data.model


data class LoginResponse(
    val id: Int,
    val correo: String,
    val rol: String,
    val mensaje: String? = null
)