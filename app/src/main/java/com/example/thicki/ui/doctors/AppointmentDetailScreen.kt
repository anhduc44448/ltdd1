package com.example.thicki.ui.doctors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.Booking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    booking: Booking,
    viewModel: DoctorViewModel,
    patientId: Int,
    onBack: () -> Unit,
    onRebook: () -> Unit
) {
    val primaryGreen = Color(0xFF00A67E)
    var showCancelDialog by remember { mutableStateOf(false) }

    // Xử lý định dạng ngày: chỉ lấy phần YYYY-MM-DD
    val displayDate = if (booking.slotDate.contains("T")) {
        booking.slotDate.split("T")[0]
    } else {
        booking.slotDate
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết lịch khám", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    DetailRow(Icons.Default.Person, "Bác sĩ", booking.doctorName ?: "N/A", primaryGreen)
                    DetailRow(Icons.Default.CalendarToday, "Ngày khám", displayDate, primaryGreen)
                    DetailRow(Icons.Default.AccessTime, "Giờ khám", booking.startTime, primaryGreen)
                    
                    // CẬP NHẬT: Hiển thị "Hoàn thành" nếu đã có chẩn đoán
                    val statusText = when {
                        booking.diagnosis != null -> "Hoàn thành"
                        booking.status == "confirmed" -> "Đã xác nhận"
                        else -> "Đã hủy"
                    }
                    val statusColor = if (booking.diagnosis != null || booking.status == "confirmed") primaryGreen else Color.Red
                    
                    DetailRow(Icons.Default.Info, "Trạng thái", statusText, statusColor)
                    
                    if (booking.diagnosis != null) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        DetailRow(Icons.Default.MedicalServices, "Chẩn đoán", booking.diagnosis, Color.Magenta)
                        DetailRow(Icons.Default.Description, "Đơn thuốc", booking.prescription ?: "N/A", Color.Blue)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Chỉ hiển thị nút Hủy/Cập nhật khi bác sĩ CHƯA cập nhật chẩn đoán
            if (booking.diagnosis == null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showCancelDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Hủy lịch", modifier = Modifier.padding(8.dp))
                    }

                    Button(
                        onClick = { onRebook() },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cập nhật (Đổi giờ)", modifier = Modifier.padding(8.dp))
                    }
                }
            } else {
                // Thông báo khi lịch khám đã hoàn thành
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Lịch khám đã hoàn thành. Cảm ơn bạn đã sử dụng dịch vụ.",
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        color = primaryGreen,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = { Text("Xác nhận hủy") },
                text = { Text("Bạn có chắc chắn muốn hủy lịch hẹn này?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.cancelBooking(booking.bookingID, patientId)
                        showCancelDialog = false
                        onBack()
                    }) { Text("Xác nhận", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelDialog = false }) { Text("Quay lại") }
                }
            )
        }
    }
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String, iconColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
