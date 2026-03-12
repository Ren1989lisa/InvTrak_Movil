    package com.example.integradora5d.data.model

    import java.io.Serializable

    data class BienRegistrado(
        val etiqueta: String,
        val tipo: String,
        val descripcion: String,
        val fechaAlta: String,
        val ubicacion: String,
        val costo: String,
        val estado: String
    ): Serializable