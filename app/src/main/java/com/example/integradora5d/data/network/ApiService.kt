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

    @GET("producto")
    suspend fun getBienesReales(): List<BeanProductoResponse>

    @GET("activo")
    suspend fun getActivos(): List<ActivoCompleto>

    @GET("usuario/{id}")
    suspend fun getPerfilUsuario(@Path("id") id: Long): PerfilResponse

    @GET("usuario/perfil")
    suspend fun getPerfilPropio(): PerfilResponse

    @GET("historial/{etiqueta}")
    suspend fun getHistorialByEtiqueta(@Path("etiqueta") etiqueta: String): List<BienRegistrado>

    @POST("auth/forgot-password")
    suspend fun solicitarRecuperacion(@Body request: ForgotPasswordRequest): Response<Unit>

    @PUT("usuario/{id}")
    suspend fun actualizarPerfil(
        @Path("id") id: Long,
        @Body datos: UpdateUsuarioRequest
    ): Response<Unit>

    @Multipart
    @POST("reporte")
    suspend fun crearReporte(
        @Part("datos") datos: RequestBody,
        @Part archivos: List<MultipartBody.Part>? = null
    ): Response<ReporteCreado>

    @GET("historial/activo/{activoId}")
    suspend fun getHistorialByActivoId(@Path("activoId") activoId: Long): List<BeanHistorialResponse>

    // --- RESGUARDO ---
    @GET("resguardo")
    suspend fun getResguardos(): List<ResguardoResponse>

    @GET("resguardo/verificar/{activoId}")
    suspend fun verificarResguardoQR(@Path("activoId") activoId: Long): ResguardoResponse

    @POST("resguardo/{id}/solicitar-devolucion")
    suspend fun solicitarDevolucion(@Path("id") id: Long): Response<ResguardoResponse>

    @Multipart
    @POST("resguardo/confirmar")
    suspend fun confirmarResguardo(
        @Part("datos") datos: RequestBody,
        @Part fotos: List<MultipartBody.Part>? = null
    ): Response<ResguardoResponse>

    @Multipart
    @POST("resguardo/devolver")
    suspend fun devolverResguardo(
        @Part("datos") datos: RequestBody,
        @Part fotos: List<MultipartBody.Part>
    ): Response<ResguardoResponse>

    // --- MANTENIMIENTO (TÉCNICO) ---
    @GET("mantenimiento/tecnico/{tecnicoId}")
    suspend fun getMantenimientosByTecnico(@Path("tecnicoId") tecnicoId: Long): List<MantenimientoResponse>

    @GET("mantenimiento/{id}")
    suspend fun getMantenimientoById(@Path("id") id: Long): MantenimientoResponse

    @Multipart
    @PUT("mantenimiento/atender")
    suspend fun atenderMantenimiento(
        @Part("datos") datos: RequestBody,
        @Part fotos: List<MultipartBody.Part>? = null
    ): Response<MantenimientoResponse>

    @PUT("mantenimiento/cerrar")
    suspend fun cerrarMantenimiento(@Body body: Map<String, Any>): Response<MantenimientoResponse>
}