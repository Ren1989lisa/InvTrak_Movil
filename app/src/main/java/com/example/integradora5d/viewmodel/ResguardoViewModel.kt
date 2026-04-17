package com.example.integradora5d.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.ResguardoResponse
import com.example.integradora5d.data.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class ResguardoViewModel : ViewModel() {

    private val _resguardos = MutableStateFlow<List<ResguardoResponse>>(emptyList())
    val resguardos = _resguardos.asStateFlow()

    private val _resguardoActual = MutableStateFlow<ResguardoResponse?>(null)
    val resguardoActual = _resguardoActual.asStateFlow()

    var estaCargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var exito by mutableStateOf(false)
        private set

    fun cargarResguardos(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            error = null
            try {
                val lista = RetrofitClient.create(context).getResguardos()
                _resguardos.value = lista
            } catch (e: Exception) {
                error = "Error al cargar resguardos: ${e.localizedMessage}"
                Log.e("ResguardoVM", e.message ?: "")
            } finally {
                estaCargando = false
            }
        }
    }

    fun verificarPorQR(context: Context, activoId: Long) {
        viewModelScope.launch {
            estaCargando = true
            error = null
            try {
                val resguardo = RetrofitClient.create(context).verificarResguardoQR(activoId)
                _resguardoActual.value = resguardo
            } catch (e: Exception) {
                error = "No tienes un resguardo pendiente para este activo"
                Log.e("ResguardoVM", e.message ?: "")
            } finally {
                estaCargando = false
            }
        }
    }

    fun confirmar(
        context: Context,
        resguardoId: Long,
        enciende: String,
        pantallaFunciona: String,
        tieneCargador: String,
        danios: String,
        observaciones: String,
        fotos: List<Uri>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            estaCargando = true
            error = null
            try {
                val dto = mapOf(
                    "resguardoId" to resguardoId,
                    "enciende" to enciende,
                    "pantallaFunciona" to pantallaFunciona,
                    "tieneCargador" to tieneCargador,
                    "danios" to danios,
                    "observaciones" to observaciones
                )
                val datosPart = Gson().toJson(dto)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val fotosParts = fotos.mapNotNull { uriToMultipart(context, it, "fotos") }

                val response = RetrofitClient.create(context)
                    .confirmarResguardo(datosPart, fotosParts.ifEmpty { null })

                if (response.isSuccessful) {
                    exito = true
                    onSuccess()
                } else {
                    error = "Error ${response.code()}: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                error = "Error de conexión: ${e.localizedMessage}"
                Log.e("ResguardoVM", e.message ?: "")
            } finally {
                estaCargando = false
            }
        }
    }

    fun devolver(
        context: Context,
        resguardoId: Long,
        observaciones: String,
        fotos: List<Uri>,
        onSuccess: () -> Unit
    ) {
        if (fotos.isEmpty()) {
            error = "Debes adjuntar al menos una foto para la devolución"
            return
        }
        viewModelScope.launch {
            estaCargando = true
            error = null
            try {
                val dto = mapOf(
                    "resguardoId" to resguardoId,
                    "observaciones" to observaciones
                )
                val datosPart = Gson().toJson(dto)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val fotosParts = fotos.mapNotNull { uriToMultipart(context, it, "fotos") }

                val response = RetrofitClient.create(context)
                    .devolverResguardo(datosPart, fotosParts)

                if (response.isSuccessful) {
                    exito = true
                    onSuccess()
                } else {
                    error = "Error ${response.code()}: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                error = "Error de conexión: ${e.localizedMessage}"
                Log.e("ResguardoVM", e.message ?: "")
            } finally {
                estaCargando = false
            }
        }
    }

    fun devolverDirecto(
        context: Context,
        resguardoId: Long,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            estaCargando = true
            error = null
            try {
                val response = RetrofitClient.create(context)
                    .solicitarDevolucion(resguardoId)
                if (response.isSuccessful) {
                    exito = true
                    cargarResguardos(context)
                    onSuccess()
                } else {
                    error = "Error ${response.code()}: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                error = "Error de conexión: ${e.localizedMessage}"
                Log.e("ResguardoVM", e.message ?: "")
            } finally {
                estaCargando = false
            }
        }
    }

    fun limpiarError() { error = null }
    fun limpiarResguardoActual() { _resguardoActual.value = null }

    private fun uriToMultipart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
        return try {
            val file = File(context.cacheDir, "resguardo_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, file.name, requestFile)
        } catch (e: Exception) {
            Log.e("ResguardoVM", "Error al procesar imagen: ${e.message}")
            null
        }
    }
}
