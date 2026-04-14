package com.example.integradora5d.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integradora5d.data.network.RetrofitClient
import com.example.integradora5d.data.model.UpdateUsuarioRequest // <--- Asegúrate de importar tu DTO
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EditProfileViewModel : ViewModel() {
    var estaCargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var exito by mutableStateOf(false)

    var nombre by mutableStateOf("")
    var correo by mutableStateOf("")
    var area by mutableStateOf("")
    var curp by mutableStateOf("")
    var fechaNacimiento by mutableStateOf("")
    var rol by mutableStateOf("")
    var numeroEmpleado by mutableStateOf("")

    fun cargarDatos(context: Context) {
        viewModelScope.launch {
            estaCargando = true
            try {
                val api = RetrofitClient.create(context)
                val perfil = api.getPerfilPropio()

                nombre = perfil.nombre ?: ""
                correo = perfil.correo ?: ""
                area = perfil.area ?: ""
                curp = perfil.curp ?: ""
                rol = perfil.rol ?: ""
                numeroEmpleado = perfil.numeroEmpleado ?: ""
                fechaNacimiento = formatarFechaParaVista(perfil.fechaNacimiento ?: "")

            } catch (e: Exception) {
                mensajeError = "No se pudieron cargar los datos del servidor"
                Log.e("FETCH_ERROR", e.message ?: "")
            } finally {
                estaCargando = false
            }
        }
    }

    fun guardarCambios(context: Context, idUsuario: Long) {
        if (idUsuario <= 0L) {
            mensajeError = "Error interno: ID de sesión no encontrado"
            return
        }

        viewModelScope.launch {
            estaCargando = true
            mensajeError = null
            try {
                val api = RetrofitClient.create(context)
                val fechaFormateada = formatarFechaParaBackend(fechaNacimiento)


                val request = UpdateUsuarioRequest(
                    nombre = nombre,
                    correo = correo,
                    fechaNacimiento = fechaFormateada, // Enviamos yyyy-MM-dd
                    curp = curp,
                    area = area
                )

                val response = api.actualizarPerfil(idUsuario, request)

                if (response.isSuccessful) {
                    exito = true
                    Log.d("UPDATE_SUCCESS", "Perfil actualizado con éxito")
                } else {
                    mensajeError = "Error al guardar: ${response.code()}"
                }

            } catch (e: Exception) {
                mensajeError = "Error de conexión con el servidor"
                Log.e("UPDATE_EXCEPTION", e.message ?: "Desconocido")
            } finally {
                estaCargando = false
            }
        }
    }

    private fun formatarFechaParaVista(fecha: String) = try {
        val parts = fecha.split("-")
        "${parts[2]}/${parts[1]}/${parts[0]}"
    } catch (e: Exception) { fecha }

    private fun formatarFechaParaBackend(fecha: String) = try {
        val parts = fecha.split("/")
        "${parts[2]}-${parts[1]}-${parts[0]}"
    } catch (e: Exception) { fecha }
}