package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class PerfilResponse(
    @SerializedName("idUsuario") val idUsuario: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("numeroEmpleado") val numeroEmpleado: String?,
    @SerializedName("area") val area: String?,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String?,
    @SerializedName("curp") val curp: String?,
    @SerializedName("estatus") val estatus: Boolean,
    @SerializedName("rol") val rol: String?
)
