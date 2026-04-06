package com.example.thicki.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.Doctor
import com.example.thicki.data.model.User
import com.example.thicki.ui.doctors.DoctorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: User?,
    doctorViewModel: DoctorViewModel,
    onNavigateToDoctors: () -> Unit,
    onNavigateToHospitals: () -> Unit,
    onNavigateToPharmacy: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDoctorDetail: (Doctor) -> Unit,
    onNavigateToManagement: (() -> Unit)? = null,
    onNavigateToAppointments: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToHealthRecord: () -> Unit,
    onNavigateToPaymentHistory: () -> Unit
) {
    val seaBlue = Color(0xFFE0F7FA)
    val primaryGreen = Color(0xFF00A67E)
    val featuredDoctors = doctorViewModel.doctors.take(5)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Trang chủ") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToDoctors,
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(32.dp), tint = primaryGreen) },
                    label = { Text("Đặt lịch") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToNotifications,
                    icon = {
                        BadgedBox(badge = { if(doctorViewModel.notificationCount > 0) Badge { Text(doctorViewModel.notificationCount.toString()) } }) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                        }
                    },
                    label = { Text("Thông báo") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Cá nhân") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(seaBlue)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderSection(user, primaryGreen)

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(vertical = 20.dp, horizontal = 16.dp)
            ) {
                ServicesGrid(
                    onNavigateToDoctors = onNavigateToDoctors,
                    onNavigateToHospitals = onNavigateToHospitals,
                    onNavigateToAppointments = onNavigateToAppointments,
                    onNavigateToHealthRecord = onNavigateToHealthRecord,
                    onNavigateToPaymentHistory = onNavigateToPaymentHistory
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            BannerSection()

            Spacer(modifier = Modifier.height(24.dp))

            FeaturedDoctorsSection(primaryGreen, featuredDoctors, onNavigateToDoctors, onNavigateToDoctorDetail)
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HeaderSection(user: User?, primaryGreen: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Xin chào, ${user?.fullName?.uppercase() ?: "NGƯỜI DÙNG"} 👋",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Chăm sóc sức khỏe chủ động\nvà hiệu quả",
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 18.sp
            )
        }
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp), tint = primaryGreen)
        }
    }
}

data class ServiceItem(val name: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun ServicesGrid(
    onNavigateToDoctors: () -> Unit,
    onNavigateToHospitals: () -> Unit,
    onNavigateToAppointments: () -> Unit,
    onNavigateToHealthRecord: () -> Unit,
    onNavigateToPaymentHistory: () -> Unit
) {
    val services = listOf(
        ServiceItem("Đặt hẹn\nbác sĩ", Icons.Default.Event, onNavigateToDoctors),
        ServiceItem("Bản đồ\ny tế", Icons.Default.Map, onNavigateToHospitals),
        ServiceItem("Hồ sơ\nsức khỏe", Icons.Default.AssignmentInd, onNavigateToHealthRecord),
        ServiceItem("Lịch\nkhám", Icons.Default.CalendarToday, onNavigateToAppointments),
        ServiceItem("Tra cứu\nhóa đơn", Icons.Default.Receipt, onNavigateToPaymentHistory)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        val rows = services.chunked(3)
        rows.forEachIndexed { index, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (rowItems.size < 3) Arrangement.Center else Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { service ->
                    ServiceGridItem(service)
                    if (rowItems.size < 3 && service != rowItems.last()) {
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
            }
            if (index < rows.size - 1) {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ServiceGridItem(service: ServiceItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(75.dp)
            .clickable { service.onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFF0FDF4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = service.icon,
                contentDescription = service.name,
                tint = Color(0xFF00A67E),
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = service.name,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BannerSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB))
                )
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "DỊCH VỤ VIP",
                    color = Color(0xFF00796B),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "BỆNH VIỆN VIỆT ĐỨC",
                    color = Color(0xFF004D40),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A67E)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Tìm hiểu ngay", fontSize = 10.sp)
                }
            }
            Icon(Icons.Default.HealthAndSafety, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color(0xFF00A67E))
        }
    }
}

@Composable
fun FeaturedDoctorsSection(
    primaryGreen: Color,
    doctors: List<Doctor>,
    onViewAll: () -> Unit,
    onDoctorClick: (Doctor) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bác sĩ tiêu biểu",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Tất cả",
                fontSize = 14.sp,
                color = primaryGreen,
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(doctors) { doctor ->
                DoctorCard(doctor, primaryGreen, onDoctorClick)
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor, primaryGreen: Color, onClick: (Doctor) -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick(doctor) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F8E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = primaryGreen)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = doctor.fullName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = doctor.specialtyName,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = doctor.address ?: "Đà Nẵng",
                fontSize = 11.sp,
                color = primaryGreen,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${doctor.experienceYears} năm kinh nghiệm",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }
    }
}
