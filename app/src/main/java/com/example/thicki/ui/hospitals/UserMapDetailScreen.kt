package com.example.thicki.ui.hospitals

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.thicki.data.model.Place
import com.example.thicki.data.model.UserMap
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMapDetailScreen(
    userMap: UserMap,
    viewModel: UserMapsViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    val initialLocation = if (userMap.places.isNotEmpty()) {
        LatLng(userMap.places.first().latitude, userMap.places.first().longitude)
    } else {
        LatLng(16.0544, 108.2022) // Đà Nẵng
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }

    var mapProperties by remember { 
        mutableStateOf(MapProperties(
            mapType = MapType.NORMAL,
            isMyLocationEnabled = false 
        )) 
    }
    
    var showMapTypeMenu by remember { mutableStateOf(false) }
    var showAddPlaceDialog by remember { mutableStateOf<LatLng?>(null) }
    var newPlaceTitle by remember { mutableStateOf("") }
    var newPlaceDesc by remember { mutableStateOf("") }

    // Xử lý quyền vị trí
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            mapProperties = mapProperties.copy(isMyLocationEnabled = true)
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            mapProperties = mapProperties.copy(isMyLocationEnabled = true)
        } else {
            locationPermissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userMap.title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { showMapTypeMenu = true }) {
                        Icon(Icons.Default.Layers, contentDescription = null)
                    }
                    DropdownMenu(expanded = showMapTypeMenu, onDismissRequest = { showMapTypeMenu = false }) {
                        listOf(MapType.NORMAL, MapType.SATELLITE, MapType.TERRAIN, MapType.HYBRID).forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    mapProperties = mapProperties.copy(mapType = type)
                                    showMapTypeMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (mapProperties.isMyLocationEnabled) {
                        try {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                location?.let {
                                    val userLatLng = LatLng(it.latitude, it.longitude)
                                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                                }
                            }
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        }
                    }
                },
                containerColor = Color.White,
                contentColor = Color(0xFF00A67E)
            ) { Icon(Icons.Default.MyLocation, contentDescription = null) }
        }
    ) { innerPadding ->
        GoogleMap(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = MapUiSettings(myLocationButtonEnabled = false),
            onMapLongClick = { latLng -> showAddPlaceDialog = latLng }
        ) {
            userMap.places.forEach { place ->
                Marker(
                    state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                    title = place.title,
                    snippet = place.description
                )
            }
        }

        if (showAddPlaceDialog != null) {
            AlertDialog(
                onDismissRequest = { showAddPlaceDialog = null },
                title = { Text("Thêm địa điểm mới") },
                text = {
                    Column {
                        TextField(value = newPlaceTitle, onValueChange = { newPlaceTitle = it }, label = { Text("Tiêu đề") })
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(value = newPlaceDesc, onValueChange = { newPlaceDesc = it }, label = { Text("Mô tả") })
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val latLng = showAddPlaceDialog!!
                        if (newPlaceTitle.isNotBlank()) {
                            viewModel.addPlaceToMap(
                                userMap.title,
                                Place(newPlaceTitle, newPlaceDesc, latLng.latitude, latLng.longitude),
                                context
                            )
                            showAddPlaceDialog = null
                            newPlaceTitle = ""
                            newPlaceDesc = ""
                        }
                    }) { Text("Thêm") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddPlaceDialog = null }) { Text("Hủy") }
                }
            )
        }
    }
}
