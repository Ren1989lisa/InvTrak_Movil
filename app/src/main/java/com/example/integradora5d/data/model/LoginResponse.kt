package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("tokenType") val tokenType: String,
    @SerializedName("primerAcceso") val primerAcceso: Boolean,
    @SerializedName("roles") val roles: List<String>,
    @SerializedName("idUsuario") val idUsuario: Long
)