package com.example.integradora5d.data.network

import com.example.integradora5d.data.model.LoginResponse
import com.example.integradora5d.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("/auth/login")
    suspend fun login(@Body credenciales: Map<String, String>): LoginResponse

    @GET("usuario/tecnicos")
    suspend fun getTecnicos(): List<User>
}