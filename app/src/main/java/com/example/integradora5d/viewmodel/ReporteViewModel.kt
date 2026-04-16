package com.example.integradora5d.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
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
    var exito by mutableStateOf(false)

    private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        return try {
            val file = File(context.cacheDir, "temp_reporte_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            // Nombre "archivos" coincide con @RequestPart en ReporteController.java
            MultipartBody.Part.createFormData("archivos", file.name, requestFile)
        } catch (e: Exception) {
            Log.e("URI_TO_MULTIPART", "Error: ${e.message}")
            null
        }
    }

    fun enviarReporte(
        context: Context,
        etiqueta: String,
        descripcion: String,
        estatus: String,
        imagenes: List<Uri>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            estaCargando = true
            mensajeError = null
            try {
                // Extraer el número de la etiqueta (ej: "ID: 5" -> 5)
                val activoId = etiqueta.filter { it.isDigit() }.toLongOrNull()
                
                if (activoId == null) {
                    mensajeError = "La etiqueta debe contener el número ID del bien."
                    return@launch
                }

                // DATA QUE COINCIDE CON CreateReporteDTO.java
                val reporteData = mapOf(
                    "activoId" to activoId,
                    "tipoFalla" to estatus, // "Activo", "En mantenimiento", "Dañado"
                    "descripcion" to descripcion,
                    "prioridadId" to 1L // Valor por defecto (Baja/Media)
                )

                val jsonBody = Gson().toJson(reporteData)
                // Es vital especificar application/json aquí
                val datosPart = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

                val multipartImagenes = imagenes.mapNotNull { uriToMultipart(context, it) }

                val api = RetrofitClient.create(context)
                val response = api.crearReporte(
                    datos = datosPart,
                    archivos = if (multipartImagenes.isEmpty()) null else multipartImagenes
                )

                if (response.isSuccessful) {
                    exito = true
                    onSuccess()
                } else {
                    val errorDetail = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("API_ERROR", "Status: ${response.code()} -> $errorDetail")
                    mensajeError = "Error del servidor (${response.code()})"
                }
            } catch (e: Exception) {
                mensajeError = "No hay conexión con el servidor"
                Log.e("NETWORK_ERROR", e.localizedMessage ?: "Error")
            } finally {
                estaCargando = false
            }
        }
    }

    fun limpiarError() { mensajeError = null }
}
