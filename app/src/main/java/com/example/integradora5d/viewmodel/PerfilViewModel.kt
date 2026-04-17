package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.ResguardoResponse
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PerfilViewModel : ViewModel() {
    var nombre by mutableStateOf("Cargando...")
    var correo by mutableStateOf("")
    var fechaNacimiento by mutableStateOf("")
    var curp by mutableStateOf("")
    var rol by mutableStateOf("")
    var numeroEmpleado by mutableStateOf("")
    var area by mutableStateOf("")
    var estaCargando by mutableStateOf(false)

    private val _bienesResguardados = MutableStateFlow<List<ResguardoResponse>>(emptyList())
    val bienesResguardados = _bienesResguardados.asStateFlow()

    fun cargarDatosUsuario(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            try {
                val api = RetrofitClient.create(context)
                val user = api.getPerfilPropio()
                nombre = user.nombre
                correo = user.correo
                fechaNacimiento = user.fechaNacimiento ?: "No especificada"
                curp = user.curp ?: "No registrado"
                rol = user.rol ?: "Sin rol"
                numeroEmpleado = user.numeroEmpleado ?: "N/A"
                area = user.area ?: "General"

                // Cargar solo los resguardos confirmados del usuario
                val resguardos = api.getResguardos()
                _bienesResguardados.value = resguardos.filter { it.confirmado && it.fechaDevolucion == null }

            } catch (e: HttpException) {
                Log.e("API_ERROR", "Error del servidor: ${e.code()}")
                nombre = "Error del servidor (${e.code()})"
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error de red/datos: ${e.message}")
                nombre = "Error de conexión"
            } finally {
                estaCargando = false
            }
        }
    }
}