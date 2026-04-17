package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class UpdateUsuarioRequest(
    @SerializedName("nombre") val nombre: String? = null,
    @SerializedName("correo") val correo: String? = null,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String? = null,
    @SerializedName("curp") val curp: String? = null,
    @SerializedName("area") val area: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("primerAcceso") val primerAcceso: Boolean? = null,
    @SerializedName("tokenDispositivo") val tokenDispositivo: String? = null
)