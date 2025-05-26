package org.iesalandalus.pi_musicaincrescendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.common.components.*
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.HomeViewModel
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.MainViewModel
import org.iesalandalus.pi_musicaincrescendo.ui.auth.LoginScreen
import org.iesalandalus.pi_musicaincrescendo.ui.auth.RegisterScreen
import org.iesalandalus.pi_musicaincrescendo.ui.main.*
import org.iesalandalus.pi_musicaincrescendo.ui.theme.PI_MusicaInCrescendoTheme

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Inicio")
    object Events : Screen("events", "Eventos")
    object Repertoire : Screen("repertoire", "Repertorio")
    object Notifications : Screen("notifications", "Notificaciones")
    object Profile : Screen("profile", "Perfil")
    object AddRepertoire : Screen("add_repertoire", "Añadir obra")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PI_MusicaInCrescendoTheme {
                val systemUiController = rememberSystemUiController()
                val darkTheme = isSystemInDarkTheme()

                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = Color.Transparent,
                        darkIcons = !darkTheme
                    )
                }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val startRoute = if (AuthRepositoryImpl().currentUserEmail() != null)
        Screen.Home.route
    else
        "login"

    NavHost(navController = navController, startDestination = startRoute) {
        // --- Login y Registro ---
        composable("login") {
            Scaffold { padding ->
                Box(Modifier.padding(padding)) {
                    LoginScreen(
                        onNavigateToRegister = { navController.navigate("register") },
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
        composable("register") {
            Scaffold { padding ->
                Box(Modifier.padding(padding)) {
                    RegisterScreen(
                        onNavigateToLogin = {
                            navController.popBackStack()
                            navController.navigate("login") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onRegisterSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        // --- Bottom navigation screens (except Repertoire) ---
        listOf(
            Screen.Home,
            Screen.Events,
            Screen.Notifications,
            Screen.Profile
        ).forEach { screen ->
            composable(screen.route) {
                MainScaffold(navController, screen.title) { padding ->
                    Box(
                        Modifier
                            .padding(padding)
                            .fillMaxSize()
                    ) {
                        when (screen) {
                            Screen.Home -> HomeScreen()
                            Screen.Events -> EventsScreen()
                            Screen.Notifications -> NotificationsScreen()
                            Screen.Profile -> ProfileScreen()
                            else -> {/* ... */
                            }
                        }
                    }
                }
            }
        }

        // --- Repertoire (necesita navController) ---
        composable(Screen.Repertoire.route) {
            MainScaffold(navController, Screen.Repertoire.title) { padding ->
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    RepertoireScreen(navController)
                }
            }
        }

        composable(Screen.AddRepertoire.route) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(Screen.AddRepertoire.title) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver"
                                )
                            }
                        },
                        actions = {
                            TextButton(onClick = { /* ... */ }) {
                                Text("Guardar")
                            }
                        }
                    )
                }
            ) { padding ->
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    AddRepertoireScreen()
                }
            }
        }
    }
}

@Composable
fun MainScaffold(
    navController: NavHostController,
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val mainViewModel: MainViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val activity = LocalActivity.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    onLogout = {
                        homeViewModel.cancelarRecoleccion()
                        mainViewModel.logout()
                        navController.navigate("login") { popUpTo(0) }
                    },
                    onExit = { activity?.finish() }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBarWithDrawer(
                    title = title,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            },
            bottomBar = { BottomNavigationBar(navController) },
            content = content
        )
    }
}