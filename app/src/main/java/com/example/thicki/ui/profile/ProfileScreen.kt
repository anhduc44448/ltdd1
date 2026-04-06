package com.example.thicki.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.User
import com.example.thicki.ui.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    viewModel: AuthViewModel,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    val primaryGreen = Color(0xFF00A67E)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cá nhân", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE0F2F1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(60.dp), tint = primaryGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = user?.fullName ?: "Người dùng", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = user?.email ?: "", color = Color.Gray)
            
            Surface(
                modifier = Modifier.padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                color = primaryGreen.copy(alpha = 0.1f)
            ) {
                Text(
                    text = if (user?.role == "doctor") "BÁC SĨ" else "BỆNH NHÂN",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = primaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Options
            ProfileMenuItem("Thông tin tài khoản", Icons.Default.Badge) { }
            ProfileMenuItem("Đổi mật khẩu", Icons.Default.Lock) { showChangePasswordDialog = true }
            ProfileMenuItem("Cài đặt thông báo", Icons.Default.Notifications) { }
            ProfileMenuItem("Lịch sử khám", Icons.Default.History) { onNavigateToHistory() }
            
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng xuất", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { old, new ->
                user?.userID?.let { viewModel.changePassword(it, old, new) }
                showChangePasswordDialog = false
            }
        )
    }
}

@Composable
fun ProfileMenuItem(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đổi mật khẩu") },
        text = {
            Column {
                OutlinedTextField(
                    value = oldPass,
                    onValueChange = { oldPass = it },
                    label = { Text("Mật khẩu cũ") },
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("Mật khẩu mới") },
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(oldPass, newPass) }) { Text("Xác nhận") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}
