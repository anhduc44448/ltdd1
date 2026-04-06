package com.example.thicki.data

data class User(
    val userID: Int? = null,
    val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
    val role: String = "patient"
)

