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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val startRoute = if (AuthRepositoryImpl().currentUserEmail() != null)
        Screen.Home.route
    else
        "login"

    NavHost(navController = navController, startDestination = startRoute) {
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

        listOf(
            Screen.Home,
            Screen.Notifications,
            Screen.Profile
        ).forEach { screen ->
            composable(screen.route) {
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
                            else -> {/* ... */
                            }
                        }
                    }
                }
            }
        }

        composable(Screen.Events.route) {
            val eventsViewModel: EventsViewModel = viewModel()
            MainScaffold(navController, Screen.Events.title) { padding ->
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    EventsScreen(navController, eventsViewModel)
                }
            }
        }

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

        composable(
            route = "add_repertoire?workId={workId}",
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
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver"
                                )
                            }
                        },
                        actions = {
                            TextButton(onClick = {
                                addRepertoireViewModel.onSave()
                            }) {
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
                    AddRepertoireScreen(
                        viewModel = addRepertoireViewModel,
                        navController = navController
                    )
                }
            }
        }

        composable(
            route = "repertoire_detail/{workId}",
            arguments = listOf(navArgument("workId") { type = NavType.StringType })
        ) {
            RepertoireDetailScreen(navController = navController)
        }

        composable(
            route = "event_detail/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) {
            EventDetailScreen(navController = navController)
        }

        composable(
            route = "add_event?eventId={eventId}",
            arguments = listOf(navArgument("eventId") {
                type = NavType.StringType
                nullable = true
            })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            val title = if (eventId == null) "Añadir evento" else "Editar evento"
            val addEventViewModel: AddEventViewModel = viewModel()

            LaunchedEffect(key1 = Unit) {
                addEventViewModel.loadEventForEditing(eventId)
            }

            val isFormValid by addEventViewModel.isFormValid.collectAsState()

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(title) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver"
                                )
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
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    AddEventScreen(navController = navController, viewModel = addEventViewModel)
                }
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
    // Instanciamos todos los ViewModels que puedan tener listeners activos
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

    // Observador de errores de eliminación
    LaunchedEffect(deleteError) {
        deleteError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            mainViewModel.onDismissDeleteDialogs() // Resetea el error
        }
    }

    // Primer diálogo de confirmación
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

    // Segundo diálogo de confirmación (botones invertidos)
    if (showDeleteDialog2) {
        AlertDialog(
            onDismissRequest = { mainViewModel.onDismissDeleteDialogs() },
            title = { Text("¿Estás completamente seguro?") },
            text = { Text("No podrás recuperar tu cuenta ni tus datos. Esta es tu última oportunidad para cancelar.") },
            confirmButton = {
                // El botón de cancelar aparece a la derecha en este diálogo
                TextButton(onClick = { mainViewModel.onDismissDeleteDialogs() }) {
                    Text("No, cancelar")
                }
            },
            dismissButton = {
                // El botón de confirmación final aparece a la izquierda
                Button(
                    onClick = {
                        mainViewModel.onConfirmDelete2()
                        // Navega al login inmediatamente después de la confirmación final
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
                        // Cancelamos la recolección de datos de todos los listeners
                        // para evitar crashes por permisos al desloguear.
                        homeViewModel.cancelarRecoleccion()
                        eventsViewModel.cancelarRecoleccion()
                        repertoireViewModel.cancelarRecoleccion()
                        notificationsViewModel.cancelarRecoleccion()

                        // Procedemos con el logout y la navegación
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