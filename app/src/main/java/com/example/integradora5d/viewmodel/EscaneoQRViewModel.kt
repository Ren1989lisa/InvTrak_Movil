package com.example.integradora5d.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.data.network.ApiService
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EscaneoQRViewModel(private val apiService: ApiService) : ViewModel() {

    private val _bienEncontrado = MutableStateFlow<BienRegistrado?>(null)
    val bienEncontrado = _bienEncontrado.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun procesarCodigoEscaneado(codigo: String) {
        // Evitamos peticiones duplicadas si ya estamos buscando
        if (_isSearching.value) return

        viewModelScope.launch {
            _isSearching.value = true
            _errorMessage.value = null

            try {
                // Limpiamos cualquier resultado previo antes de buscar
                _bienEncontrado.value = null

                val response = apiService.getBienByEtiqueta(codigo)

                if (response != null) {
                    _bienEncontrado.value = response
                } else {
                    _errorMessage.value = "El producto no existe en el inventario"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión con el servidor"
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun resetScanner() {
        _bienEncontrado.value = null
        _errorMessage.value = null
    }

    // FACTORY para inyectar el ApiService correctamente
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EscaneoQRViewModel::class.java)) {
                val apiService = RetrofitClient.create(context)
                @Suppress("UNCHECKED_CAST")
                return EscaneoQRViewModel(apiService) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}