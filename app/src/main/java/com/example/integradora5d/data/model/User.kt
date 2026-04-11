package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("idUsuario")
    val idUsuario: Long? = null,

    val nombre: String? = null,
    val correo: String? = null,

    // El password normalmente no lo recibes del servidor por seguridad (WRITE_ONLY en Java)
    val password: String? = null,

    val fechaNacimiento: String? = null, // En Kotlin/Retrofit es más fácil manejarlo como String inicialmente
    val curp: String? = null,
    val numeroEmpleado: String? = null,
    val area: String? = null,
    val estatus: Boolean = true,
    val primerAcceso: Boolean = true,
    val tokenDispositivo: String? = null
)