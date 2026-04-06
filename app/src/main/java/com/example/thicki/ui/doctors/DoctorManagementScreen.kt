package com.example.thicki.ui.doctors

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.Booking
import com.example.thicki.data.model.User
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorManagementScreen(
    user: User?,
    viewModel: DoctorViewModel,
    onNavigateToHome: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }
    var showRecordDialog by remember { mutableStateOf(false) }

    // Sử dụng doctorID thay vì userID
    val doctorId = user?.doctorID ?: 0

    LaunchedEffect(doctorId) {
        if (doctorId != 0) {
            viewModel.fetchAppointments(doctorId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý Bác sĩ", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Home, contentDescription = "Trang chủ")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    label = { Text("Lịch hẹn") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    label = { Text("Khung giờ") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    label = { Text("Đơn thuốc") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            when (selectedTab) {
                0 -> AppointmentManagement(
                    appointments = viewModel.appointments,
                    onWriteRecord = { booking ->
                        selectedBooking = booking
                        showRecordDialog = true
                    }
                )
                1 -> TimeSlotManagement(doctorId, viewModel)
                2 -> PrescriptionManagement(viewModel.appointments)
            }
        }
    }

    if (showRecordDialog && selectedBooking != null) {
        MedicalRecordDialog(
            booking = selectedBooking!!,
            onDismiss = { showRecordDialog = false },
            onSave = { diagnosis, prescription ->
                viewModel.saveMedicalRecord(selectedBooking!!.bookingID, diagnosis, prescription) { success, msg ->
                    if (success) {
                        showRecordDialog = false
                        viewModel.fetchAppointments(doctorId)
                    }
                }
            }
        )
    }
}

@Composable
fun AppointmentManagement(
    appointments: List<Booking>,
    onWriteRecord: (Booking) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lịch hẹn sắp tới", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (appointments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa có lịch hẹn nào.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(appointments) { booking ->
                    AppointmentItem(booking, onClick = { onWriteRecord(booking) })
                }
            }
        }
    }
}

@Composable
fun AppointmentItem(booking: Booking, onClick: () -> Unit) {
    val status = booking.status.lowercase()
    val isInteractive = status == "confirmed" || status == "completed"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isInteractive) { onClick() }, // Khóa nếu pending_payment hoặc cancelled
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isInteractive) Color.White else Color(0xFFF0F0F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isInteractive) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Bệnh nhân: ${booking.patientName}", 
                    fontWeight = FontWeight.Bold, 
                    fontSize = 16.sp,
                    color = if (isInteractive) Color.Black else Color.Gray
                )
                Text("Giờ: ${booking.startTime}", color = Color.Gray, fontSize = 14.sp)
                Text("Ngày: ${booking.slotDate}", color = Color.Gray, fontSize = 14.sp)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.padding(top = 4.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = when (status) {
                            "confirmed" -> Color(0xFFE8F5E9)
                            "completed" -> Color(0xFFE8F5E9)
                            "cancelled" -> Color(0xFFFFEBEE)
                            "pending_payment" -> Color(0xFFFFF3E0)
                            else -> Color(0xFFF5F5F5)
                        }
                    ) {
                        val displayText = when(status) {
                            "pending_payment" -> "Chờ thanh toán"
                            "confirmed" -> "Đã xác nhận"
                            "completed" -> "Hoàn thành"
                            "cancelled" -> "Đã hủy"
                            else -> booking.status.uppercase()
                        }
                        Text(
                            text = displayText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (status) {
                                "confirmed", "completed" -> Color(0xFF2E7D32)
                                "cancelled" -> Color(0xFFC62828)
                                "pending_payment" -> Color(0xFFEF6C00)
                                else -> Color.Gray
                            }
                        )
                    }
                    if (booking.diagnosis != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Đã khám",
                            tint = Color(0xFF00A67E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            if (isInteractive) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
            }
        }
    }
}

@Composable
fun MedicalRecordDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var diagnosis by remember { mutableStateOf(booking.diagnosis ?: "") }
    var prescription by remember { mutableStateOf(booking.prescription ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hồ sơ y tế: ${booking.patientName}") },
        text = {
            Column {
                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it },
                    label = { Text("Chẩn đoán") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = prescription,
                    onValueChange = { prescription = it },
                    label = { Text("Đơn thuốc") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(diagnosis, prescription) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A67E))
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSlotManagement(doctorId: Int, viewModel: DoctorViewModel) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val startTimePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            startTime = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val endTimePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            endTime = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tạo khung giờ khám", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Chọn Ngày
                OutlinedTextField(
                    value = date,
                    onValueChange = { },
                    label = { Text("Ngày khám") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    enabled = false,
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                        disabledLeadingIconColor = Color(0xFF00A67E)
                    )
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                // Chọn Giờ Bắt Đầu
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { },
                    label = { Text("Giờ bắt đầu") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { startTimePickerDialog.show() },
                    enabled = false,
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                        disabledLeadingIconColor = Color(0xFF00A67E)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Chọn Giờ Kết Thúc
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { },
                    label = { Text("Giờ kết thúc") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { endTimePickerDialog.show() },
                    enabled = false,
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = Color.Black,
                        disabledBorderColor = Color.Gray,
                        disabledLabelColor = Color.Gray,
                        disabledLeadingIconColor = Color(0xFF00A67E)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                if (message.isNotEmpty()) {
                    Text(
                        text = message, 
                        color = if (message.contains("thành công")) Color(0xFF00A67E) else Color.Red,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (doctorId == 0) {
                            message = "Không tìm thấy ID bác sĩ. Vui lòng đăng nhập lại."
                            return@Button
                        }
                        if (date.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank()) {
                            viewModel.createTimeSlot(doctorId, date, startTime, endTime) { success, msg ->
                                message = msg
                                if (success) {
                                    date = ""; startTime = ""; endTime = ""
                                }
                            }
                        } else {
                            message = "Vui lòng chọn đầy đủ ngày và giờ"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A67E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tạo khung giờ", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@Composable
fun PrescriptionManagement(appointments: List<Booking>) {
    val completedBookings = appointments.filter { it.diagnosis != null }
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lịch sử khám & Đơn thuốc", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (completedBookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa có hồ sơ nào.", color = Color.Gray)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(completedBookings) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Bệnh nhân: ${booking.patientName}", fontWeight = FontWeight.Bold)
                            Text("Ngày: ${booking.slotDate} - ${booking.startTime}", fontSize = 14.sp, color = Color.Gray)
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                            Text("Chẩn đoán:", fontWeight = FontWeight.Medium, color = Color(0xFF00A67E))
                            Text(booking.diagnosis ?: "")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Đơn thuốc:", fontWeight = FontWeight.Medium, color = Color(0xFF00A67E))
                            Text(booking.prescription ?: "Chưa có đơn thuốc")
                        }
                    }
                }
            }
        }
    }
}
