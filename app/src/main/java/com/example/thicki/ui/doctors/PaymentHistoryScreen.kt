package com.example.thicki.ui.doctors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.PaymentRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentHistoryScreen(
    patientId: Int,
    viewModel: DoctorViewModel,
    onBack: () -> Unit
) {
    val history = viewModel.paymentHistory
    val primaryGreen = Color(0xFF00A67E)

    LaunchedEffect(patientId) {
        viewModel.getPaymentHistory(patientId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử hóa đơn", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Chưa có lịch sử hóa đơn nào.", color = Color.Gray)
                }
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
                items(history) { record ->
                    PaymentRecordCard(record, primaryGreen)
                }
            }
        }
    }
}

@Composable
fun PaymentRecordCard(record: PaymentRecord, primaryGreen: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mã GD: ${record.appTransID}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                val statusText = if (record.isSuccessful == 1) "Thành công" else "Thất bại"
                val statusColor = if (record.isSuccessful == 1) primaryGreen else Color.Red
                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Bác sĩ: ${record.doctorName}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Thời gian: ${record.startTime} - ${record.slotDate.split("T")[0]}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = String.format("%,.0f đ", record.amount),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (record.isSuccessful == 1) primaryGreen else Color.Red
                )
            }
        }
    }
}
