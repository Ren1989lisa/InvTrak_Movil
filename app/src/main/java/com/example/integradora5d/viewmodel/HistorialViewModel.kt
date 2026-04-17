package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.BeanHistorialResponse
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HistorialViewModel : ViewModel() {
    var listaHistorial by mutableStateOf<List<BeanHistorialResponse>>(emptyList())
    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    fun cargarHistorial(context: Context, etiqueta: String) {
        if (etiqueta == "todos") {
            mensajeError = "Selecciona un activo específico desde la lista de bienes para ver su historial."
            cargando = false
            return
        }

        val idL = etiqueta.filter { it.isDigit() }.toLongOrNull()

        if (idL == null) {
            mensajeError = "ID de activo no válido"
            return
        }

        viewModelScope.launch {
            cargando = true
            mensajeError = null
            try {
                val api = RetrofitClient.create(context)
                val response = api.getHistorialByActivoId(idL)
                listaHistorial = response
                Log.d("API_DEBUG", "Éxito al cargar historial")
            } catch (e: HttpException) {
                // ESTO EVITA EL CRASH: Atrapamos el error del servidor (401, 403, etc)
                mensajeError = if (e.code() == 401) {
                    "Tu sesión ha expirado o no tienes permisos."
                } else {
                    "Error del servidor: ${e.code()}"
                }
                Log.e("API_DEBUG", "Error HTTP controlado: ${e.message()}")
            } catch (e: Exception) {
                // Atrapamos errores de internet o de código
                mensajeError = "No se pudo conectar con el servidor"
                Log.e("API_DEBUG", "Crash evitado: ${e.localizedMessage}")
            } finally {
                cargando = false
            }
        }
    }
}