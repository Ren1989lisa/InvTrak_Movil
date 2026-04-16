package com.example.integradora5d.data.network

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    // Usamos applicationContext para evitar fugas de memoria
    private val prefs = context.applicationContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        // Obtenemos el token y aplicamos trim() para eliminar espacios accidentales
        val token = prefs.getString("token", null)?.trim()
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val path = originalRequest.url.encodedPath

        // Lista de exclusión más robusta
        val skipToken = path.contains("auth/login") ||
                path.contains("auth/forgot-password") ||
                path.contains("auth/register")

        if (!token.isNullOrBlank() && !skipToken) {
            // Seteamos la cabecera usando .header (que reemplaza) en lugar de .addHeader
            requestBuilder.header("Authorization", "Bearer $token")
            Log.d("API_AUTH", "Token enviado a: $path")
        } else {
            Log.d("API_AUTH", "Petición sin token a: $path (Token vacío: ${token.isNullOrBlank()})")
        }

        val response = try {
            chain.proceed(requestBuilder.build())
        } catch (e: Exception) {
            // Si la conexión falla físicamente, lanzamos una excepción limpia
            Log.e("API_AUTH", "Fallo físico de red: ${e.message}")
            throw e
        }

        // Manejo de la respuesta del servidor
        if (response.code == 401) {
            Log.e("API_AUTH", "ERROR 401: El servidor rechazó el token en $path. Bad Credentials.")
            // Aquí es donde Spring Security en tu backend está fallando.
        }

        return response
    }
}