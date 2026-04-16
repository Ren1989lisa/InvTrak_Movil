package com.example.integradora5d.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BienRegistrado(
    // El idOriginal es vital para la navegación y para el QR
    val idOriginal: Long,
    val etiqueta: String,
    val tipo: String,
    val descripcion: String,
    val fechaAlta: String,
    val ubicacion: String,
    val costo: String,
    val estado: String
) : Parcelable