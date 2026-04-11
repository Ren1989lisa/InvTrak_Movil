package com.example.integradora5d.data.network

import com.example.integradora5d.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body credenciales: Map<String, String>): LoginResponse

    @GET("usuario/tecnicos")
    suspend fun getTecnicos(): List<User>

    @GET("bienes/{etiqueta}")
    suspend fun getBienByEtiqueta(@Path("etiqueta") etiqueta: String): BienRegistrado

    @Multipart
    @POST("reporte")
    suspend fun crearReporte(
        @Part("datos") datos: RequestBody,
        @Part archivos: List<MultipartBody.Part>? = null
    ): Response<Unit>

    @GET("producto")
    suspend fun getBienesReales(): List<BeanProductoResponse>

    @GET("usuario/{id}")
    suspend fun getPerfilUsuario(@Path("id") id: Long): PerfilResponse

    @GET("usuario/perfil")
    suspend fun getPerfilPropio(): PerfilResponse

    @GET("historial/{etiqueta}")
    suspend fun getHistorialByEtiqueta(@Path("etiqueta") etiqueta: String): List<BienRegistrado>

    @POST("auth/forgot-password")
    suspend fun solicitarRecuperacion(@Body body: Map<String, String>): Response<Map<String, String>>
}