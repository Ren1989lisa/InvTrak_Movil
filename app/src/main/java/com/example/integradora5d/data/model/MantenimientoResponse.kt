package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class MantenimientoResponse(
    @SerializedName("id_mantenimiento") val idMantenimiento: Long,
    val diagnostico: String?,
    val accionesRealizadas: String?,
    val piezasUtilizadas: String?,
    @SerializedName("fecha_inicio") val fechaInicio: String?,
    @SerializedName("fecha_fin") val fechaFin: String?,
    val estatus: String?,
    val tipo: String?,
    val prioridad: PrioridadResponse?,
    val reporte: ReporteMantenimiento?,
    val tecnico: TecnicoMantenimiento?
)

data class PrioridadResponse(
    @SerializedName("id_prioridad") val idPrioridad: Long,
    val nombre: String?,
    val nivel: String?
)

data class ReporteMantenimiento(
    val idReporte: Long,
    val descripcion: String?,
    val activo: ActivoMantenimiento?
)

data class ActivoMantenimiento(
    val idActivo: Long,
    val etiquetaBien: String?,
    val numeroSerie: String?,
    val descripcion: String?,
    val estatus: String?
)

data class TecnicoMantenimiento(
    val idUsuario: Long,
    val nombre: String?,
    val correo: String?
)
