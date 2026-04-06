package com.example.thicki.ui.doctors

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.Booking
import com.example.thicki.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthRecordScreen(
    user: User?,
    viewModel: DoctorViewModel,
    onBack: () -> Unit
) {
    val primaryGreen = Color(0xFF00A67E)
    // Lọc chỉ hiện các lịch hẹn đã có hồ sơ y tế (diagnosis không null)
    val records = viewModel.patientBookings.filter { it.diagnosis != null }

    LaunchedEffect(user?.userID) {
        user?.userID?.let { viewModel.fetchPatientBookings(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ sức khỏe", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Thông tin cá nhân", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = primaryGreen)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                        
                        ProfileInfoRow(Icons.Default.Badge, "Họ tên", user?.fullName ?: "N/A")
                        ProfileInfoRow(Icons.Default.Email, "Email", user?.email ?: "N/A")
                        ProfileInfoRow(Icons.Default.Phone, "Số điện thoại", user?.phone ?: "N/A")
                    }
                }
            }

            item {
                Text("Lịch sử khám & Kết quả", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
            }

            if (records.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Chưa có hồ sơ sức khỏe nào", color = Color.Gray)
                    }
                }
            } else {
                items(records) { record ->
                    HealthRecordItem(record, primaryGreen)
                }
            }
        }
    }
}

@Composable
fun HealthRecordItem(record: Booking, primaryGreen: Color) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = primaryGreen, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Bác sĩ: ${record.doctorName}", fontWeight = FontWeight.Bold)
                        Text("${record.startTime} - ${record.slotDate}", fontSize = 13.sp, color = Color.Gray)
                    }
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (!record.diagnosis.isNullOrBlank()) {
                        ResultSection(
                            title = "Chẩn đoán:",
                            content = record.diagnosis,
                            icon = Icons.Default.LocalHospital,
                            color = Color(0xFFE91E63)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    
                    if (!record.prescription.isNullOrBlank()) {
                        ResultSection(
                            title = "Đơn thuốc & Lời khuyên:",
                            content = record.prescription,
                            icon = Icons.Default.Medication,
                            color = primaryGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultSection(title: String, content: String, icon: ImageVector, color: Color) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = color)
            Text(content, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}
