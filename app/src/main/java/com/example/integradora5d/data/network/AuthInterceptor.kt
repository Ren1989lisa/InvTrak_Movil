package com.example.integradora5d.data.network

import android.content.Context
import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {

        val token = prefs.getString("token", null)

        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $token")
            Log.d("AUTH", "Token enviado: $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}