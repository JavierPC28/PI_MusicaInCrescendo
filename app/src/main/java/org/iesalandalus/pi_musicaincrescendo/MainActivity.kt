package org.iesalandalus.pi_musicaincrescendo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.common.components.*
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.*
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

    object AddEvent : Screen("add_event", "Añadir Evento") {
        fun routeWithArgs(eventId: String? = null): String {
            return if (eventId != null) "add_event?eventId=$eventId" else "add_event"
        }
    }

    object AddEditRepertoire : Screen("add_repertoire", "Añadir obra") {
        fun routeWithArgs(workId: String? = null): String {
            return if (workId != null) "add_repertoire?workId=$workId" else "add_repertoire"
        }
    }

    object RepertoireDetail : Screen("repertoire_detail", "Detalle de la Obra") {
        fun routeWithArgs(workId: String): String {
            return "repertoire_detail/$workId"
        }
    }

    object EventDetail : Screen("event_detail", "Detalles del Evento") {
        fun routeWithArgs(eventId: String): String {
            return "event_detail/$eventId"
        }
    }
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
    val startRoute = if (AuthRepositoryImpl().currentUserEmail() != null)
        Screen.Home.route
    else
        "login"

    NavHost(navController = navController, startDestination = startRoute) {
        authGraph(navController)
        mainGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    composable("login") {
        LoginScreen(
            onNavigateToRegister = { navController.navigate("register") },
            onLoginSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }
    composable("register") {
        RegisterScreen(
            onNavigateToLogin = { navController.popBackStack() },
            onRegisterSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    val mainScreens =
        listOf(Screen.Home, Screen.Notifications, Screen.Profile, Screen.Events, Screen.Repertoire)
    mainScreens.forEach { screen ->
        composable(screen.route) {
            MainScaffoldWithProviders(navController = navController, screen = screen)
        }
    }

    composable(
        route = Screen.AddEditRepertoire.route + "?workId={workId}",
        arguments = listOf(navArgument("workId") {
            type = NavType.StringType
            nullable = true
        })
    ) {
        val workId = it.arguments?.getString("workId")
        val title = if (workId == null) "Añadir obra" else "Editar obra"
        val addRepertoireViewModel: AddRepertoireViewModel = viewModel()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    actions = {
                        TextButton(onClick = { addRepertoireViewModel.onSave() }) {
                            Text("Guardar")
                        }
                    }
                )
            }
        ) { padding ->
            AddRepertoireScreen(
                modifier = Modifier.padding(padding),
                viewModel = addRepertoireViewModel,
                navController = navController
            )
        }
    }

    composable(
        route = Screen.RepertoireDetail.route + "/{workId}",
        arguments = listOf(navArgument("workId") { type = NavType.StringType })
    ) {
        RepertoireDetailScreen(navController = navController)
    }

    composable(
        route = Screen.EventDetail.route + "/{eventId}",
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })
    ) {
        EventDetailScreen(navController = navController)
    }

    composable(
        route = Screen.AddEvent.route + "?eventId={eventId}",
        arguments = listOf(navArgument("eventId") {
            type = NavType.StringType
            nullable = true
        })
    ) { backStackEntry ->
        val eventId = backStackEntry.arguments?.getString("eventId")
        val title = if (eventId == null) "Añadir evento" else "Editar evento"
        val addEventViewModel: AddEventViewModel = viewModel()

        LaunchedEffect(key1 = eventId) {
            addEventViewModel.loadEventForEditing(eventId)
        }
        val isFormValid by addEventViewModel.isFormValid.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { addEventViewModel.onSaveEvent() },
                            enabled = isFormValid
                        ) {
                            Text("Guardar")
                        }
                    }
                )
            }
        ) { padding ->
            AddEventScreen(
                modifier = Modifier.padding(padding),
                navController = navController,
                viewModel = addEventViewModel
            )
        }
    }
}

@Composable
private fun MainScaffoldWithProviders(navController: NavHostController, screen: Screen) {
    val notificationsViewModel: NotificationsViewModel = viewModel()
    val isDirector by notificationsViewModel.isDirector.collectAsState()

    val topBarActions: @Composable RowScope.() -> Unit =
        if (screen == Screen.Notifications && isDirector) {
            {
                IconButton(onClick = { notificationsViewModel.onDeleteRequest() }) {
                    Icon(Icons.Default.Delete, "Eliminar notificaciones")
                }
            }
        } else {
            {}
        }

    MainScaffold(
        navController = navController,
        title = screen.title,
        actions = topBarActions
    ) { padding ->
        Box(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (screen) {
                Screen.Home -> HomeScreen()
                Screen.Notifications -> NotificationsScreen(viewModel = notificationsViewModel)
                Screen.Profile -> ProfileScreen()
                Screen.Events -> EventsScreen(navController)
                Screen.Repertoire -> RepertoireScreen(navController)
                else -> {/* ... */}
            }
        }
    }
}

@Composable
fun MainScaffold(
    navController: NavHostController,
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val mainViewModel: MainViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val eventsViewModel: EventsViewModel = viewModel()
    val repertoireViewModel: RepertoireViewModel = viewModel()
    val notificationsViewModel: NotificationsViewModel = viewModel()
    val activity = LocalActivity.current
    val context = LocalContext.current

    val showDeleteDialog1 by mainViewModel.showDeleteDialog1.collectAsState()
    val showDeleteDialog2 by mainViewModel.showDeleteDialog2.collectAsState()
    val deleteError by mainViewModel.deleteError.collectAsState()

    LaunchedEffect(deleteError) {
        deleteError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            mainViewModel.onDismissDeleteDialogs()
        }
    }

    if (showDeleteDialog1) {
        AlertDialog(
            onDismissRequest = { mainViewModel.onDismissDeleteDialogs() },
            title = { Text("¿Eliminar cuenta?") },
            text = { Text("Esta acción es permanente y eliminará todos tus datos. ¿Estás seguro?") },
            confirmButton = {
                Button(
                    onClick = { mainViewModel.onConfirmDelete1() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sí, eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mainViewModel.onDismissDeleteDialogs() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showDeleteDialog2) {
        AlertDialog(
            onDismissRequest = { mainViewModel.onDismissDeleteDialogs() },
            title = { Text("¿Estás completamente seguro?") },
            text = { Text("No podrás recuperar tu cuenta ni tus datos. Esta es tu última oportunidad para cancelar.") },
            confirmButton = {
                TextButton(onClick = { mainViewModel.onDismissDeleteDialogs() }) {
                    Text("No, cancelar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        mainViewModel.onConfirmDelete2()
                        navController.navigate("login") { popUpTo(0) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sí, estoy seguro")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    onLogout = {
                        homeViewModel.cancelarRecoleccion()
                        eventsViewModel.cancelarRecoleccion()
                        repertoireViewModel.cancelarRecoleccion()
                        notificationsViewModel.cancelarRecoleccion()
                        mainViewModel.logout()
                        navController.navigate("login") { popUpTo(0) }
                    },
                    onExit = { activity?.finish() },
                    onDeleteAccount = {
                        scope.launch { drawerState.close() }
                        mainViewModel.onDeleteAccountRequest()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBarWithDrawer(
                    title = title,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    actions = actions
                )
            },
            bottomBar = { BottomNavigationBar(navController) },
            content = content
        )
    }
}