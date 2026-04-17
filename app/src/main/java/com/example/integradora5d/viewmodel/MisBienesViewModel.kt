package com.example.integradora5d.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.ResguardoResponse
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MisBienesViewModel : ViewModel() {

    private val _misBienes = MutableStateFlow<List<ResguardoResponse>>(emptyList())
    val misBienes: StateFlow<List<ResguardoResponse>> = _misBienes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarMisBienes(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val api = RetrofitClient.create(context)
                // Obtener todos los resguardos del usuario logueado
                val resguardos = api.getResguardos()
                
                // Filtrar solo los bienes que están confirmados y no devueltos (resguardados actualmente)
                val bienesResguardados = resguardos.filter { 
                    it.confirmado && it.fechaDevolucion == null 
                }
                
                _misBienes.value = bienesResguardados
            } catch (e: Exception) {
                _error.value = "Error al cargar tus bienes resguardados: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}