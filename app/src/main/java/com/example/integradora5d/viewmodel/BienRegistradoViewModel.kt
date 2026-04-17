package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
        Log.d("VIEWMODEL", "Cargando datos para el rol: $role")

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
                val respuesta = api.getActivos()

                val listaMapeada = respuesta.map { activo ->
                    BienRegistrado(
                        idOriginal = activo.idActivo,
                        etiqueta = activo.etiquetaBien ?: "Activo ${activo.idActivo}",
                        tipo = activo.productoNombre.ifBlank { "Activo" },
                        descripcion = activo.descripcion ?: "Sin descripcion",
                        fechaAlta = activo.fechaAlta ?: "N/A",
                        ubicacion = activo.ubicacionCompleta.ifBlank { "Sin ubicacion" },
                        costo = activo.costo?.let { "$${"%.2f".format(it)}" } ?: "N/A",
                        estado = activo.estatus ?: "DISPONIBLE"
                    )
                }

                bienesRegistrados.clear()
                bienesRegistrados.addAll(listaMapeada)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error en cargarBienes: ${e.message}")
            } finally {
                estaCargando = false
            }
        }
    }

    fun cargarUsuarios(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            try {
                val api = RetrofitClient.create(context)
                val usuarios = api.getTecnicos()

                val listaMapeada = usuarios.map { user ->
                    BienRegistrado(
                        idOriginal = 0L,
                        etiqueta = user.nombre ?: "Usuario",
                        tipo = "Tecnico",
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
                Log.e("API_ERROR", "Error en cargarUsuarios: ${e.message}")
            } finally {
                estaCargando = false
            }
        }
    }
}
