package com.example.thicki.ui.doctors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.Booking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingSuccessScreen(
    booking: Booking,
    viewModel: DoctorViewModel,
    onNavigateToHome: () -> Unit,
    onRebook: () -> Unit
) {
    val primaryGreen = Color(0xFF00A67E)
    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Hủy lịch hẹn") },
            text = { Text("Bạn có muốn đổi sang lịch hẹn khác không?") },
            confirmButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    viewModel.cancelBooking(booking.bookingID, booking.patientID)
                    onRebook()
                }) {
                    Text("Có (Đổi lịch)", color = primaryGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCancelDialog = false
                    viewModel.cancelBooking(booking.bookingID, booking.patientID)
                    onNavigateToHome()
                }) {
                    Text("Không (Chỉ hủy)", color = Color.Red)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Trạng thái đặt lịch", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Home, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(primaryGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = primaryGreen
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Cảm ơn quý khách đã đặt lịch!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = primaryGreen,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Thông tin lịch hẹn:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    InfoRow("Bác sĩ:", booking.doctorName ?: "N/A")
                    InfoRow("Ngày:", booking.slotDate)
                    InfoRow("Giờ:", booking.startTime.substring(0, 5))
                    InfoRow("Mã đặt lịch:", "#${booking.bookingID}")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = { showCancelDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.Red)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Hủy đặt lịch", modifier = Modifier.padding(8.dp))
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onNavigateToHome,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Về trang chủ", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}
