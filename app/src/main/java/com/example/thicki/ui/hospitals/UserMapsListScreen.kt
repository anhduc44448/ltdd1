package com.example.thicki.ui.hospitals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thicki.data.model.UserMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMapsListScreen(
    viewModel: UserMapsViewModel,
    onMapClick: (UserMap) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val maps = viewModel.userMaps
    var showDialog by remember { mutableStateOf(false) }
    var newMapTitle by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }

    // Lọc bản đồ theo tiêu đề bản đồ hoặc tiêu đề địa điểm đã ghim
    val filteredMaps = remember(searchQuery, maps) {
        if (searchQuery.isBlank()) {
            maps
        } else {
            maps.filter { map ->
                map.title.contains(searchQuery, ignoreCase = true) ||
                        map.places.any { it.title.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadMaps(context)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Bản đồ của tôi", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        }
                    }
                )
                // Thanh tìm kiếm
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Tìm theo tên bản đồ hoặc địa điểm...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00A67E),
                        unfocusedBorderColor = Color.Gray
                    ),
                    singleLine = true
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF00A67E),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tạo bản đồ mới")
            }
        }
    ) { innerPadding ->
        if (filteredMaps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (searchQuery.isEmpty()) "Chưa có bản đồ nào. Nhấn + để tạo!" else "Không tìm thấy kết quả phù hợp.",
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(filteredMaps) { map ->
                    ListItem(
                        headlineContent = { Text(map.title, fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("${map.places.size} địa điểm đã đánh dấu") },
                        leadingContent = {
                            Icon(
                                Icons.Default.Map,
                                contentDescription = null,
                                tint = Color(0xFF00A67E)
                            )
                        },
                        modifier = Modifier.clickable { onMapClick(map) }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Tạo bản đồ mới") },
                text = {
                    TextField(
                        value = newMapTitle,
                        onValueChange = { newMapTitle = it },
                        placeholder = { Text("Nhập tiêu đề bản đồ...") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        if (newMapTitle.isNotBlank()) {
                            viewModel.addMap(newMapTitle, context)
                            newMapTitle = ""
                            showDialog = false
                        }
                    }) { Text("Lưu") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Hủy") }
                }
            )
        }
    }
}
