package org.iesalandalus.pi_musicaincrescendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
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
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.MainViewModel
import org.iesalandalus.pi_musicaincrescendo.ui.auth.LoginScreen
import org.iesalandalus.pi_musicaincrescendo.ui.auth.RegisterScreen
import org.iesalandalus.pi_musicaincrescendo.ui.main.*
import org.iesalandalus.pi_musicaincrescendo.ui.theme.PI_MusicaInCrescendoTheme

/**
 * Define las rutas, títulos y contenido de las pantallas principales,
 * aplicando el padding correspondiente.
 */
sealed class Screen(
    val route: String,
    val title: String,
    val content: @Composable (PaddingValues) -> Unit
) {
    object Home : Screen("home", "Inicio", { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) { HomeScreen() }
    })

    object Events : Screen("events", "Eventos", { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) { EventsScreen() }
    })

    object Repertoire : Screen("repertoire", "Repertorio", { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) { RepertoireScreen() }
    })

    object Notifications : Screen("notifications", "Notificaciones", { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) { NotificationsScreen() }
    })

    object Profile : Screen("profile", "Perfil", { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) { ProfileScreen() }
    })
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

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val startRoute =
        if (AuthRepositoryImpl().currentUserEmail() != null) Screen.Home.route else "login"

    NavHost(
        navController = navController,
        startDestination = startRoute,
        modifier = Modifier.fillMaxSize()
    ) {
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
        // Pantallas principales
        listOf(
            Screen.Home,
            Screen.Events,
            Screen.Repertoire,
            Screen.Notifications,
            Screen.Profile
        ).forEach { screen ->
            composable(screen.route) {
                MainScaffold(
                    navController = navController,
                    title = screen.title
                ) { padding ->
                    screen.content(padding)
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
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val mainViewModel: MainViewModel = viewModel()
    val activity = LocalActivity.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    onLogout = {
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
            bottomBar = { BottomNavigationBar(navController) }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}