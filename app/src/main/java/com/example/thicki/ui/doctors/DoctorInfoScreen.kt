package com.example.thicki.ui.doctors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.Doctor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorInfoScreen(
    doctor: Doctor,
    onBack: () -> Unit,
    onBookClick: () -> Unit
) {
    val primaryGreen = Color(0xFF00A67E)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin bác sĩ", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F2F1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(80.dp), tint = primaryGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = doctor.fullName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = doctor.specialtyName, fontSize = 18.sp, color = primaryGreen)

            Spacer(modifier = Modifier.height(8.dp))

            // Xóa phần đánh giá sao, chỉ hiển thị số năm kinh nghiệm
            Text(text = "${doctor.experienceYears} năm kinh nghiệm", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // Giới thiệu
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Giới thiệu", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bác sĩ ${doctor.fullName} là một chuyên gia hàng đầu trong lĩnh vực ${doctor.specialtyName} với hơn ${doctor.experienceYears} năm kinh nghiệm công tác tại các bệnh viện lớn. Bác sĩ luôn tận tâm và chu đáo trong việc thăm khám và điều trị cho bệnh nhân.",
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Thông tin liên hệ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Địa chỉ: Phòng khám Đa khoa Quốc tế", fontSize = 15.sp, color = Color.DarkGray)
                    Text(text = "Giá khám: ${String.format("%,.0f", doctor.basePrice)} đ", fontSize = 15.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBookClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryGreen)
            ) {
                Text(text = "Đặt lịch hẹn ngay", modifier = Modifier.padding(8.dp), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
