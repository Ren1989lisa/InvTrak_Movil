package com.example.integradora5d.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChecklistViewModel : ViewModel() {

    data class UiState(
        val encendido: String = "",
        val pantalla: String = "",
        val cargador: String = "",
        val observaciones: String = "",
        val fotos: List<String> = emptyList(),
        val mensaje: String? = null,
        val envioExitoso: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setEncendido(valor: String) {
        _uiState.update { it.copy(encendido = valor) }
    }

    fun setPantalla(valor: String) {
        _uiState.update { it.copy(pantalla = valor) }
    }

    fun setCargador(valor: String) {
        _uiState.update { it.copy(cargador = valor) }
    }

    fun setObservaciones(valor: String) {
        _uiState.update { it.copy(observaciones = valor) }
    }

    fun agregarFoto(path: String) {
        _uiState.update { it.copy(fotos = it.fotos + path) }
    }

    fun validarYEnviar() {
        val state = _uiState.value

        if (state.encendido.isBlank() || state.pantalla.isBlank() || state.cargador.isBlank()) {
            _uiState.update { it.copy(mensaje = "Por favor, completa todo el checklist") }
            return
        }

        if (state.fotos.isEmpty()) {
            _uiState.update { it.copy(mensaje = "Es obligatorio capturar al menos una evidencia fotográfica") }
            return
        }

        // Aquí iría tu llamada a la API de Spring Boot
        _uiState.update {
            it.copy(
                mensaje = "Resguardo confirmado correctamente",
                envioExitoso = true
            )
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}