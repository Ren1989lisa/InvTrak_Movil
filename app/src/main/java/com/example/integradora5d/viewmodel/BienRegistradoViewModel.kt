package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
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

    fun cargarDatosSegunRol(context: Context, userId: Int, userRole: String) {
        when (userRole.uppercase()) {
            "ADMIN" -> cargarUsuarios(context)
            "TECNICO" -> cargarUsuarios(context)
            "USUARIO" -> cargarUsuarios(context)
            else -> cargarUsuarios(context)
        }
    }

    fun cargarUsuarios(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            try {
                val api = RetrofitClient.create(context)

                val usuarios = api.getTecnicos()

                bienesRegistrados.clear()

                usuarios.forEach { user ->
                    bienesRegistrados.add(
                        BienRegistrado(
                            etiqueta = user.nombre,
                            tipo = "Usuario",
                            descripcion = user.correo,
                            fechaAlta = "N/A",
                            ubicacion = user.area ?: "Sin área",
                            costo = "N/A",
                            estado = if (user.estatus) "Activo" else "Inactivo"
                        )
                    )
                }

                Log.d("API", "Usuarios cargados: ${usuarios.size}")

            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
            } finally {
                estaCargando = false
            }
        }
    }
}