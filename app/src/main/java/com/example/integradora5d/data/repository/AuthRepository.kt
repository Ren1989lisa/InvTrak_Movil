package com.example.integradora5d.data.repository

class AuthRepository {
    fun login(correo: String, contrasena: String): Boolean {
        // Aquí iría la lógica real (API, BD, etc.)
        return correo == "your.email@example.com" && contrasena == "1234"
    }
}
