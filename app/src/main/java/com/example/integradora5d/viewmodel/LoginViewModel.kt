package com.example.integradora5d.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class LoginViewModel : ViewModel() {

    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")
    var loginExitoso by mutableStateOf<Boolean?>(null)

    fun onCorreoChange(value: String) {
        correo = value
    }

    fun onContrasenaChange(value: String) {
        contrasena = value
    }

    fun login() {
        loginExitoso = (correo == "alex@gmail.com" && contrasena == "1234")
    }
}
