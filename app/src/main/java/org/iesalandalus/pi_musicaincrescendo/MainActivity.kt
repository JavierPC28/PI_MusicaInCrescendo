package org.iesalandalus.pi_musicaincrescendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.common.components.*
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.ui.auth.LoginScreen
import org.iesalandalus.pi_musicaincrescendo.ui.auth.RegisterScreen
import org.iesalandalus.pi_musicaincrescendo.ui.navigation.*
import org.iesalandalus.pi_musicaincrescendo.ui.theme.PI_MusicaInCrescendoTheme

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val startDestination = if (AuthRepositoryImpl().currentUserEmail() != null) "home" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        composable("login") {
            Scaffold { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    LoginScreen(
                        onNavigateToRegister = { navController.navigate("register") },
                        onLoginSuccess = {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
        composable("register") {
            Scaffold { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    RegisterScreen(
                        onNavigateToLogin = { navController.popBackStack() },
                        onRegisterSuccess = {
                            navController.navigate("home") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }

        // Rutas con drawer y navegación inferior
        listOf(
            "home" to "Inicio",
            "events" to "Eventos",
            "repertoire" to "Repertorio",
            "notifications" to "Notificaciones",
            "profile" to "Perfil"
        ).forEach { (route, title) ->
            composable(route) {
                MainScaffold(
                    navController = navController,
                    title = title
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        when (route) {
                            "home" -> HomeScreenWrapper(onLogout = {
                                AuthRepositoryImpl().logout()
                                navController.navigate("login") {
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = true
                                    }
                                }
                            })

                            "events" -> EventsScreenWrapper(innerPadding)
                            "repertoire" -> RepertoireScreenWrapper(innerPadding)
                            "notifications" -> NotificationsScreenWrapper(innerPadding)
                            "profile" -> ProfileScreenWrapper(innerPadding)
                        }
                    }
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent()
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