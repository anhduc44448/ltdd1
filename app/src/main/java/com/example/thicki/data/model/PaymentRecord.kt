package com.example.thicki.data.model

import com.google.gson.annotations.SerializedName

data class PaymentRecord(
    @SerializedName("PaymentID") val paymentID: Int,
    @SerializedName("BookingID") val bookingID: Int,
    @SerializedName("Amount") val amount: Double,
    @SerializedName("AppTransID") val appTransID: String,
    @SerializedName("IsSuccessful") val isSuccessful: Int,
    @SerializedName("PaymentDate") val paymentDate: String?,
    @SerializedName("SlotDate") val slotDate: String,
    @SerializedName("StartTime") val startTime: String,
    @SerializedName("DoctorName") val doctorName: String
)
