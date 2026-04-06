package com.example.thicki.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thicki.data.api.ApiService
import com.example.thicki.data.model.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthViewModel : ViewModel() {
    private val serverIp = "192.168.2.12" // Đã cập nhật theo IP mới
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://$serverIp:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    var loginState by mutableStateOf<AuthState>(AuthState.Idle)
    var registerState by mutableStateOf<AuthState>(AuthState.Idle)
    var changePasswordMessage by mutableStateOf("")

    fun login(identifier: String, pass: String) {
        viewModelScope.launch {
            loginState = AuthState.Loading
            try {
                Log.d("AuthViewModel", "Gửi request đăng nhập cho: $identifier")
                val response = apiService.login(LoginRequest(identifier, pass))
                
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.success == true) {
                        loginState = AuthState.Success(body.user)
                    } else {
                        val msg = body?.message ?: "Đăng nhập thất bại"
                        loginState = AuthState.Error(msg)
                    }
                } else {
                    loginState = AuthState.Error("Lỗi hệ thống")
                }
            } catch (e: Exception) {
                loginState = AuthState.Error("Lỗi kết nối")
            }
        }
    }

    fun register(fullName: String, email: String, phone: String, pass: String) {
        viewModelScope.launch {
            registerState = AuthState.Loading
            try {
                val response = apiService.register(RegisterRequest(fullName, email, phone, pass))
                if (response.isSuccessful && response.body()?.success == true) {
                    registerState = AuthState.Success(response.body()?.user)
                } else {
                    registerState = AuthState.Error(response.body()?.message ?: "Đăng ký thất bại")
                }
            } catch (e: Exception) {
                registerState = AuthState.Error("Lỗi kết nối")
            }
        }
    }

    fun changePassword(userId: Int, oldPass: String, newPass: String) {
        viewModelScope.launch {
            try {
                val response = apiService.changePassword(ChangePasswordRequest(userId, oldPass, newPass))
                if (response.isSuccessful && response.body()?.success == true) {
                    changePasswordMessage = "Đổi mật khẩu thành công!"
                } else {
                    changePasswordMessage = response.body()?.message ?: "Thất bại"
                }
            } catch (e: Exception) {
                changePasswordMessage = "Lỗi kết nối"
            }
        }
    }

    fun logout() {
        loginState = AuthState.Idle
        registerState = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User?) : AuthState()
    data class Error(val message: String) : AuthState()
}
