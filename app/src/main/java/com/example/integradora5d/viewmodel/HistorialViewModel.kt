package com.example.integradora5d.viewmodel

import android.content.Context // Importación necesaria
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch

class HistorialViewModel : ViewModel() {
    var listaHistorial by mutableStateOf<List<BienRegistrado>>(emptyList())
    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    // Agregamos el context como parámetro para poder crear el cliente de Retrofit
    fun cargarHistorial(context: Context, etiqueta: String) {
        viewModelScope.launch {
            cargando = true
            mensajeError = null
            try {
                // Se utiliza RetrofitClient.create(context) para obtener la instancia de ApiService
                val api = RetrofitClient.create(context)
                val respuesta = api.getHistorialByEtiqueta(etiqueta)
                listaHistorial = respuesta
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.localizedMessage}"
                listaHistorial = emptyList()
            } finally {
                cargando = false
            }
        }
    }
}