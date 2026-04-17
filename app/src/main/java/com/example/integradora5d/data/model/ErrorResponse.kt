package com.example.integradora5d.data.model

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String,
    @SerializedName("error") val error: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null
)