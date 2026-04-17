package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class BeanHistorialResponse(
    @SerializedName("id_historial")
    val idHistorial: Long,
    @SerializedName("estatus_anterior")
    val estatusAnterior: String?,
    @SerializedName("estatus_nuevo")
    val estatusNuevo: String?,
    @SerializedName("fecha_cambio")
    val fechaCambio: String?,
    val motivo: String?,
    val activo: HistorialActivo?,
    val usuario: HistorialUsuario?
)

data class HistorialActivo(
    @SerializedName("idActivo")
    val idActivo: Long,
    @SerializedName("etiquetaBien")
    val etiquetaBien: String?,
    @SerializedName("numeroSerie")
    val numeroSerie: String?,
    val descripcion: String?,
    val estatus: String?
)

data class HistorialUsuario(
    @SerializedName("idUsuario")
    val idUsuario: Long,
    val nombre: String?,
    val correo: String?
)
