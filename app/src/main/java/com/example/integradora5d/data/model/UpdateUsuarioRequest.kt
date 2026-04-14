package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class UpdateUsuarioRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String, // Formato yyyy-MM-dd
    @SerializedName("curp") val curp: String,
    @SerializedName("area") val area: String
)