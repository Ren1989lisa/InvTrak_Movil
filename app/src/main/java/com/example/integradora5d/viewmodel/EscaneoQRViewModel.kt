package com.example.integradora5d.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class QrResultado {
    data class ResguardoPendiente(val activoId: Long, val resguardoId: Long) : QrResultado()
    data class MantenimientoAsignado(val mantenimientoId: Long) : QrResultado()
    data class Error(val mensaje: String) : QrResultado()
}

class EscaneoQRViewModel : ViewModel() {

    private val _resultado = MutableStateFlow<QrResultado?>(null)
    val resultado = _resultado.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun procesarCodigoEscaneado(context: Context, codigo: String, rol: String) {
        viewModelScope.launch {
            _isSearching.value = true
            _errorMessage.value = null
            _resultado.value = null

            try {
                val activoId = codigo.filter { it.isDigit() }.toLongOrNull()
                    ?: run {
                        _errorMessage.value = "QR no válido: no contiene un ID de activo"
                        return@launch
                    }

                val api = RetrofitClient.create(context)

                when (rol.uppercase()) {
                    "TECNICO" -> {
                        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        val tecnicoId = prefs.getLong("idUsuario", 0L)
                        val mantenimientos = api.getMantenimientosByTecnico(tecnicoId)
                        val mant = mantenimientos.firstOrNull {
                            it.reporte?.activo?.idActivo == activoId &&
                                    it.estatus in listOf("PENDIENTE", "EN_PROCESO")
                        }
                        if (mant != null) {
                            _resultado.value = QrResultado.MantenimientoAsignado(mant.idMantenimiento)
                        } else {
                            _errorMessage.value = "No tienes un mantenimiento activo para este activo"
                        }
                    }
                    else -> {
                        val resguardo = api.verificarResguardoQR(activoId)
                        _resultado.value = QrResultado.ResguardoPendiente(
                            activoId = activoId,
                            resguardoId = resguardo.idResguardo
                        )
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = when {
                    e.message?.contains("404") == true -> "No tienes un resguardo pendiente para este activo"
                    e.message?.contains("403") == true -> "No tienes permiso para este activo"
                    else -> "Error de conexión con el servidor"
                }
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun resetScanner() {
        _resultado.value = null
        _errorMessage.value = null
    }
}