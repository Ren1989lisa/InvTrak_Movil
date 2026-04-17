package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.ActivoCompleto
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    var activos = mutableStateListOf<ActivoCompleto>()
        private set

    var estaCargando by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun cargarTodosLosActivos(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            error = null
            try {
                val api = RetrofitClient.create(context)
                val respuesta = api.getActivos()
                
                activos.clear()
                activos.addAll(respuesta)
                
                Log.d("AdminVM", "Cargados ${activos.size} activos")
            } catch (e: Exception) {
                error = "Error al cargar los activos: ${e.message}"
                Log.e("AdminVM", "Error: ${e.message}")
            } finally {
                estaCargando = false
            }
        }
    }

    fun limpiarError() {
        error = null
    }
}