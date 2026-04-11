package com.example.integradora5d.data.network

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefs.getString("token", null)
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val path = originalRequest.url.encodedPath.lowercase()

        val isPublicRoute = path.contains("auth/login") || path.contains("auth/reset-password")

        if (!token.isNullOrBlank() && !isPublicRoute) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
            Log.d("API_AUTH", "TOKEN APLICADO: $path")
        } else {
            Log.d("API_AUTH", "RUTA PÚBLICA (Sin Token): $path")
        }

        val response = chain.proceed(requestBuilder.build())

        if (isPublicRoute) {
            Log.d("API_AUTH", "Respuesta de Login: Código ${response.code}")
        }

        return response
    }
}