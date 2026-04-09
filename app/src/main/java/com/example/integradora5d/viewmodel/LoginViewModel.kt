package com.example.integradora5d.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class LoginViewModel : ViewModel() {

    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var loginExitoso by mutableStateOf<Boolean?>(null)

    var rol by mutableStateOf("")

    fun onCorreoChange(value: String) {
        correo = value
    }

<<<<<<< Updated upstream
    fun onContrasenaChange(value: String) {
        contrasena = value
    }
=======
    fun login(context: Context) {
        if (correo.isBlank() || contrasena.isBlank()) {
            loginExitoso = false
            return
        }

        viewModelScope.launch {
            estaCargando = true
            loginExitoso = null

            try {
                val api = RetrofitClient.create(context)
                val credenciales = mapOf(
                    "correo" to correo,
                    "password" to contrasena
                )

                val respuesta = api.login(credenciales)
                token = respuesta.accessToken

                val rolesList = respuesta.roles
                rol = when {
                    rolesList.contains("ROLE_ADMINISTRADOR") -> "ADMIN"
                    rolesList.contains("ROLE_TECNICO") -> "TECNICO"
                    else -> "USUARIO"
                }

                // Guardado unificado en "user_prefs"
                val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit()
                    .putString("token", token)
                    .putString("rol", rol)
                    .apply()
>>>>>>> Stashed changes

    fun login() {
        when {
            correo == "admin@gmail.com" && contrasena == "1234" -> {
                loginExitoso = true
<<<<<<< Updated upstream
                rol = "ADMIN"
            }
            correo == "tecnico@gmail.com" && contrasena == "1234" -> {
                loginExitoso = true
                rol = "TECNICO"
            }
            correo == "user@gmail.com" && contrasena == "1234" -> {
                loginExitoso = true
                rol = "USER"
            }
            else -> {
=======

            } catch (e: Exception) {
                Log.e("LOGIN", "Error: ${e.message}")
>>>>>>> Stashed changes
                loginExitoso = false
            }
        }
    }
}