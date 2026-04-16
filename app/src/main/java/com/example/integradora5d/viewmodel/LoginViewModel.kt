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
        private set
    var contrasena by mutableStateOf("")
        private set

    var loginExitoso by mutableStateOf<Boolean?>(null)
    var estaCargando by mutableStateOf(false)
    var rol by mutableStateOf("")

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
                val credenciales = mapOf("correo" to correo, "password" to contrasena)

                val respuesta = api.login(credenciales)

                val userId = respuesta.idUsuario
                val rolesList = respuesta.roles

                rol = when {
                    rolesList.contains("ROLE_ADMINISTRADOR") -> "ADMIN"
                    rolesList.contains("ROLE_TECNICO") -> "TECNICO"
                    else -> "USUARIO"
                }

                // GUARDADO CRÍTICO: Usamos commit() para asegurar la escritura antes de navegar
                val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                val success = prefs.edit().apply {
                    putString("token", respuesta.accessToken)
                    putString("rol", rol)
                    putLong("idUsuario", userId)
                }.commit() // commit() es síncrono y devuelve true si se guardó bien

                if (success) {
                    Log.d("LOGIN_SUCCESS", "Usuario ID $userId y Token guardados correctamente")
                    loginExitoso = true
                    onLoginSuccess()
                } else {
                    Log.e("LOGIN_ERROR", "Error al escribir en SharedPreferences")
                    loginExitoso = false
                }

            } catch (e: Exception) {
                Log.e("LOGIN_ERROR", "Error: ${e.localizedMessage}")
                loginExitoso = false
            } finally {
                estaCargando = false
            }
        }
    }
}