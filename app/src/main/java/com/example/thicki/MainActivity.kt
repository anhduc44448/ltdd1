package com.example.thicki

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.thicki.payment.Constant.AppInfo
import com.example.thicki.ui.AuthState
import com.example.thicki.ui.AuthViewModel
import com.example.thicki.ui.auth.LoginScreen
import com.example.thicki.ui.auth.RegisterScreen
import com.example.thicki.ui.doctors.*
import com.example.thicki.ui.home.HomeScreen
import com.example.thicki.ui.hospitals.HospitalListScreen
import com.example.thicki.ui.hospitals.HospitalMapScreen
import com.example.thicki.ui.hospitals.HospitalViewModel
import com.example.thicki.ui.notifications.NotificationListScreen
import com.example.thicki.ui.pharmacy.PharmacyListScreen
import com.example.thicki.ui.pharmacy.PharmacyViewModel
import com.example.thicki.ui.profile.ProfileScreen
import com.example.thicki.ui.theme.ThiCkiTheme
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPaySDK

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Khởi tạo ZaloPay SDK
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX)

        enableEdgeToEdge()
        setContent {
            ThiCkiTheme {
                AppNavigation()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val doctorViewModel: DoctorViewModel = viewModel()
    val hospitalViewModel: HospitalViewModel = viewModel()
    val pharmacyViewModel: PharmacyViewModel = viewModel()

    val currentUser = when (val state = authViewModel.loginState) {
        is AuthState.Success -> state.user
        else -> null
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    val user = (authViewModel.loginState as? AuthState.Success)?.user
                    if (user?.role == "doctor") {
                        navController.navigate("doctor_management") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable("home") {
            HomeScreen(
                user = currentUser,
                doctorViewModel = doctorViewModel,
                onNavigateToDoctors = {
                    navController.navigate("doctor_list")
                },
                onNavigateToHospitals = {
                    navController.navigate("hospital_map")
                },
                onNavigateToPharmacy = {
                    navController.navigate("pharmacy_list")
                },
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateToDoctorDetail = { doctor ->
                    navController.navigate("doctor_info/${doctor.doctorID}")
                },
                onNavigateToManagement = {
                    navController.navigate("doctor_management")
                },
                onNavigateToAppointments = {
                    navController.navigate("patient_appointments")
                },
                onNavigateToNotifications = {
                    navController.navigate("notifications")
                },
                onNavigateToHealthRecord = {
                    navController.navigate("health_record")
                },
                onNavigateToPaymentHistory = {
                    navController.navigate("payment_history")
                }
            )
        }
        composable("payment_history") {
            if (currentUser != null) {
                PaymentHistoryScreen(
                    patientId = currentUser.userID ?: 0,
                    viewModel = doctorViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable("notifications") {
            if (currentUser != null) {
                NotificationListScreen(
                    patientId = currentUser.userID ?: 0,
                    viewModel = doctorViewModel,
                    onBack = { navController.popBackStack() },
                    onNotificationClick = { booking ->
                        navController.navigate("appointment_detail/${booking.bookingID}")
                    }
                )
            }
        }
        composable("hospital_map") {
            HospitalMapScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("patient_appointments") {
            if (currentUser != null) {
                PatientAppointmentsScreen(
                    patientId = currentUser.userID ?: 0,
                    viewModel = doctorViewModel,
                    onBack = { navController.popBackStack() },
                    onAppointmentClick = { booking ->
                        navController.navigate("appointment_detail/${booking.bookingID}")
                    }
                )
            }
        }
        composable(
            route = "appointment_detail/{bookingId}",
            arguments = listOf(navArgument("bookingId") { type = NavType.IntType })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getInt("bookingId")
            val booking = doctorViewModel.patientBookings.find { it.bookingID == bookingId }
            if (booking != null && currentUser != null) {
                AppointmentDetailScreen(
                    booking = booking,
                    viewModel = doctorViewModel,
                    patientId = currentUser.userID ?: 0,
                    onBack = { navController.popBackStack() },
                    onRebook = {
                        doctorViewModel.cancelBooking(booking.bookingID, currentUser.userID ?: 0)
                        navController.navigate("doctor_booking/${booking.doctorID}") {
                            popUpTo("patient_appointments") { inclusive = true }
                        }
                    }
                )
            }
        }
        composable("health_record") {
            HealthRecordScreen(
                user = currentUser,
                viewModel = doctorViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("profile") {
            ProfileScreen(
                user = currentUser,
                viewModel = authViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() },
                onNavigateToHistory = {
                    navController.navigate("patient_appointments")
                }
            )
        }
        composable("doctor_management") {
            DoctorManagementScreen(
                user = currentUser,
                viewModel = doctorViewModel,
                onNavigateToHome = {
                    navController.navigate("home")
                }
            )
        }
        composable("doctor_list") {
            DoctorListScreen(
                viewModel = doctorViewModel,
                onBack = { navController.popBackStack() },
                onDoctorClick = { doctor ->
                    navController.navigate("doctor_info/${doctor.doctorID}")
                }
            )
        }
        composable(
            route = "doctor_info/{doctorId}",
            arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getInt("doctorId")
            val doctor = doctorViewModel.doctors.find { it.doctorID == doctorId }
            if (doctor != null) {
                DoctorInfoScreen(
                    doctor = doctor,
                    onBack = { navController.popBackStack() },
                    onBookClick = { navController.navigate("doctor_booking/${doctor.doctorID}") }
                )
            }
        }
        composable(
            route = "doctor_booking/{doctorId}",
            arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getInt("doctorId")
            val doctor = doctorViewModel.doctors.find { it.doctorID == doctorId }
            if (doctor != null) {
                DoctorDetailScreen(
                    doctor = doctor,
                    viewModel = doctorViewModel,
                    currentUser = currentUser,
                    onBack = { navController.popBackStack() },
                    onBookingSuccess = {
                        navController.navigate("booking_success")
                    }
                )
            }
        }
        composable("booking_success") {
            val booking = doctorViewModel.lastBooking
            if (booking != null) {
                BookingSuccessScreen(
                    booking = booking,
                    viewModel = doctorViewModel,
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onRebook = {
                        navController.navigate("doctor_booking/${booking.doctorID}") {
                            popUpTo("booking_success") { inclusive = true }
                        }
                    }
                )
            }
        }
        composable("hospital_list") {
            HospitalListScreen(
                viewModel = hospitalViewModel,
                onBack = { navController.popBackStack() },
                onHospitalClick = { hospital -> }
            )
        }
        composable("pharmacy_list") {
            PharmacyListScreen(
                viewModel = pharmacyViewModel,
                onBack = { navController.popBackStack() },
                onMedicationClick = { medication -> }
            )
        }
    }
}
