package com.example.thicki.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.Booking
import com.example.thicki.ui.doctors.DoctorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(
    patientId: Int,
    viewModel: DoctorViewModel,
    onBack: () -> Unit,
    onNotificationClick: (Booking) -> Unit // Thêm callback để điều hướng
) {
    val appointments = viewModel.patientBookings
    val primaryGreen = Color(0xFF00A67E)

    LaunchedEffect(patientId) {
        viewModel.fetchPatientBookings(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            if (appointments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có thông báo nào.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(appointments) { booking ->
                        // Xử lý định dạng ngày: chỉ lấy phần YYYY-MM-DD
                        val displayDate = if (booking.slotDate.contains("T")) {
                            booking.slotDate.split("T")[0]
                        } else {
                            booking.slotDate
                        }

                        // Định dạng giờ: 09:00:00 -> 09:00
                        val displayTime = if (booking.startTime.length >= 5) {
                            booking.startTime.substring(0, 5)
                        } else {
                            booking.startTime
                        }

                        // Nếu có chẩn đoán -> Hiện thông báo kết quả khám
                        if (booking.diagnosis != null) {
                            NotificationItem(
                                title = "Kết quả khám bệnh mới",
                                message = "Bác sĩ ${booking.doctorName} đã cập nhật hồ sơ y tế và đơn thuốc cho bạn. Nhấn để xem chi tiết.",
                                icon = Icons.Default.AssignmentTurnedIn,
                                iconColor = Color(0xFF2196F3), // Màu xanh dương cho kết quả
                                onClick = { onNotificationClick(booking) }
                            )
                        }

                        // Thông báo nhắc nhở lịch khám
                        NotificationItem(
                            title = "Nhắc nhở lịch khám",
                            message = "Bạn có lịch hẹn với bác sĩ ${booking.doctorName} vào lúc $displayTime ngày $displayDate.",
                            icon = Icons.Default.Notifications,
                            iconColor = primaryGreen,
                            status = if (booking.status == "confirmed") "Đã xác nhận" else "Đã hủy",
                            statusColor = if (booking.status == "confirmed") primaryGreen else Color.Red,
                            onClick = { onNotificationClick(booking) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    title: String,
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    status: String? = null,
    statusColor: Color = Color.Gray,
    onClick: () -> Unit // Thêm sự kiện click
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Thêm clickable vào Card
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, color = iconColor, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(message, fontSize = 14.sp, lineHeight = 20.sp, color = Color.DarkGray)
            
            if (status != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Trạng thái: $status",
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
