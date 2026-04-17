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

    fun cargarHistorial(context: Context, activoRef: String) {
        viewModelScope.launch {
            cargando = true
            mensajeError = null
            try {
                val api = RetrofitClient.create(context)
                listaHistorial = if (activoRef == "todos") {
                    api.getHistorial()
                } else {
                    val activoId = activoRef.toLongOrNull()
                        ?: throw IllegalArgumentException("ID de activo no valido")
                    api.getHistorialByActivoId(activoId)
                }
                Log.d("API_DEBUG", "Historial cargado correctamente")
            } catch (e: IllegalArgumentException) {
                listaHistorial = emptyList()
                mensajeError = e.message
            } catch (e: HttpException) {
                listaHistorial = emptyList()
                mensajeError = if (e.code() == 401) {
                    "Tu sesion ha expirado o no tienes permisos."
                } else {
                    "Error del servidor: ${e.code()}"
                }
                Log.e("API_DEBUG", "Error HTTP controlado: ${e.message()}")
            } catch (e: Exception) {
                listaHistorial = emptyList()
                mensajeError = "No se pudo conectar con el servidor"
                Log.e("API_DEBUG", "Error al cargar historial: ${e.localizedMessage}")
            } finally {
                cargando = false
            }
        }
    }
}
