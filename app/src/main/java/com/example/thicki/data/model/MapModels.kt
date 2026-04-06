package com.example.thicki.data.model

import java.io.Serializable

data class Place(
    val title: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
) : Serializable

data class UserMap(
    val title: String,
    val places: List<Place>
) : Serializable
