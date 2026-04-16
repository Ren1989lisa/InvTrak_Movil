package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch

class BienRegistradoViewModel : ViewModel() {

    var bienesRegistrados = mutableStateListOf<BienRegistrado>()
        private set

    var estaCargando by mutableStateOf(false)
        private set

    fun cargarDatosSegunRol(context: Context, userRole: String) {
        val role = userRole.uppercase().trim()

        // Evitamos recargar si ya hay datos
        if (bienesRegistrados.isNotEmpty()) return

        if (role == "TECNICO") {
            cargarUsuarios(context)
        } else {
            cargarBienes(context)
        }
    }

    fun cargarBienes(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            try {
                val api = RetrofitClient.create(context)
                val respuesta = api.getBienesReales()

                val listaMapeada = respuesta.map { producto ->
                    // MAPEO: Aquí conectamos los campos de Spring con los de tu UI
                    BienRegistrado(
                        idOriginal = producto.id_producto,
                        etiqueta = "ID: ${producto.id_producto}",
                        tipo = "Activo",
                        descripcion = producto.nombre ?: "Sin nombre",
                        fechaAlta = "N/A",
                        ubicacion = producto.descripcion ?: "Sin ubicación",
                        costo = "$0.00",
                        estado = producto.estatus ?: "DISPONIBLE"
                    )
                }
                bienesRegistrados.clear()
                bienesRegistrados.addAll(listaMapeada)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error: ${e.message}")
            } finally {
                estaCargando = false
            }
        }
    }

    private fun cargarUsuarios(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            try {
                val api = RetrofitClient.create(context)
                val usuarios = api.getTecnicos()
                val listaMapeada = usuarios.map { user ->
                    BienRegistrado(
                        idOriginal = user.idUsuario ?: 0L,
                        etiqueta = user.nombre ?: "Usuario",
                        tipo = "Técnico",
                        descripcion = user.correo ?: "",
                        fechaAlta = "N/A",
                        ubicacion = user.area ?: "General",
                        costo = "N/A",
                        estado = if (user.estatus) "Activo" else "Inactivo"
                    )
                }
                bienesRegistrados.clear()
                bienesRegistrados.addAll(listaMapeada)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error: ${e.message}")
            } finally {
                estaCargando = false
            }
        }
    }
}