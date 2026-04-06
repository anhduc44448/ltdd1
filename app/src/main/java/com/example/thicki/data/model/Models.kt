package com.example.thicki.data.model

import com.google.gson.annotations.SerializedName

data class Specialty(
    @SerializedName("SpecialtyID") val specialtyID: Int,
    @SerializedName("Name") val name: String
)

data class Doctor(
    @SerializedName("doctorID") val doctorID: Int,
    @SerializedName("userID") val userID: Int,
    @SerializedName("specialtyID") val specialtyID: Int,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("address") val address: String? = null,
    @SerializedName("specialtyName") val specialtyName: String,
    @SerializedName("experienceYears") val experienceYears: Int,
    @SerializedName("basePrice") val basePrice: Double,
    val rating: Float = 4.8f,
    val imageUrl: String? = null
)

data class TimeSlot(
    @SerializedName("SlotID") val slotID: Int,
    @SerializedName("DoctorID") val doctorID: Int,
    @SerializedName("SlotDate") val slotDate: String,
    @SerializedName("StartTime") val startTime: String,
    @SerializedName("EndTime") val endTime: String,
    @SerializedName("IsActive") val isActive: Int,
    @SerializedName("isBooked") val isBooked: Boolean = false
)

data class CreateTimeSlotRequest(
    val doctorID: Int,
    val slotDate: String,
    val startTime: String,
    val endTime: String
)

data class MedicalRecordRequest(
    val bookingID: Int,
    val diagnosis: String,
    val prescription: String?
)

data class Booking(
    @SerializedName("BookingID") val bookingID: Int,
    @SerializedName("PatientID") val patientID: Int,
    @SerializedName("DoctorID") val doctorID: Int,
    @SerializedName("SlotID") val slotID: Int,
    @SerializedName("Status") val status: String,
    @SerializedName("CreatedAt") val createdAt: String,
    @SerializedName("PatientName") val patientName: String? = null,
    @SerializedName("DoctorName") val doctorName: String? = null,
    @SerializedName("SlotDate") val slotDate: String,
    @SerializedName("StartTime") val startTime: String,
    @SerializedName("EndTime") val endTime: String? = null,
    @SerializedName("Diagnosis") val diagnosis: String? = null,
    @SerializedName("Prescription") val prescription: String? = null,
    @SerializedName("UpdatedDate") val updatedDate: String? = null,
    @SerializedName("SpecialtyName") val specialtyName: String? = null
)

data class PatientBookingsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<Booking>
)

data class BookingRequest(
    val patientID: Int,
    val doctorID: Int,
    val slotID: Int
)

data class UpdateBookingRequest(
    val slotID: Int
)

data class BookingResponse(
    val success: Boolean,
    val message: String,
    val bookingID: Int? = null
)

data class ChangePasswordRequest(
    val userID: Int,
    val oldPassword: String,
    val newPassword: String
)

// SỬA: Cho phép message null để không bị lỗi parse JSON
data class SimpleResponse(
    val success: Boolean,
    val message: String? = null 
)

data class Hospital(
    val id: Int,
    val name: String,
    val address: String,
    val imageUrl: String? = null
)

data class MedicalService(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String
)

data class Medication(
    val id: Int,
    val name: String,
    val price: Double,
    val manufacturer: String,
    val imageUrl: String? = null
)

data class PaymentUpdateRequest(
    val bookingID: Int,
    val appTransID: String,
    val amount: Double,
    val zpTransToken: String? = null
)
