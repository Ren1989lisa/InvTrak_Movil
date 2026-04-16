package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class BeanHistorialResponse(
    val id_historial: Long,
    val estatus_anterior: String?,
    val estatus_nuevo: String?,
    val motivo: String?,
    // CORREGIDO: fecha_cambio puede fallar en el parseo si el formato de LocalDateTime cambia
    val fecha_cambio: String?,
    val usuario: UsuarioSimple?
)

data class UsuarioSimple(
    @SerializedName("nombre")
    val nombre_completo: String?
)