package com.example.integradora5d.data.model

data class User(
    val idUsuario: Long,
    val nombre: String,
    val correo: String,
    val area: String?,
    val estatus: Boolean
)