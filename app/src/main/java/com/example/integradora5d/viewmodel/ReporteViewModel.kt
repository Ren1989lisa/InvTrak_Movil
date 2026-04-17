package com.example.integradora5d.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.network.RetrofitClient
import com.example.integradora5d.data.model.Reporte // Asegúrate de tener este modelo o el que uses
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
    var exito by mutableStateOf(false)
    var folioReporte by mutableStateOf<Long?>(null)

    // Helper para convertir URI a MultipartBody.Part
    private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        return try {
            val file = File(context.cacheDir, "temp_reporte_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }

            // Creamos el RequestBody para la imagen
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

            // IMPORTANTE: El nombre "archivos" debe coincidir con @RequestPart en el Controller
            MultipartBody.Part.createFormData("archivos", file.name, requestFile)
        } catch (e: Exception) {
            Log.e("URI_TO_MULTIPART", "Error al procesar imagen: ${e.message}")
            null
        }
    }

    fun enviarReporte(
        context: Context,
        activoId: Long,
        descripcion: String,
        imagenes: List<Uri>,
        onSuccess: () -> Unit
    ) {
        if (descripcion.isBlank()) {
            mensajeError = "Por favor, completa los campos requeridos."
            return
        }

        viewModelScope.launch {
            estaCargando = true
            mensajeError = null
            try {
                val reporteMap = mapOf(
                    "activoId" to activoId,
                    "tipoFalla" to "Falla técnica",
                    "descripcion" to descripcion,
                    "prioridadId" to 1L
                )

                val jsonBody = Gson().toJson(reporteMap)
                val datosPart = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val multipartImagenes = imagenes.mapNotNull { uriToMultipart(context, it) }

                val api = RetrofitClient.create(context)
                val response = api.crearReporte(
                    datos = datosPart,
                    archivos = if (multipartImagenes.isEmpty()) null else multipartImagenes
                )

                if (response.isSuccessful) {
                    exito = true
                    folioReporte = response.body()?.idReporte
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("API_ERROR", "Código: ${response.code()} Body: $errorBody")
                    mensajeError = "Servidor: Error ${response.code()}"
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: Revisa tu internet o el servidor"
                Log.e("NETWORK_ERROR", e.localizedMessage ?: "Error desconocido")
            } finally {
                estaCargando = false
            }
        }
    }

    fun limpiarError() { mensajeError = null }
}