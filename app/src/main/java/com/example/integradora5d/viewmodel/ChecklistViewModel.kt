package com.example.integradora5d.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.ChecklistConfirmacion
import com.example.integradora5d.data.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class ChecklistViewModel : ViewModel() {

    data class UiState(
        val resguardoId: Long = 0L,
        val activoNombre: String = "",
        val encendido: String = "",
        val pantalla: String = "",
        val cargador: String = "",
        val danios: String = "NO",
        val observaciones: String = "",
        val fotos: List<String> = emptyList(),
        val mensaje: String? = null,
        val isLoading: Boolean = false,
        val envioExitoso: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun inicializarChecklist(resguardoId: Long, activoNombre: String) {
        _uiState.update { 
            it.copy(
                resguardoId = resguardoId,
                activoNombre = activoNombre
            )
        }
    }

    fun setEncendido(valor: String) {
        _uiState.update { it.copy(encendido = valor) }
    }

    fun setPantalla(valor: String) {
        _uiState.update { it.copy(pantalla = valor) }
    }

    fun setCargador(valor: String) {
        _uiState.update { it.copy(cargador = valor) }
    }

    fun setDanios(valor: String) {
        _uiState.update { it.copy(danios = valor) }
    }

    fun setObservaciones(valor: String) {
        _uiState.update { it.copy(observaciones = valor) }
    }

    fun agregarFoto(path: String) {
        _uiState.update { it.copy(fotos = it.fotos + path) }
    }

    fun confirmarResguardo(context: Context, onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.encendido.isBlank() || state.pantalla.isBlank() || state.cargador.isBlank()) {
            _uiState.update { it.copy(mensaje = "Por favor, completa todo el checklist") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensaje = null) }
            
            try {
                val checklist = ChecklistConfirmacion(
                    idResguardo = state.resguardoId,
                    enciende = state.encendido,
                    pantallaFunciona = state.pantalla,
                    tieneCargador = state.cargador,
                    danios = state.danios,
                    observaciones = state.observaciones.ifBlank { null }
                )

                val gson = Gson()
                val jsonBody = gson.toJson(checklist)
                val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())

                val api = RetrofitClient.create(context)
                val response = api.confirmarResguardo(requestBody, null)

                if (response.isSuccessful) {
                    _uiState.update { 
                        it.copy(
                            mensaje = "Resguardo confirmado correctamente",
                            envioExitoso = true,
                            isLoading = false
                        )
                    }
                    onSuccess()
                } else {
                    _uiState.update { 
                        it.copy(
                            mensaje = "Error al confirmar el resguardo",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        mensaje = "Error de conexión: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }

    fun resetState() {
        _uiState.value = UiState()
    }
}