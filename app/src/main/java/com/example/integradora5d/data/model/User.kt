package com.example.integradora5d.data.model

data class User(
    val id: Int,
    val username: String,
    val role: String // Puede ser "ADMIN", "TECNICO", "USUARIO"
)