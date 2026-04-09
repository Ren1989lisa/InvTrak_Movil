package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.network.RetrofitClient
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

    fun cargarDatosUsuario(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            try {
                // Forzamos la creación del cliente con el interceptor actualizado
                val api = RetrofitClient.create(context)
                val user = api.getPerfilPropio()

                nombre = user.nombre
                correo = user.correo
                fechaNacimiento = user.fechaNacimiento ?: "No especificada"
                curp = user.curp ?: "No registrado"
                rol = user.rol ?: "Sin rol"
                numeroEmpleado = user.numeroEmpleado ?: "N/A"
                area = user.area ?: "General"

            } catch (e: HttpException) {
                // Captura errores específicos del servidor (403, 404, 500)
                Log.e("API_ERROR", "Error del servidor: ${e.code()}")
                nombre = "Error del servidor (${e.code()})"
            } catch (e: Exception) {
                // Captura errores de red o de parsing de JSON
                Log.e("API_ERROR", "Error de red/datos: ${e.message}")
                nombre = "Error de conexión"
            } finally {
                estaCargando = false
            }
        }
    }
}