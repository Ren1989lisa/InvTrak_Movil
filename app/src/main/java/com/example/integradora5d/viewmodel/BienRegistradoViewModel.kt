package com.example.integradora5d.viewmodel

<<<<<<< Updated upstream
import androidx.compose.runtime.mutableStateListOf
=======
import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
>>>>>>> Stashed changes
import androidx.lifecycle.ViewModel
import com.example.integradora5d.data.model.BienRegistrado

class BienRegistradoViewModel : ViewModel() {

    var bienesRegistrados = mutableStateListOf<BienRegistrado>()
        private set

<<<<<<< Updated upstream
    init {
        cargarBienes()
    }

    private fun cargarBienes() {

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

=======
    var estaCargando by mutableStateOf(false)
        private set

    fun cargarDatosSegunRol(context: Context, userId: Int, userRole: String) {
        // Log para depurar qué rol está llegando
        Log.d("VIEWMODEL", "Cargando datos para el rol: $userRole")

        when (userRole) {
            "ADMIN", "USER" -> cargarBienes(context)
            "TECNICO" -> cargarUsuarios(context)
            else -> cargarBienes(context)
        }
    }

    fun cargarBienes(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            try {
                val api = RetrofitClient.create(context)

                // Llamamos al controlador de Spring: ProductoController @GetMapping
                val respuesta = api.getBienesReales()

                bienesRegistrados.clear()

                // Mapeamos los BeanProducto que vienen de Java a tu modelo BienRegistrado de Android
                respuesta.forEach { producto ->
                    bienesRegistrados.add(
                        BienRegistrado(
                            etiqueta = producto.id_producto.toString(), // O el campo que uses como ID
                            tipo = "Producto",
                            descripcion = producto.nombre ?: "Sin nombre",
                            fechaAlta = "N/A",
                            ubicacion = "Almacén", // O el campo correspondiente si existe
                            costo = "$${producto.precio ?: 0}",
                            estado = "Disponible"
                        )
                    )
                }

                Log.d("API", "Bienes cargados desde /api/producto: ${respuesta.size}")

            } catch (e: Exception) {
                Log.e("API", "Error al cargar bienes: ${e.message}")
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
            } catch (e: Exception) {
                Log.e("API", "Error al cargar usuarios: ${e.message}")
            } finally {
                estaCargando = false
            }
        }
>>>>>>> Stashed changes
    }
}