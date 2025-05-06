package org.iesalandalus.pi_musicaincrescendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.LoginViewModel
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RegisterViewModel
import org.iesalandalus.pi_musicaincrescendo.ui.*
import org.iesalandalus.pi_musicaincrescendo.ui.theme.PI_MusicaInCrescendoTheme

/**
 * Actividad principal con navegación.
 * Al navegar desde login a register limpia campos de login.
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
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            val loginSuccess by loginViewModel.loginSuccess.collectAsState()

            // Si ya inició sesión, navegamos a home
            LaunchedEffect(loginSuccess) {
                if (loginSuccess) navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }

            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            val registerViewModel: RegisterViewModel = viewModel()
            val regSuccess by registerViewModel.registrationSuccess.collectAsState()

            LaunchedEffect(regSuccess) {
                if (regSuccess) navController.navigate("home") {
                    popUpTo("register") { inclusive = true }
                }
            }

            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("home") {
            HomeScreen()
        }
    }
}