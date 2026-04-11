package com.example.integradora5d.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    var mensajeExito by mutableStateOf<String?>(null)
        private set

    fun solicitarRecuperacion(context: Context, correo: String, onSuccess: () -> Unit) {
        if (correo.isBlank()) {
            mensajeError = "Por favor, ingresa tu correo electrónico"
            return
        }

        viewModelScope.launch {
            isLoading = true
            mensajeError = null
            mensajeExito = null

            try {
                val api = RetrofitClient.create(context)
                val response = api.solicitarRecuperacion(mapOf("correo" to correo))

                if (response.isSuccessful) {
                    mensajeExito = "Código enviado. Revisa tu bandeja de entrada."
                    onSuccess()
                } else {
                    mensajeError = "El correo ingresado no está registrado en el sistema."
                }
            } catch (e: Exception) {
                mensajeError = "No se pudo conectar con el servidor. Verifica tu internet."
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