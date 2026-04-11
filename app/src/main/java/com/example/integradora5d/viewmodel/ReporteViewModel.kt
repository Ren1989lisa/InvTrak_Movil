package com.example.integradora5d.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class ReporteViewModel : ViewModel() {
    var estaCargando by mutableStateOf(false)
        private set

    var mensajeError by mutableStateOf<String?>(null)
        private set

    private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        return try {
            val file = File(context.cacheDir, "temp_reporte_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            // Importante: El nombre "archivos" DEBE coincidir con el @RequestPart de tu Controller
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("archivos", file.name, requestFile)
        } catch (e: Exception) { null }
    }

    fun enviarReporte(
        context: Context,
        etiqueta: String,
        descripcion: String,
        estatus: String,
        imagenes: List<Uri>,
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
                // Limpieza de ID: "Etq. bien: ID: 1" -> "1"
                val soloNumeros = etiqueta.filter { it.isDigit() }
                val activoId = soloNumeros.toLongOrNull()

                if (activoId == null) {
                    mensajeError = "La etiqueta no contiene un ID valido."
                    estaCargando = false
                    return@launch
                }

                // Mapa exacto para CreateReporteDTO
                val reporteMap = mapOf(
                    "activoId" to activoId,
                    "tipoFalla" to "Falla técnica",
                    "descripcion" to descripcion,
                    "prioridadId" to 1L // Valor por defecto
                )

                val jsonBody = Gson().toJson(reporteMap)
                // Usamos "application/json" para que @RequestPart lo procese correctamente
                val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

                val multipartImagenes = imagenes.mapNotNull { uriToMultipart(context, it) }

                val api = RetrofitClient.create(context)

                // Si no hay imagenes, enviamos null para que el Controller lo maneje
                val response = api.crearReporte(
                    datos = requestBody,
                    archivos = if (multipartImagenes.isEmpty()) null else multipartImagenes
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    mensajeError = "Servidor (${response.code()}): $errorBody"
                }
            } catch (e: Exception) {
                mensajeError = "Error de red: ${e.localizedMessage}"
            } finally {
                estaCargando = false
            }
        }
    }

    fun limpiarError() { mensajeError = null }
}