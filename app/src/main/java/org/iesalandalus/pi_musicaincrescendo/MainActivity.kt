package org.iesalandalus.pi_musicaincrescendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.LoginViewModel
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RegisterViewModel
import org.iesalandalus.pi_musicaincrescendo.ui.auth.LoginScreen
import org.iesalandalus.pi_musicaincrescendo.ui.auth.RegisterScreen
import org.iesalandalus.pi_musicaincrescendo.ui.main.*
import org.iesalandalus.pi_musicaincrescendo.ui.theme.PI_MusicaInCrescendoTheme

/**
 * Actividad principal con navegación y barra inferior personalizada.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PI_MusicaInCrescendoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost()
                }
            }
        }
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { navController.navigate("register") },
                onLoginSuccess = {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                }
            )
        }
        composable("register") {
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("home") { popUpTo("register") { inclusive = true } }
                }
            )
        }
        composable("home") {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                HomeScreenWrapper(innerPadding)
            }
        }
        composable("events") {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                EventsScreenWrapper(innerPadding)
            }
        }
        composable("notifications") {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                NotificationsScreenWrapper(innerPadding)
            }
        }
        composable("profile") {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                ProfileScreenWrapper(innerPadding)
            }
        }
        composable("repertoire") {
            Scaffold(
                bottomBar = { BottomNavigationBar(navController) }
            ) { innerPadding ->
                RepertoireScreenWrapper(innerPadding)
            }
        }
    }
}

@Composable
private fun HomeScreenWrapper(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        HomeScreen()
    }
}

@Composable
private fun EventsScreenWrapper(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        EventsScreen()
    }
}

@Composable
private fun NotificationsScreenWrapper(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        NotificationsScreen()
    }
}

@Composable
private fun ProfileScreenWrapper(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        ProfileScreen()
    }
}

@Composable
private fun RepertoireScreenWrapper(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        RepertoireScreen()
    }
}