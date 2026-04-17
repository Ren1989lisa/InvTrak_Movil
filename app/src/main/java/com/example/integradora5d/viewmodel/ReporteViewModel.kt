package com.example.integradora5d.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.PrioridadResponse
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
    var folioReporte by mutableStateOf<Long?>(null)
    var prioridades = mutableStateListOf<PrioridadResponse>()
        private set
    var prioridadesCargadas by mutableStateOf(false)
        private set

    private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        return try {
            val file = File(context.cacheDir, "temp_reporte_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }

            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("archivos", file.name, requestFile)
        } catch (e: Exception) {
            Log.e("URI_TO_MULTIPART", "Error al procesar imagen: ${e.message}")
            null
        }
    }

    fun cargarPrioridades(context: Context) {
        if (prioridadesCargadas) return

        viewModelScope.launch {
            try {
                val api = RetrofitClient.create(context)
                val response = api.getPrioridades()
                prioridades.clear()
                prioridades.addAll(response)
                prioridadesCargadas = true
            } catch (e: Exception) {
                Log.e("PRIORIDADES_ERROR", e.localizedMessage ?: "Error desconocido")
                mensajeError = "No se pudieron cargar las prioridades"
            }
        }
    }

    fun enviarReporte(
        context: Context,
        activoId: Long,
        prioridadId: Long,
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
                    "tipoFalla" to "Falla tecnica",
                    "descripcion" to descripcion,
                    "prioridadId" to prioridadId
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
                    val errorBody = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                        ?: "Servidor: Error ${response.code()}"
                    Log.e("API_ERROR", "Codigo: ${response.code()} Body: $errorBody")
                    mensajeError = errorBody
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexion: Revisa tu internet o el servidor"
                Log.e("NETWORK_ERROR", e.localizedMessage ?: "Error desconocido")
            } finally {
                estaCargando = false
            }
        }
    }

    fun limpiarError() {
        mensajeError = null
    }
}
