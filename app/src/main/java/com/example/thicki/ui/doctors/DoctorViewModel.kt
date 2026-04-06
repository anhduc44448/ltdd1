package com.example.thicki.ui.doctors

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thicki.data.api.ApiService
import com.example.thicki.data.model.*
import com.example.thicki.payment.Api.CreateOrder
import com.example.thicki.payment.Helper.Helpers
import com.example.thicki.ui.notifications.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener

class DoctorViewModel(application: Application) : AndroidViewModel(application) {
    private val serverIp = "192.168.2.12"
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://$serverIp:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)
    private val notificationHelper = NotificationHelper(application)

    var doctors by mutableStateOf<List<Doctor>>(emptyList())
    var filteredDoctors by mutableStateOf<List<Doctor>>(emptyList())
    var isLoading by mutableStateOf(false)
    var isProcessingPayment by mutableStateOf(false)
    var searchQuery by mutableStateOf("")

    var timeSlots by mutableStateOf<List<TimeSlot>>(emptyList())
    var appointments by mutableStateOf<List<Booking>>(emptyList())
    var patientBookings by mutableStateOf<List<Booking>>(emptyList())
    var paymentHistory by mutableStateOf<List<PaymentRecord>>(emptyList())
    var lastBooking by mutableStateOf<Booking?>(null)
    
    var notificationCount by mutableIntStateOf(0)

    init {
        fetchDoctors()
    }

