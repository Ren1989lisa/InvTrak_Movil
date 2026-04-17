package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class ChecklistConfirmacion(
    @SerializedName("idResguardo") val idResguardo: Long,
    @SerializedName("enciende") val enciende: String,
    @SerializedName("pantallaFunciona") val pantallaFunciona: String,
    @SerializedName("tieneCargador") val tieneCargador: String,
    @SerializedName("danios") val danios: String? = null,
    @SerializedName("observaciones") val observaciones: String? = null
)

data class ValidacionQRResponse(
    val valido: Boolean,
    val mensaje: String,
    val resguardo: ResguardoResponse?
)