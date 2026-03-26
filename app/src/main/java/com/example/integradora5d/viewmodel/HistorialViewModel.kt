package com.example.integradora5d.viewmodel

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

    fun cargarHistorial(etiqueta: String) {
        viewModelScope.launch {
            cargando = true
            mensajeError = null
            try {
                val respuesta = RetrofitClient.apiService.getHistorialByEtiqueta(etiqueta)
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