package com.example.integradora5d.data.network

import com.example.integradora5d.data.model.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException

object ErrorHandler {
    
    fun parseHttpError(exception: HttpException): String {
        return try {
            val errorBody = exception.response()?.errorBody()?.string()
            if (errorBody != null) {
                val gson = Gson()
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.message
            } else {
                getDefaultErrorMessage(exception.code())
            }
        } catch (e: Exception) {
            getDefaultErrorMessage(exception.code())
        }
    }
    
    private fun getDefaultErrorMessage(code: Int): String {
        return when (code) {
            400 -> "Solicitud inválida"
            401 -> "No autorizado"
            403 -> "Sin permisos para esta acción"
            404 -> "Recurso no encontrado"
            500 -> "Error interno del servidor"
            502 -> "Error de conexión con el servidor"
            503 -> "Servicio no disponible"
            else -> "Error del servidor (código: $code)"
        }
    }
}