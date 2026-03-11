package com.example.integradora5d.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.integradora5d.data.model.BienRegistrado

class BienRegistradoViewModel : ViewModel() {

    var bienesRegistrados = mutableStateListOf<BienRegistrado>()
        private set

    init {
        cargarBienes()
    }

    private fun cargarBienes() {

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

        bienesRegistrados.add(
            BienRegistrado(
                etiqueta = "MOTHUTEZD1A4",
                tipo = "Monitor",
                descripcion = "Monitor ThinkVision ultimo modelo",
                fechaAlta = "15-5-25",
                ubicacion = "UTEZ D1 CA2",
                costo = "$2555",
                estado = "Resguardado"
            )
        )

    }
}