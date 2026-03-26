package com.example.integradora5d.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch

class BienRegistradoViewModel : ViewModel() {

    // Lista observable para la LazyColumn
    var bienesRegistrados = mutableStateListOf<BienRegistrado>()
        private set

    // Estado para el ProgressIndicator
    var estaCargando by mutableStateOf(false)
        private set

    /**
     * Función unificada: Decide qué endpoint llamar basándose en
     * los datos que "jalamos" del Login.
     */
    fun cargarDatosSegunRol(idUsuario: Int, rolUsuario: String) {
        viewModelScope.launch {
            estaCargando = true
            try {
                // Seleccionamos el método de tu ApiService según el Rol
                val respuesta = when (rolUsuario.uppercase()) {
                    "ADMIN" -> {
                        Log.d("API_Bienes", "Pidiendo todos los bienes (ADMIN)")
                        RetrofitClient.apiService.getTodosLosBienes()
                    }
                    "TECNICO" -> {
                        Log.d("API_Bienes", "Pidiendo bienes de mantenimiento (TECNICO)")
                        RetrofitClient.apiService.getBienesParaTecnico()
                    }
                    else -> {
                        Log.d("API_Bienes", "Pidiendo bienes del usuario: $idUsuario")
                        RetrofitClient.apiService.getBienesPorUsuario(idUsuario)
                    }
                }

                // Actualizamos la lista en la UI
                bienesRegistrados.clear()
                bienesRegistrados.addAll(respuesta)

            } catch (e: Exception) {
                Log.e("API_Bienes", "Error al obtener datos: ${e.message}")
                // Aquí podrías agregar un mensaje de error para el usuario
            } finally {
                estaCargando = false
            }
        }
    }
}