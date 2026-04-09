package com.example.integradora5d.data.model

data class Reporte(
    val id_activo: Long,      // Debe coincidir con el campo en tu CreateReporteDTO
    val descripcion: String,
    val tipo_falla: String,   // Agregado para cumplir con tu modelo
    val estatus: String       // "ABIERTO", "EN_REVISION", etc.
)