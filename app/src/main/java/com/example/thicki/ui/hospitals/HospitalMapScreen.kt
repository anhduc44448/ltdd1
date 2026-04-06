package com.example.thicki.ui.hospitals

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalMapScreen(
    onBack: () -> Unit
) {
    // Vị trí mặc định (ví dụ: trung tâm Đà Nẵng)
    val daNang = LatLng(16.0544, 108.2022)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(daNang, 15f)
    }

    var mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    
    var showMapTypeMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bản đồ bệnh viện", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showMapTypeMenu = true }) {
                        Icon(Icons.Default.Layers, contentDescription = "Kiểu bản đồ")
                    }
                    DropdownMenu(
                        expanded = showMapTypeMenu,
                        onDismissRequest = { showMapTypeMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Bình thường") },
                            onClick = {
                                mapProperties = mapProperties.copy(mapType = MapType.NORMAL)
                                showMapTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Vệ tinh") },
                            onClick = {
                                mapProperties = mapProperties.copy(mapType = MapType.SATELLITE)
                                showMapTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Địa hình") },
                            onClick = {
                                mapProperties = mapProperties.copy(mapType = MapType.TERRAIN)
                                showMapTypeMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hỗn hợp") },
                            onClick = {
                                mapProperties = mapProperties.copy(mapType = MapType.HYBRID)
                                showMapTypeMenu = false
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(daNang, 15f)
                },
                containerColor = Color.White,
                contentColor = Color(0xFF00A67E)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Vị trí của tôi")
            }
        }
    ) { innerPadding ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
            onMapLongClick = { latLng ->
                // Logic thêm marker khi nhấn giữ có thể thực hiện ở đây
            }
        ) {
            // Ví dụ một vài Marker bệnh viện tại Đà Nẵng
            Marker(
                state = MarkerState(position = LatLng(16.0544, 108.2022)),
                title = "Bệnh viện Đà Nẵng",
                snippet = "124 Hải Phòng, Thạch Thang, Hải Châu, Đà Nẵng"
            )
            Marker(
                state = MarkerState(position = LatLng(16.0474, 108.2132)),
                title = "Bệnh viện Hoàn Mỹ",
                snippet = "291 Nguyễn Văn Linh, Thạc Gián, Thanh Khê, Đà Nẵng"
            )
        }
    }
}
