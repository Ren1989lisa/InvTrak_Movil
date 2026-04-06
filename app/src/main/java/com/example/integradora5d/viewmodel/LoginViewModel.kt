package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")

    var loginExitoso by mutableStateOf<Boolean?>(null)
    var estaCargando by mutableStateOf(false)

    var rol by mutableStateOf("")
    var token by mutableStateOf("")

    fun onCorreoChange(value: String) { correo = value }
    fun onContrasenaChange(value: String) { contrasena = value }

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
                rol = respuesta.roles.firstOrNull() ?: "USER"

                val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                prefs.edit()
                    .putString("token", token)
                    .putString("rol", rol)
                    .apply()

                loginExitoso = true

                Log.d("LOGIN", "Token: $token")
                Log.d("LOGIN", "Rol: $rol")

            } catch (e: Exception) {
                Log.e("LOGIN", "Error: ${e.message}")
                loginExitoso = false
            } finally {
                estaCargando = false
            }
        }
    }
}