    fun getPaymentHistory(patientId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getPaymentHistory(patientId)
                if (response.isSuccessful) {
                    paymentHistory = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("DoctorViewModel", "Error fetching payment history: ${e.message}")
            }
        }
    }

    fun resetPaymentStatus() {
        isProcessingPayment = false
    }

    private fun fetchDoctors() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = apiService.getDoctors()
                if (response.isSuccessful) {
                    doctors = response.body() ?: emptyList()
                    filteredDoctors = doctors
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchTimeSlots(doctorId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getDoctorTimeSlots(doctorId)
                if (response.isSuccessful) {
                    timeSlots = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun processPaymentAndBooking(
        context: Context,
        patientId: Int,
        doctor: Doctor,
        slot: TimeSlot,
        onSuccess: (Booking) -> Unit,
        onError: (String) -> Unit
    ) {
        if (isProcessingPayment) return
        
        if (patientId <= 0) {
            onError("Lỗi: Bạn chưa đăng nhập.")
            return
        }

        viewModelScope.launch {
            val timeoutJob = launch {
                delay(60000)
                if (isProcessingPayment) {
                    isProcessingPayment = false
                }
            }

            try {
                isProcessingPayment = true
                
                val bookingReq = BookingRequest(patientId, doctor.doctorID, slot.slotID)
                val bookingRes = apiService.createBooking(bookingReq)
                
                if (bookingRes.isSuccessful && bookingRes.body()?.success == true) {
                    val bookingId = bookingRes.body()?.bookingID ?: 0
                    val amount = doctor.basePrice.toInt().toString()
                    val generatedAppTransId = Helpers.getAppTransId()

                    val data = withContext(Dispatchers.IO) {
                        val orderApi = CreateOrder()
                        orderApi.createOrder(amount, generatedAppTransId)
                    }
                    
                    if (data == null) {
                        isProcessingPayment = false
                        timeoutJob.cancel()
                        onError("Lỗi: ZaloPay không phản hồi.")
                        return@launch
                    }

                    val code = if (data.has("return_code")) data.optInt("return_code") else data.optInt("returncode", -1)

                    if (code == 1) {
                        val token = if (data.has("zp_trans_token")) data.getString("zp_trans_token") else data.getString("zptranstoken")

                        apiService.initializePayment(PaymentUpdateRequest(
                            bookingID = bookingId,
                            appTransID = generatedAppTransId,
                            amount = doctor.basePrice,
                            zpTransToken = token
                        ))

                        ZaloPaySDK.getInstance().payOrder(
                            context as ComponentActivity,
                            token,
                            "thickipay://app",
                            object : PayOrderListener {
                                override fun onPaymentSucceeded(p0: String?, p1: String?, p2: String?) {
                                    val finalAppTransId = p2 ?: generatedAppTransId
                                    Log.d("ZALOPAY_FLOW", "✅ Thanh toán thành công!")

                                    viewModelScope.launch {
                                        try {
                                            val req = UpdatePaymentRequest(
                                                appTransId = finalAppTransId,
                                                zpTransId = p0 ?: "",
                                                isSuccessful = true
                                            )
                                            apiService.updatePaymentResult(req)
                                        } catch (e: Exception) {
                                            Log.e("ZALOPAY_FLOW", "❌ Lỗi: ${e.message}")
                                        }
                                    }

                                    isProcessingPayment = false
                                    timeoutJob.cancel()
                                    lastBooking = Booking(
                                        bookingID = bookingId, patientID = patientId,
                                        doctorID = doctor.doctorID, slotID = slot.slotID,
                                        status = "confirmed", createdAt = "",
                                        doctorName = doctor.fullName, slotDate = slot.slotDate, startTime = slot.startTime
                                    )
                                    
                                    // HIỆN THÔNG BÁO HỆ THỐNG KHI ĐẶT LỊCH VÀ THANH TOÁN THÀNH CÔNG
                                    notificationHelper.showNotification(
                                        "Đặt lịch thành công",
                                        "Bạn đã đặt lịch với bác sĩ ${doctor.fullName} vào ${slot.startTime} ${slot.slotDate} thành công!"
                                    )
                                    notificationCount++

                                    onSuccess(lastBooking!!)
                                    fetchTimeSlots(doctor.doctorID)
                                }
                                override fun onPaymentCanceled(p0: String?, p1: String?) {
                                    isProcessingPayment = false
                                    timeoutJob.cancel()
                                    onError("Thanh toán bị hủy")
                                }
                                override fun onPaymentError(p0: ZaloPayError?, p1: String?, p2: String?) {
                                    isProcessingPayment = false
                                    timeoutJob.cancel()
                                    onError("Lỗi thanh toán ZaloPay")
                                }
                            }
                        )
                    } else {
                        isProcessingPayment = false
                        timeoutJob.cancel()
                        onError("Lỗi tạo đơn ZaloPay")
                    }
                }
            } catch (e: Exception) {
                isProcessingPayment = false
                timeoutJob.cancel()
                onError("Lỗi hệ thống: ${e.message}")
            }
        }
    }

    fun fetchAppointments(doctorId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getDoctorAppointments(doctorId)
                if (response.isSuccessful) {
                    appointments = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchPatientBookings(patientId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.getPatientBookings(patientId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val newList = response.body()?.data ?: emptyList()
                    
                    if (patientBookings.isNotEmpty()) {
                        newList.forEach { newBooking ->
                            val oldBooking = patientBookings.find { it.bookingID == newBooking.bookingID }
                            if (newBooking.diagnosis != null && oldBooking?.diagnosis == null) {
                                notificationCount++
                                notificationHelper.showNotification(
                                    "Kết quả khám bệnh mới",
                                    "Bác sĩ ${newBooking.doctorName} đã cập nhật hồ sơ y tế cho bạn."
                                )
                            }
                        }
                    }
                    patientBookings = newList
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearNotifications() {
        notificationCount = 0
    }

    fun createTimeSlot(doctorId: Int, date: String, start: String, end: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val request = CreateTimeSlotRequest(doctorId, date, start, end)
                val response = apiService.createTimeSlot(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    onResult(true, "Tạo thành công!")
                    fetchTimeSlots(doctorId)
                } else {
                    onResult(false, response.body()?.message ?: "Thất bại")
                }
            } catch (e: Exception) {
                onResult(false, "Lỗi kết nối")
            }
        }
    }

    fun cancelBooking(bookingId: Int, patientId: Int, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                val response = apiService.cancelBooking(bookingId)
                if (response.isSuccessful && response.body()?.success == true) {
                    fetchPatientBookings(patientId)
                    onSuccess?.invoke()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateBooking(bookingId: Int, newSlotId: Int, patientId: Int) {
        viewModelScope.launch {
            try {
                val response = apiService.updateBooking(bookingId, UpdateBookingRequest(newSlotId))
                if (response.isSuccessful && response.body()?.success == true) {
                    fetchPatientBookings(patientId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveMedicalRecord(bookingId: Int, diagnosis: String, prescription: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val request = MedicalRecordRequest(bookingId, diagnosis, prescription)
                val response = apiService.saveMedicalRecord(request)
                if (response.isSuccessful && response.body()?.success == true) {
                    onResult(true, "Lưu hồ sơ thành công!")
                } else {
                    onResult(false, response.body()?.message ?: "Lưu hồ sơ thất bại")
                }
            } catch (e: Exception) {
                onResult(false, "Lỗi kết nối: ${e.message}")
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        filteredDoctors = if (newQuery.isEmpty()) {
            doctors
        } else {
            doctors.filter { 
                it.fullName.contains(newQuery, ignoreCase = true) || 
                it.specialtyName.contains(newQuery, ignoreCase = true) 
            }
        }
    }

    fun sortDoctors(by: String) {
        filteredDoctors = when (by) {
            "Kinh nghiệm" -> filteredDoctors.sortedByDescending { it.experienceYears }
            "Giá: Thấp đến Cao" -> filteredDoctors.sortedBy { it.basePrice }
            "Đánh giá" -> filteredDoctors.sortedByDescending { it.rating }
            else -> filteredDoctors
        }
    }
}
