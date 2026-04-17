package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.model.ActivoCompleto
import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.data.network.RetrofitClient
import kotlinx.coroutines.launch

class BienRegistradoViewModel : ViewModel() {

    var bienesRegistrados = mutableStateListOf<BienRegistrado>()
        private set

    var activosCompletos = mutableStateListOf<ActivoCompleto>()
        private set

    var estaCargando by mutableStateOf(false)
        private set

    val campusOptions: List<String> get() =
        activosCompletos.map { it.campus }.filter { it.isNotBlank() }.distinct().sorted()

    fun edificioOptions(campus: String): List<String> =
        activosCompletos.filter { it.campus == campus }
            .map { it.edificio }.filter { it.isNotBlank() }.distinct().sorted()

    fun aulaOptions(campus: String, edificio: String): List<String> =
        activosCompletos.filter { it.campus == campus && it.edificio == edificio }
            .map { it.aulaName }.filter { it.isNotBlank() }.distinct().sorted()

    fun cargarDatosSegunRol(context: Context, userRole: String) {
        when (userRole.uppercase()) {
            "ADMIN" -> cargarTodosLosActivos(context)
            else -> cargarMisActivos(context)
        }
    }

    fun cargarTodosLosActivos(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            try {
                val api = RetrofitClient.create(context)
                val respuesta = api.getActivos() // Obtener TODOS los activos

                activosCompletos.clear()
                activosCompletos.addAll(respuesta)

                val listaMapeada = respuesta.map { activo ->
                    BienRegistrado(
                        idOriginal = activo.idActivo,
                        etiqueta = activo.etiquetaBien ?: "Sin etiqueta",
                        tipo = activo.productoNombre.ifBlank { "Activo" },
                        descripcion = activo.descripcion ?: "Sin descripción",
                        fechaAlta = activo.fechaAlta ?: "N/A",
                        ubicacion = activo.ubicacionCompleta.ifBlank { "Sin ubicación" },
                        costo = activo.costo?.let { "$${"%.2f".format(it)}" } ?: "N/A",
                        estado = activo.estatus ?: "DISPONIBLE"
                    )
                }
                bienesRegistrados.clear()
                bienesRegistrados.addAll(listaMapeada)
                
                Log.d("BienVM", "Cargados ${bienesRegistrados.size} activos para admin")
            } catch (e: Exception) {
                Log.e("BienVM", "Error en cargarTodosLosActivos: ${e.message}")
            } finally {
                estaCargando = false
            }
        }
    }

    fun cargarMisActivos(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            try {
                val api = RetrofitClient.create(context)
                val respuesta = api.getMisActivos()

                activosCompletos.clear()
                activosCompletos.addAll(respuesta)

                val listaMapeada = respuesta.map { activo ->
                    BienRegistrado(
                        idOriginal = activo.idActivo,
                        etiqueta = activo.etiquetaBien ?: "Sin etiqueta",
                        tipo = activo.productoNombre.ifBlank { "Activo" },
                        descripcion = activo.descripcion ?: "Sin descripción",
                        fechaAlta = activo.fechaAlta ?: "N/A",
                        ubicacion = activo.ubicacionCompleta.ifBlank { "Sin ubicación" },
                        costo = activo.costo?.let { "$${"%.2f".format(it)}" } ?: "N/A",
                        estado = activo.estatus ?: "DISPONIBLE"
                    )
                }
                bienesRegistrados.clear()
                bienesRegistrados.addAll(listaMapeada)
            } catch (e: Exception) {
                Log.e("BienVM", "Error en cargarMisActivos: ${e.message}")
            } finally {
                estaCargando = false
            }
        }
    }

    // Mantener para compatibilidad con BienDetalleScreen
    fun cargarActivos(context: Context) = cargarMisActivos(context)
    fun cargarBienes(context: Context) = cargarMisActivos(context)
}
