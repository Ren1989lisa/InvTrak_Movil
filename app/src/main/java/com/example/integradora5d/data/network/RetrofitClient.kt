package com.example.integradora5d.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
<<<<<<< Updated upstream
    // Usando tu IP proporcionada
    private const val BASE_URL = "http://192.168.105.179:8080/api/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
=======
    private const val BASE_URL = "http://10.0.2.2:8085/api/"
    private var retrofit: Retrofit? = null

    fun create(context: Context): ApiService {
        // Siempre creamos un cliente nuevo para asegurar que el AuthInterceptor
        // lea el token más reciente de las SharedPreferences
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()

        val instance = Retrofit.Builder()
>>>>>>> Stashed changes
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return instance.create(ApiService::class.java)
    }
}