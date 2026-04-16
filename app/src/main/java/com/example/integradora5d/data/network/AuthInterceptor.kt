package com.example.integradora5d.data.network

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    private val applicationContext = context.applicationContext
    private val prefs = applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        // Recuperamos y limpiamos el token
        val token = prefs.getString("token", null)?.trim()
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val path = originalRequest.url.encodedPath

        // Rutas que no requieren token
        val skipToken = path.contains("auth/login") ||
                path.contains("auth/forgot-password") ||
                path.contains("auth/reset-password")

        if (!token.isNullOrBlank() && !skipToken) {
            // header() reemplaza cualquier cabecera previa del mismo nombre
            requestBuilder.header("Authorization", "Bearer $token")
            Log.d("API_AUTH", "Enviando Token a: $path | Token: ${token.take(10)}...")
        } else {
            Log.d("API_AUTH", "Petición sin token (Ruta excluida o token nulo) a: $path")
        }

        val response = try {
            chain.proceed(requestBuilder.build())
        } catch (e: Exception) {
            Log.e("API_AUTH", "Fallo de conexión: ${e.message}")
            throw e
        }

        // Si el servidor responde 401, el token guardado ya no es válido
        if (response.code == 401) {
            Log.e("API_AUTH", "Sesión inválida (401) en $path. Redirigir al login si es necesario.")
        }

        return response
    }
}