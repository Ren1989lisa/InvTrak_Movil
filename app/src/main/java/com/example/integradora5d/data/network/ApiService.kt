package com.example.integradora5d.data.network

import com.example.integradora5d.data.model.BienRegistrado
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    // Endpoint para obtener el historial basado en la etiqueta del activo
    @GET("historial/{etiqueta}")
    suspend fun getHistorialByEtiqueta(@Path("etiqueta") etiqueta: String): List<BienRegistrado>
}