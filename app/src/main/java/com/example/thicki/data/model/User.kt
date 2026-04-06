package com.example.thicki.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("userID") val userID: Int?,
    @SerializedName("doctorID") val doctorID: Int?, // Đảm bảo nhận đúng DoctorID
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("role") val role: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
)

data class LoginRequest(
    val identifier: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String = "patient"
)
