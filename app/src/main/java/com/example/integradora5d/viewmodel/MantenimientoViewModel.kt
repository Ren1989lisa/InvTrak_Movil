package com.example.integradora5d.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.MantenimientoResponse
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

class MantenimientoViewModel : ViewModel() {

    private val _mantenimientos = MutableStateFlow<List<MantenimientoResponse>>(emptyList())
    val mantenimientos = _mantenimientos.asStateFlow()

    private val _mantenimientoActual = MutableStateFlow<MantenimientoResponse?>(null)
    val mantenimientoActual = _mantenimientoActual.asStateFlow()

    var estaCargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    var exito by mutableStateOf(false)
        private set

    fun cargarMantenimientos(context: Context, tecnicoId: Long) {
        viewModelScope.launch {
            estaCargando = true
            error = null
            try {
                val lista = RetrofitClient.create(context).getMantenimientosByTecnico(tecnicoId)
                _mantenimientos.value = lista
            } catch (e: Exception) {
                error = "Error al cargar mantenimientos: ${e.localizedMessage}"
                Log.e("MantenimientoVM", e.message ?: "")
            } finally {
                estaCargando = false
            }
        }
    }

    fun cargarDetalle(context: Context, mantenimientoId: Long) {
        viewModelScope.launch {
            estaCargando = true
            error = null
            try {
                val detalle = RetrofitClient.create(context).getMantenimientoById(mantenimientoId)
                _mantenimientoActual.value = detalle
            } catch (e: Exception) {
                error = "Error al cargar el mantenimiento: ${e.localizedMessage}"
                Log.e("MantenimientoVM", e.message ?: "")
            } finally {
                estaCargando = false
            }
        }
    }

    fun atender(
        context: Context,
        mantenimientoId: Long,
        diagnostico: String,
        accionesRealizadas: String,
        piezasUtilizadas: String,
        fotos: List<Uri>,
        onSuccess: () -> Unit
    ) {
        if (diagnostico.isBlank() || accionesRealizadas.isBlank()) {
            error = "El diagnóstico y las acciones realizadas son obligatorios"
            return
        }
        viewModelScope.launch {
            estaCargando = true
            error = null
            try {
                val dto = mapOf(
                    "mantenimientoId" to mantenimientoId,
                    "diagnostico" to diagnostico,
                    "accionesRealizadas" to accionesRealizadas,
                    "piezasUtilizadas" to piezasUtilizadas
                )
                val datosPart = Gson().toJson(dto)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val fotosParts = fotos.mapNotNull { uriToMultipart(context, it) }

                val response = RetrofitClient.create(context)
                    .atenderMantenimiento(datosPart, fotosParts.ifEmpty { null })

                if (response.isSuccessful) {
                    response.body()?.let { _mantenimientoActual.value = it }
                    onSuccess()
                } else {
                    error = "Error ${response.code()}: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                error = "Error de conexión: ${e.localizedMessage}"
                Log.e("MantenimientoVM", e.message ?: "")
            } finally {
                estaCargando = false
            }
        }
    }

    fun cerrar(
        context: Context,
        mantenimientoId: Long,
        estatusFinal: String,
        observaciones: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            estaCargando = true
            error = null
            try {
                val body = mapOf<String, Any>(
                    "mantenimientoId" to mantenimientoId,
                    "estatusFinal" to estatusFinal,
                    "observaciones" to observaciones
                )
                val response = RetrofitClient.create(context).cerrarMantenimiento(body)
                if (response.isSuccessful) {
                    exito = true
                    onSuccess()
                } else {
                    error = "Error ${response.code()}: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                error = "Error de conexión: ${e.localizedMessage}"
                Log.e("MantenimientoVM", e.message ?: "")
            } finally {
                estaCargando = false
            }
        }
    }

    fun limpiarError() { error = null }

    private fun uriToMultipart(context: Context, uri: Uri): MultipartBody.Part? {
        return try {
            val file = File(context.cacheDir, "mant_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("fotos", file.name, requestFile)
        } catch (e: Exception) {
            Log.e("MantenimientoVM", "Error al procesar imagen: ${e.message}")
            null
        }
    }
}
