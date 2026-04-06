package com.example.thicki.ui.hospitals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thicki.data.model.Hospital
import kotlinx.coroutines.launch

class HospitalViewModel : ViewModel() {
    var hospitals by mutableStateOf<List<Hospital>>(emptyList())
    var filteredHospitals by mutableStateOf<List<Hospital>>(emptyList())
    var isLoading by mutableStateOf(false)
    var searchQuery by mutableStateOf("")

    init {
        fetchHospitals()
    }

    private fun fetchHospitals() {
        viewModelScope.launch {
            isLoading = true
            // Mock data for hospitals
            hospitals = listOf(
                Hospital(1, "Bệnh viện Việt Đức", "40 Tràng Thi, Hoàn Kiếm, Hà Nội"),
                Hospital(2, "Bệnh viện Bạch Mai", "78 Giải Phóng, Phương Mai, Đống Đa, Hà Nội"),
                Hospital(3, "Bệnh viện TW Quân đội 108", "1 Trần Hưng Đạo, Bạch Đằng, Hai Bà Trưng, Hà Nội"),
                Hospital(4, "Bệnh viện Chợ Rẫy", "201B Nguyễn Chí Thanh, Phường 12, Quận 5, Hồ Chí Minh")
            )
            filteredHospitals = hospitals
            isLoading = false
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        filteredHospitals = if (newQuery.isEmpty()) {
            hospitals
        } else {
            hospitals.filter { 
                it.name.contains(newQuery, ignoreCase = true) || 
                it.address.contains(newQuery, ignoreCase = true) 
            }
        }
    }
}
