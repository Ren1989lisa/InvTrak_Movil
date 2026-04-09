package com.example.integradora5d.viewmodel

import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

class ReporteViewModel : ViewModel() {
    var estaCargando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    fun enviarReporte(
        context: Context,
        etiqueta: String,
        descripcion: String,
        estatus: String,
        usuarioId: Int,
        onSuccess: () -> Unit
    ) {
        if (etiqueta.isBlank() || descripcion.isBlank()) {
            mensajeError = "Por favor, completa los campos requeridos."
            return
        }

        viewModelScope.launch {
            estaCargando = true
            mensajeError = null
            try {
                // 1. Mapeamos los datos al formato exacto que espera tu CreateReporteDTO en Spring
                // IMPORTANTE: Asegúrate de que estas llaves coincidan con tu DTO de Java
                val reporteMap = mapOf(
                    "id_activo" to (etiqueta.toLongOrNull() ?: 0L),
                    "descripcion" to descripcion,
                    "tipo_falla" to "Falla técnica", // Valor requerido por tu BeanReporte
                    "estatus" to "ABIERTO"         // Usando el ENUM_REPORTEDANIO
                )

                // 2. Convertimos el mapa a JSON y luego a RequestBody
                val gson = Gson()
                val jsonBody = gson.toJson(reporteMap)
                val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

                val api = RetrofitClient.create(context)

                // 3. Enviamos el requestBody a la parte "datos" y null a "archivos"
                val response = api.crearReporte(requestBody, null)

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    // Si sale 400 aquí, es porque los nombres en reporteMap no coinciden con el DTO de Java
                    mensajeError = "Error en el servidor: ${response.code()}"
                }
            } catch (e: Exception) {
                mensajeError = "Error de red: ${e.message}"
            } finally {
                estaCargando = false
            }
        }
    }

    fun limpiarError() {
        mensajeError = null
    }
}