package com.example.thicki.data.api

import com.example.thicki.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("api/doctors")
    suspend fun getDoctors(): Response<List<Doctor>>

    @GET("api/doctors/{doctorId}/slots")
    suspend fun getDoctorTimeSlots(@Path("doctorId") doctorId: Int): Response<List<TimeSlot>>

    @GET("api/doctors/{doctorId}/appointments")
    suspend fun getDoctorAppointments(@Path("doctorId") doctorId: Int): Response<List<Booking>>

    @POST("api/bookings")
    suspend fun createBooking(@Body request: BookingRequest): Response<BookingResponse>

    @GET("api/patients/{patientId}/bookings")
    suspend fun getPatientBookings(@Path("patientId") patientId: Int): Response<PatientBookingsResponse>

    @DELETE("api/bookings/{bookingId}")
    suspend fun cancelBooking(@Path("bookingId") bookingId: Int): Response<SimpleResponse>

    @PUT("api/bookings/{bookingId}")
    suspend fun updateBooking(@Path("bookingId") bookingId: Int, @Body request: UpdateBookingRequest): Response<SimpleResponse>

    @POST("api/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<SimpleResponse>

    @POST("api/timeslots")
    suspend fun createTimeSlot(@Body request: CreateTimeSlotRequest): Response<SimpleResponse>

    @POST("api/medical-records")
    suspend fun saveMedicalRecord(@Body request: MedicalRecordRequest): Response<SimpleResponse>

    @POST("api/payments/init")
    suspend fun initializePayment(@Body request: PaymentUpdateRequest): Response<SimpleResponse>

    @GET("api/payments/verify/{appTransID}")
    suspend fun verifyPayment(@Path("appTransID") appTransID: String): Response<SimpleResponse>

    @GET("api/payments/check-status/{appTransId}")
    suspend fun checkPaymentStatus(@Path("appTransId") appTransId: String): Response<PaymentStatusResponse>

    @GET("api/payment-history/{patientId}")
    suspend fun getPaymentHistory(@Path("patientId") patientId: Int): Response<List<PaymentRecord>>

    // Đã sửa đổi để nhận UpdatePaymentRequest thay vì MapD
    @POST("api/payments/update-result")
    suspend fun updatePaymentResult(@Body request: UpdatePaymentRequest): Response<SimpleResponse>
}
