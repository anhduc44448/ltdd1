package com.example.thicki.ui.pharmacy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thicki.data.model.Medication
import kotlinx.coroutines.launch

class PharmacyViewModel : ViewModel() {
    var medications by mutableStateOf<List<Medication>>(emptyList())
    var filteredMedications by mutableStateOf<List<Medication>>(emptyList())
    var isLoading by mutableStateOf(false)
    var searchQuery by mutableStateOf("")

    init {
        fetchMedications()
    }

    private fun fetchMedications() {
        viewModelScope.launch {
            isLoading = true
            // Mock data for medications
            medications = listOf(
                Medication(1, "Panadol Extra", 45000.0, "GSK"),
                Medication(2, "Decolgen Forte", 30000.0, "United Pharma"),
                Medication(3, "Berberin", 15000.0, "Traphaco"),
                Medication(4, "Amoxicillin 500mg", 65000.0, "Dược Hậu Giang"),
                Medication(5, "Vitamin C 500mg", 25000.0, "Domesco")
            )
            filteredMedications = medications
            isLoading = false
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        filteredMedications = if (newQuery.isEmpty()) {
            medications
        } else {
            medications.filter { 
                it.name.contains(newQuery, ignoreCase = true) || 
                it.manufacturer.contains(newQuery, ignoreCase = true) 
            }
        }
    }

    fun sortMedications(by: String) {
        filteredMedications = when (by) {
            "Giá: Thấp đến Cao" -> filteredMedications.sortedBy { it.price }
            "Giá: Cao đến Thấp" -> filteredMedications.sortedByDescending { it.price }
            "Tên: A-Z" -> filteredMedications.sortedBy { it.name }
            else -> filteredMedications
        }
    }
}
