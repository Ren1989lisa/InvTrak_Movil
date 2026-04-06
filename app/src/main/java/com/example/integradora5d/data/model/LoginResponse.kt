package com.example.integradora5d.data.model

data class LoginResponse(
    val accessToken: String,
    val tokenType: String,
    val primerAcceso: Boolean,
    val roles: List<String>
)