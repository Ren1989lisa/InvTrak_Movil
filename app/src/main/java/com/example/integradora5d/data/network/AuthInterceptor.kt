package com.example.integradora5d.data.network

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    // Unificamos el nombre a "user_prefs"
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefs.getString("token", null)
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Verificamos si es una ruta que NO necesita token
        val path = originalRequest.url.encodedPath
        val isAuthRequest = path.contains("auth/login")

        if (!token.isNullOrBlank() && !isAuthRequest) {
            // Agregamos el token. Asegúrate de que "Bearer " tenga el espacio.
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("API_AUTH", "Enviando Token en: $path")
        } else {
            Log.d("API_AUTH", "Petición sin token a: $path")
        }

        return chain.proceed(requestBuilder.build())
    }
}