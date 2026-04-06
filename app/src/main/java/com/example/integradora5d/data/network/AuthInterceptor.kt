package com.example.integradora5d.data.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefs.getString("token", null)
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // No enviar token en login (ni endpoints públicos)
        val isAuthRequest = originalRequest.url.encodedPath.contains("auth/login")

        // Solo agregar token si es válido y no es login
        if (!token.isNullOrBlank() && !isAuthRequest) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}