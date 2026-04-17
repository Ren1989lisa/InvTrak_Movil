package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel : ViewModel() {

    var correo by mutableStateOf("")
        private set
    var contrasena by mutableStateOf("")
        private set

    var loginExitoso by mutableStateOf<Boolean?>(null)
    var estaCargando by mutableStateOf(false)
    var rol by mutableStateOf("")
    var mensajeError by mutableStateOf("Credenciales incorrectas o error de conexión")

    fun onCorreoChange(value: String) { correo = value }
    fun onContrasenaChange(value: String) { contrasena = value }

    private fun extractMessage(errorBody: String): String? {
        return try {
            val json = org.json.JSONObject(errorBody)
            json.optString("message").takeIf { it.isNotBlank() }
                ?: json.optString("error").takeIf { it.isNotBlank() }
        } catch (e: Exception) { null }
    }

    fun login(context: Context, onLoginSuccess: () -> Unit) {
        if (correo.isBlank() || contrasena.isBlank()) {
            loginExitoso = false
            return
        }

        viewModelScope.launch {
            estaCargando = true
            loginExitoso = null

            try {
                val api = RetrofitClient.createPublic()
                val credenciales = mapOf(
                    "correo" to correo.trim(),
                    "password" to contrasena
                )

                val respuesta = api.login(credenciales)
                val userId = respuesta.idUsuario

                val rolesList = respuesta.roles
                rol = when {
                    rolesList.contains("ROLE_ADMINISTRADOR") -> "ADMIN"
                    rolesList.contains("ROLE_TECNICO") -> "TECNICO"
                    else -> "USUARIO"
                }

                val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().apply {
                    putString("token", respuesta.accessToken)
                    putString("rol", rol)
                    putLong("idUsuario", userId)
                    putBoolean("primerAcceso", respuesta.primerAcceso)
                    apply()
                }

                Log.d("LOGIN_SUCCESS", "Usuario ID $userId guardado correctamente")
                loginExitoso = true

                // Registrar token FCM si está pendiente
                val fcmToken = prefs.getString("fcmToken", null)
                if (fcmToken != null) {
                    try {
                        val authApi = RetrofitClient.create(context)
                        authApi.actualizarPerfil(
                            userId,
                            com.example.integradora5d.data.model.UpdateUsuarioRequest(tokenDispositivo = fcmToken)
                        )
                    } catch (e: Exception) {
                        Log.w("LOGIN", "No se pudo registrar FCM token: ${e.message}")
                    }
                }

                onLoginSuccess()

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                mensajeError = when (e.code()) {
                    429 -> errorBody?.let { extractMessage(it) } ?: "Cuenta bloqueada temporalmente"
                    401 -> errorBody?.let { extractMessage(it) } ?: "Credenciales incorrectas"
                    else -> "Error del servidor (${e.code()})"
                }
                Log.e("LOGIN_ERROR", "HTTP ${e.code()}: $errorBody", e)
                loginExitoso = false
            } catch (e: Exception) {
                mensajeError = "Error de conexión. Verifica tu internet"
                Log.e("LOGIN_ERROR", "Error: ${e.localizedMessage}", e)
                loginExitoso = false
            } finally {
                estaCargando = false
            }
        }
    }
}
