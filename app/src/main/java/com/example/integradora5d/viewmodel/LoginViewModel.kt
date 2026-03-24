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

    fun onContrasenaChange(value: String) {
        contrasena = value
    }

    fun login() {
        when {
            correo == "admin@gmail.com" && contrasena == "1234" -> {
                loginExitoso = true
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
                loginExitoso = false
            }
        }
    }
}