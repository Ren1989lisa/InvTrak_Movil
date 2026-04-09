package com.example.integradora5d.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.data.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EscaneoQRViewModel(private val apiService: ApiService) : ViewModel() {

    // Guarda el resultado del bien encontrado tras escanear
    private val _bienEncontrado = MutableStateFlow<BienRegistrado?>(null)
    val bienEncontrado = _bienEncontrado.asStateFlow()

    // Para mostrar un progreso mientras busca en la base de datos
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    // Para manejar errores (ej. si el QR no es de un producto válido)
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    /**
     * Esta función se dispara cuando la cámara detecta un texto
     */
    fun procesarCodigoEscaneado(codigo: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _errorMessage.value = null

            try {
                // El 'codigo' es lo que leyó el QR (la etiqueta)
                // Vamos a MySQL a través de Spring Boot
                val response = apiService.getBienByEtiqueta(codigo)

                if (response != null) {
                    _bienEncontrado.value = response
                } else {
                    _errorMessage.value = "El producto no existe en el inventario"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión con el servidor"
                e.printStackTrace()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun resetScanner() {
        _bienEncontrado.value = null
        _errorMessage.value = null
    }
}