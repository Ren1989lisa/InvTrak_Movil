package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class BeanProductoResponse(
    @SerializedName("id_producto") val id_producto: Long,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("descripcion") val descripcion: String?,
    @SerializedName("estatus") val estatus: String?
)