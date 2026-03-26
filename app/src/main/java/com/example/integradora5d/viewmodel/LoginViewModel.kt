package com.example.integradora5d.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // Campos de texto reactivos
    var correo by mutableStateOf("")
    var contrasena by mutableStateOf("")

    // Estados de flujo
    var loginExitoso by mutableStateOf<Boolean?>(null)
    var estaCargando by mutableStateOf(false)

    // Datos recuperados de la BD
    var rol by mutableStateOf("")
    var userId by mutableStateOf(0)

    fun onCorreoChange(value: String) { correo = value }
    fun onContrasenaChange(value: String) { contrasena = value }

    fun login() {
        if (correo.isBlank() || contrasena.isBlank()) {
            loginExitoso = false
            return
        }

        viewModelScope.launch {
            estaCargando = true
            loginExitoso = null

            try {
                // Preparamos el Map para el @Body de Retrofit
                val credenciales = mapOf(
                    "correo" to correo,
                    "contrasena" to contrasena
                )

                // Llamada a tu ApiService.login
                val respuesta = RetrofitClient.apiService.login(credenciales)

                // Guardamos la info que viene de Spring
                userId = respuesta.id
                rol = respuesta.rol
                loginExitoso = true

                Log.d("LoginAPI", "Usuario verificado: ${respuesta.correo} [ID: $userId, Rol: $rol]")

            } catch (e: Exception) {
                Log.e("LoginAPI", "Error de autenticación: ${e.message}")
                loginExitoso = false
            } finally {
                estaCargando = false
            }
        }
    }
}