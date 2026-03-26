package com.example.integradora5d.data.model

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("user")
    fun getUsers(): Call<List<User>>
}