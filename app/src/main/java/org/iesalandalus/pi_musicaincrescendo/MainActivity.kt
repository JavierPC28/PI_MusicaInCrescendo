package org.iesalandalus.pi_musicaincrescendo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.*
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.LoginViewModel
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RegisterViewModel
import org.iesalandalus.pi_musicaincrescendo.ui.*
import org.iesalandalus.pi_musicaincrescendo.ui.theme.PI_MusicaInCrescendoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PI_MusicaInCrescendoTheme {
                Surface {
                    RootNavHost()
                }
            }
        }
    }
}

/**
 * Elemento de navegación inferior.
 */
sealed class NavItem(val route: String, val iconRes: Int) {
    object Events : NavItem("events", R.drawable.events)
    object Repertoire : NavItem("repertoire", R.drawable.repertorio)
    object Home : NavItem("home", R.drawable.banda_alcolea)
    object Notifications : NavItem("notifications", R.drawable.notificaciones)
    object Profile : NavItem("profile", R.drawable.ajustes)
}

@Composable
fun RootNavHost() {
    val rootNavController = rememberNavController()
    NavHost(
        navController = rootNavController,
        startDestination = "auth"
    ) {
        authGraph(rootNavController)
        composable("main") {
            BottomNavHost()
        }
    }
}

/**
 * Grafo de autenticación: login y registro.
 */
fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(startDestination = "login", route = "auth") {
        composable("login") {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
        composable("register") {
            val registerViewModel: RegisterViewModel = viewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
    }
}

/**
 * Navegación principal con menú inferior.
 */
@Composable
fun BottomNavHost() {
    val navController = rememberNavController()
    val items = listOf(
        NavItem.Events,
        NavItem.Repertoire,
        NavItem.Home,
        NavItem.Notifications,
        NavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp
            ) {
                val currentDestination = navController.currentBackStackEntryAsState().value?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.iconRes),
                                contentDescription = item.route
                            )
                        },
                        selected = currentDestination?.route == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavItem.Events.route) { EventsScreen() }
            composable(NavItem.Repertoire.route) { RepertoireScreen() }
            composable(NavItem.Home.route) { HomeScreen() }
            composable(NavItem.Notifications.route) { NotificationsScreen() }
            composable(NavItem.Profile.route) { ProfileScreen() }
        }
    }
}