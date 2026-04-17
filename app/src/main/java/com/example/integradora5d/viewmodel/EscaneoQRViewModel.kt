package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.network.ErrorHandler
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed class QrResultado {
    data class ResguardoPendiente(val activoId: Long, val resguardoId: Long, val activoNombre: String = "Activo") : QrResultado()
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

            Log.d("QRScanner", "Código escaneado: $codigo")
            Log.d("QRScanner", "Rol del usuario: $rol")

            try {
                // Mejorar extracción del ID del activo
                val activoId = extraerIdDelQR(codigo)
                if (activoId == null) {
                    _errorMessage.value = "Código QR no válido. Formato esperado: número o URL con ID"
                    return@launch
                }

                Log.d("QRScanner", "ID del activo extraído: $activoId")
                val api = RetrofitClient.create(context)

                when (rol.uppercase()) {
                    "TECNICO" -> {
                        procesarQRTecnico(context, api, activoId)
                    }
                    "USER", "USUARIO" -> {
                        procesarQRUsuario(api, activoId)
                    }
                    else -> {
                        _errorMessage.value = "Tu rol ($rol) no tiene permisos para escanear códigos QR"
                    }
                }
            } catch (e: Exception) {
                Log.e("QRScanner", "Error general: ${e.message}", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isSearching.value = false
            }
        }
    }

    private fun extraerIdDelQR(codigo: String): Long? {
        return try {
            // Caso 1: Solo números
            if (codigo.all { it.isDigit() }) {
                codigo.toLong()
            }
            // Caso 2: URL con parámetro id
            else if (codigo.contains("id=")) {
                val regex = "id=(\\d+)".toRegex()
                regex.find(codigo)?.groupValues?.get(1)?.toLong()
            }
            // Caso 3: Números al final de la cadena
            else {
                val numeros = codigo.filter { it.isDigit() }
                if (numeros.isNotEmpty()) numeros.toLong() else null
            }
        } catch (e: Exception) {
            Log.e("QRScanner", "Error extrayendo ID: ${e.message}")
            null
        }
    }

    private suspend fun procesarQRTecnico(context: Context, api: com.example.integradora5d.data.network.ApiService, activoId: Long) {
        try {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val tecnicoId = prefs.getLong("idUsuario", 0L)
            
            Log.d("QRScanner", "Buscando mantenimientos para técnico: $tecnicoId, activo: $activoId")
            
            val mantenimientos = api.getMantenimientosByTecnico(tecnicoId)
            val mant = mantenimientos.firstOrNull {
                it.reporte?.activo?.idActivo == activoId &&
                        it.estatus in listOf("PENDIENTE", "EN_PROCESO")
            }
            
            if (mant != null) {
                Log.d("QRScanner", "Mantenimiento encontrado: ${mant.idMantenimiento}")
                _resultado.value = QrResultado.MantenimientoAsignado(mant.idMantenimiento)
            } else {
                _errorMessage.value = "No tienes un mantenimiento activo para este activo"
            }
        } catch (e: HttpException) {
            Log.e("QRScanner", "Error HTTP técnico: ${e.code()} - ${e.message()}")
            val errorMessage = if (e.code() >= 500) {
                "Error interno del servidor. Inténtalo más tarde"
            } else {
                ErrorHandler.parseHttpError(e)
            }
            _errorMessage.value = errorMessage
        } catch (e: Exception) {
            Log.e("QRScanner", "Error técnico: ${e.message}", e)
            _errorMessage.value = "Error de conexión: ${e.message}"
        }
    }

    private suspend fun procesarQRUsuario(api: com.example.integradora5d.data.network.ApiService, activoId: Long) {
        try {
            Log.d("QRScanner", "Verificando resguardo para activo: $activoId")

            val resguardo = api.verificarResguardoQR(activoId)

            Log.d("QRScanner", "Resguardo encontrado - ID: ${resguardo.idResguardo}, Confirmado: ${resguardo.confirmado}")

            if (!resguardo.confirmado) {
                val activoNombre = resguardo.activo?.producto?.nombre?.replace("/", "-") ?: "Activo"
                _resultado.value = QrResultado.ResguardoPendiente(
                    activoId = activoId,
                    resguardoId = resguardo.idResguardo,
                    activoNombre = activoNombre
                )
            } else {
                _errorMessage.value = "Este resguardo ya ha sido confirmado"
            }
        } catch (e: HttpException) {
            // El GlobalErrorHandler del backend devuelve texto plano (no JSON)
            val mensajeBackend = try {
                e.response()?.errorBody()?.string()?.trim()
            } catch (ex: Exception) { null }

            Log.e("QRScanner", "HTTP ${e.code()} - $mensajeBackend")

            _errorMessage.value = when (e.code()) {
                400, 404 -> mensajeBackend?.takeIf { it.isNotBlank() }
                    ?: "Este bien no está asignado a tu cuenta o ya fue confirmado"
                401 -> "Sesión expirada. Inicia sesión nuevamente"
                403 -> "No tienes permisos para acceder a este activo"
                in 500..599 -> "Error interno del servidor. Inténtalo más tarde"
                else -> "Error del servidor (${e.code()})"
            }
        } catch (e: Exception) {
            Log.e("QRScanner", "Error usuario: ${e.message}", e)
            _errorMessage.value = "Error de conexión: ${e.message}"
        }
    }

    fun resetScanner() {
        _resultado.value = null
        _errorMessage.value = null
    }
}