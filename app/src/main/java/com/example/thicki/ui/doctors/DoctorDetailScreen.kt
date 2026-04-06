package com.example.thicki.ui.doctors

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.Doctor
import com.example.thicki.data.model.TimeSlot
import com.example.thicki.data.model.User
import com.example.thicki.ui.notifications.NotificationHelper
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(
    doctor: Doctor,
    viewModel: DoctorViewModel,
    currentUser: User?, 
    onBack: () -> Unit,
    onBookingSuccess: () -> Unit
) {
    val context = LocalContext.current
    val notificationHelper = remember { NotificationHelper(context) }
    val primaryGreen = Color(0xFF00A67E)
    var selectedSlot by remember { mutableStateOf<TimeSlot?>(null) }
    val timeSlots = viewModel.timeSlots

    val isSelfBooking = currentUser?.role == "doctor" && currentUser.doctorID == doctor.doctorID

    LaunchedEffect(doctor.doctorID) {
        viewModel.fetchTimeSlots(doctor.doctorID)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đặt lịch & Thanh toán", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp)
        ) {
            // Thông tin bác sĩ
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFE0F2F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = primaryGreen)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(doctor.fullName, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(doctor.specialtyName, color = primaryGreen)
                    Text("Giá khám: ${doctor.basePrice.toInt()} VNĐ", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Chọn khung giờ khám", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            if (timeSlots.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Hiện không có lịch hẹn nào.", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(timeSlots) { slot ->
                        val isBooked = slot.isBooked
                        val isSelected = selectedSlot?.slotID == slot.slotID
                        
                        val dateFormatted = try {
                            val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val targetFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                            targetFormat.format(originalFormat.parse(slot.slotDate)!!)
                        } catch (e: Exception) { slot.slotDate }

                        val timeFormatted = if (slot.startTime.length >= 5) slot.startTime.substring(0, 5) else slot.startTime

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when {
                                        isBooked -> Color(0xFFE0E0E0) // Màu xám nhạt cho ô không khả dụng
                                        isSelected -> primaryGreen
                                        else -> Color.White
                                    }
                                )
                                .border(1.dp, if (isSelected) primaryGreen else Color.LightGray, RoundedCornerShape(12.dp))
                                .clickable(enabled = !isBooked && !isSelfBooking) { selectedSlot = slot }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = timeFormatted,
                                    color = if (isSelected) Color.White else if (isBooked) Color.Gray else Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = dateFormatted,
                                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    selectedSlot?.let { slot ->
                        viewModel.processPaymentAndBooking(
                            context = context,
                            patientId = currentUser?.userID ?: 0,
                            doctor = doctor,
                            slot = slot,
                            onSuccess = { booking ->
                                notificationHelper.showNotification(
                                    "Thanh toán thành công!",
                                    "Lịch khám với BS. ${doctor.fullName} đã được xác nhận."
                                )
                                onBookingSuccess()
                            },
                            onError = { errorMsg ->
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            }
                        )
                    }
                },
                enabled = selectedSlot != null && !isSelfBooking,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (selectedSlot != null) "Thanh toán & Đặt lịch" else "Vui lòng chọn khung giờ",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
