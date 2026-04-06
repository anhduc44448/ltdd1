package com.example.thicki.data.model

import com.google.gson.annotations.SerializedName

data class UpdatePaymentRequest(
    @SerializedName("appTransId") val appTransId: String,
    @SerializedName("zpTransId") val zpTransId: String,
    @SerializedName("isSuccessful") val isSuccessful: Boolean
)
