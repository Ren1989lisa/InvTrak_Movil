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
        private set // Es buena práctica que solo el ViewModel cambie el estado

    var contrasena by mutableStateOf("")
        private set

    var loginExitoso by mutableStateOf<Boolean?>(null)
    var estaCargando by mutableStateOf(false)
    var rol by mutableStateOf("")
    var token by mutableStateOf("")

    fun onCorreoChange(value: String) { correo = value }
    fun onContrasenaChange(value: String) { contrasena = value }

    fun login(context: Context, onLoginSuccess: () -> Unit) {
        if (correo.isBlank() || contrasena.isBlank()) {
            loginExitoso = false
            return
        }

        viewModelScope.launch {
            estaCargando = true
            loginExitoso = null

            try {
                val api = RetrofitClient.create(context)
                // Se envían "correo" y "password" tal cual los espera tu @Body en Spring
                val credenciales = mapOf("correo" to correo, "password" to contrasena)

                val respuesta = api.login(credenciales)

                // IMPORTANTE: Asegúrate de que tu modelo LoginResponse tenga estos nombres
                token = respuesta.accessToken
                val rolesList = respuesta.roles

                // Mantenemos tu lógica de mapeo de roles
                rol = when {
                    rolesList.contains("ROLE_ADMINISTRADOR") -> "ADMIN"
                    rolesList.contains("ROLE_TECNICO") -> "TECNICO"
                    else -> "USUARIO"
                }

                // Guardado en SharedPreferences
                val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("token", token)
                    putString("rol", rol)
                    putLong("userId", respuesta.id) // Asegúrate que 'id' exista en LoginResponse
                    apply()
                }

                loginExitoso = true
                onLoginSuccess()

            } catch (e: Exception) {
                Log.e("LOGIN_ERROR", "Detalle: ${e.localizedMessage}")
                loginExitoso = false
            } finally {
                estaCargando = false
            }
        }
    }
}