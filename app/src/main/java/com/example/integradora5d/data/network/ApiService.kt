package com.example.integradora5d.data.network

import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("login")
    suspend fun login(@Body credenciales: Map<String, String>): LoginResponse

    @GET("bienes")
    suspend fun getTodosLosBienes(): List<BienRegistrado>

    @GET("bienes/usuario/{id}")
    suspend fun getBienesPorUsuario(@Path("id") userId: Int): List<BienRegistrado>

    @GET("bienes/mantenimiento")
    suspend fun getBienesParaTecnico(): List<BienRegistrado>
}