package com.example.thicki.ui.doctors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.Booking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientAppointmentsScreen(
    patientId: Int,
    viewModel: DoctorViewModel,
    onBack: () -> Unit,
    onAppointmentClick: (Booking) -> Unit
) {
    val appointments = viewModel.patientBookings
    val primaryGreen = Color(0xFF00A67E)

    LaunchedEffect(patientId) {
        viewModel.fetchPatientBookings(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch khám của tôi", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (appointments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Bạn chưa có lịch hẹn nào.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF8F9FA)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(appointments) { booking ->
                    val displayDate = if (booking.slotDate.contains("T")) booking.slotDate.split("T")[0] else booking.slotDate
                    val displayTime = if (booking.startTime.length >= 5) booking.startTime.substring(0, 5) else booking.startTime

                    // Xác định nội dung và màu sắc trạng thái
                    val (statusText, statusColor) = when {
                        booking.status == "completed" || booking.diagnosis != null -> "Hoàn thành" to primaryGreen
                        booking.status == "confirmed" -> "Đã xác nhận" to primaryGreen
                        else -> "Đã hủy" to Color.Red
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAppointmentClick(booking) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier.size(48.dp).background(Color(0xFFE0F2F1), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = primaryGreen, modifier = Modifier.size(28.dp))
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column {
                                Text(text = "Bác sĩ: ${booking.doctorName}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Thời gian: $displayTime - $displayDate", color = Color.Gray, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Trạng thái: $statusText",
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
