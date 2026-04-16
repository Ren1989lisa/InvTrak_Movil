package com.example.integradora5d.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.ForgotPasswordRequest
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    var mensajeExito by mutableStateOf<String?>(null)
        private set

    fun solicitarRecuperacion(correo: String, onSuccess: () -> Unit) {
        if (correo.isBlank()) {
            mensajeError = "Por favor, ingresa tu correo electrónico"
            return
        }

        viewModelScope.launch {
            isLoading = true
            mensajeError = null
            try {
                // Usamos el cliente público
                val api = RetrofitClient.createPublic()
                val response = api.solicitarRecuperacion(ForgotPasswordRequest(correo))

                if (response.isSuccessful) {
                    mensajeExito = "Código enviado. Revisa tu bandeja de entrada."
                    onSuccess()
                } else {
                    // Si el servidor responde 404 o 400
                    mensajeError = "El correo ingresado no está registrado."
                }
            } catch (e: Exception) {
                // Error de conexión (IP incorrecta o servidor apagado)
                mensajeError = "Error de conexión: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun limpiarMensajes() {
        mensajeError = null
        mensajeExito = null
    }
}