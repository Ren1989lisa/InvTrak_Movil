package com.example.integradora5d.data.network

<<<<<<< Updated upstream
import com.example.integradora5d.data.model.BienRegistrado
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    // Endpoint para obtener el historial basado en la etiqueta del activo
    @GET("historial/{etiqueta}")
    suspend fun getHistorialByEtiqueta(@Path("etiqueta") etiqueta: String): List<BienRegistrado>
=======
import com.example.integradora5d.data.model.BeanProductoResponse
import com.example.integradora5d.data.model.BienRegistrado
import com.example.integradora5d.data.model.LoginResponse
import com.example.integradora5d.data.model.PerfilResponse
import com.example.integradora5d.data.model.Reporte
import com.example.integradora5d.data.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

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

>>>>>>> Stashed changes
}