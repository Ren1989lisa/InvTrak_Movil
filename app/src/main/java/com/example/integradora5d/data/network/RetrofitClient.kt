package com.example.integradora5d.data.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // El backend expone sus endpoints REST bajo /api/*
    private const val BASE_URL = "http://192.168.0.24:8085/api/"

    fun create(context: Context): ApiService {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
        return buildRetrofit(client)
    }

    fun createPublic(): ApiService {
        val client = OkHttpClient.Builder().build()
        return buildRetrofit(client)
    }

    private fun buildRetrofit(client: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
