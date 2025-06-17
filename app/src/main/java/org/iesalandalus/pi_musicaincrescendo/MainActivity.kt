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

/**
 * Define las rutas de navegación de la aplicación.
 * @param route La ruta única para la pantalla.
 * @param title El título a mostrar en la barra superior.
 */
sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Inicio")
    object Events : Screen("events", "Eventos")
    object Repertoire : Screen("repertoire", "Repertorio")
    object Notifications : Screen("notifications", "Notificaciones")
    object Profile : Screen("profile", "Perfil")

    // Pantalla para añadir/editar evento con argumento opcional
    object AddEvent : Screen("add_event", "Añadir Evento") {
        fun routeWithArgs(eventId: String? = null): String {
            return if (eventId != null) "add_event?eventId=$eventId" else "add_event"
        }
    }

    // Pantalla para añadir/editar obra con argumento opcional
    object AddEditRepertoire : Screen("add_repertoire", "Añadir obra") {
        fun routeWithArgs(workId: String? = null): String {
            return if (workId != null) "add_repertoire?workId=$workId" else "add_repertoire"
        }
    }

    // Pantalla de detalle de obra con argumento obligatorio
    object RepertoireDetail : Screen("repertoire_detail", "Detalle de la Obra") {
        fun routeWithArgs(workId: String): String {
            return "repertoire_detail/$workId"
        }
    }

    // Pantalla de detalle de evento con argumento obligatorio
    object EventDetail : Screen("event_detail", "Detalles del Evento") {
        fun routeWithArgs(eventId: String): String {
            return "event_detail/$eventId"
        }
    }
}

/**
 * Actividad principal de la aplicación.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PI_MusicaInCrescendoTheme {
                // Configura el color de la barra de estado para que sea transparente
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
                    // Configura el grafo de navegación
                    AppNavHost()
                }
            }
        }
    }
}

/**
 * Composable que gestiona el NavHost y la ruta inicial.
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    // Determina la ruta inicial basada en si el usuario ya ha iniciado sesión
    val startRoute = if (AuthRepositoryImpl().currentUserEmail() != null)
        Screen.Home.route
    else
        "login"

    NavHost(navController = navController, startDestination = startRoute) {
        authGraph(navController)
        mainGraph(navController)
    }
}

/**
 * Define el subgrafo de navegación para la autenticación (inicio de sesión y registro).
 * @param navController Controlador de navegación.
 */
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

/**
 * Define el grafo de navegación principal para las pantallas post-autenticación.
 * @param navController Controlador de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
private fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    val mainScreens =
        listOf(Screen.Home, Screen.Notifications, Screen.Profile, Screen.Events, Screen.Repertoire)
    // Crea una ruta para cada pantalla principal
    mainScreens.forEach { screen ->
        composable(screen.route) {
            MainScaffoldWithProviders(navController = navController, screen = screen)
        }
    }

    // Ruta para añadir/editar repertorio
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

    // Ruta para detalles del repertorio
    composable(
        route = Screen.RepertoireDetail.route + "/{workId}",
        arguments = listOf(navArgument("workId") { type = NavType.StringType })
    ) {
        RepertoireDetailScreen(navController = navController)
    }

    // Ruta para detalles del evento
    composable(
        route = Screen.EventDetail.route + "/{eventId}",
        arguments = listOf(navArgument("eventId") { type = NavType.StringType })
    ) {
        EventDetailScreen(navController = navController)
    }

    // Ruta para añadir/editar evento
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

        // Carga los datos del evento si se está editando
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

/**
 * Contenedor que proporciona ViewModels y la estructura Scaffold para las pantallas principales.
 * @param navController Controlador de navegación.
 * @param screen La pantalla actual.
 */
@Composable
private fun MainScaffoldWithProviders(navController: NavHostController, screen: Screen) {
    val notificationsViewModel: NotificationsViewModel = viewModel()
    val isDirector by notificationsViewModel.isDirector.collectAsState()

    // Define acciones específicas para la barra superior según la pantalla y el rol
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
            // Renderiza el contenido de la pantalla actual
            when (screen) {
                Screen.Home -> HomeScreen()
                Screen.Notifications -> NotificationsScreen(viewModel = notificationsViewModel)
                Screen.Profile -> ProfileScreen()
                Screen.Events -> EventsScreen(navController)
                Screen.Repertoire -> RepertoireScreen(navController)
                else -> {/* No-op */
                }
            }
        }
    }
}

/**
 * Estructura principal de la aplicación con Drawer, TopBar y BottomBar.
 * @param navController Controlador de navegación.
 * @param title Título de la pantalla actual.
 * @param actions Acciones para la barra superior.
 * @param content Contenido de la pantalla.
 */
@Composable
fun MainScaffold(
    navController: NavHostController,
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    // Instancias de ViewModels necesarios en este scope
    val mainViewModel: MainViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val eventsViewModel: EventsViewModel = viewModel()
    val repertoireViewModel: RepertoireViewModel = viewModel()
    val notificationsViewModel: NotificationsViewModel = viewModel()
    val activity = LocalActivity.current
    val context = LocalContext.current

    // Estados para los diálogos de eliminación de cuenta
    val showDeleteDialog1 by mainViewModel.showDeleteDialog1.collectAsState()
    val showDeleteDialog2 by mainViewModel.showDeleteDialog2.collectAsState()
    val deleteError by mainViewModel.deleteError.collectAsState()

    // Muestra un Toast si hay un error al eliminar
    LaunchedEffect(deleteError) {
        deleteError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            mainViewModel.onDismissDeleteDialogs()
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

    // Segundo diálogo de confirmación (más severo)
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
                        // Cancela la recolección de datos en tiempo real y cierra sesión
                        homeViewModel.cancelarRecoleccion()
                        eventsViewModel.cancelarRecoleccion()
                        repertoireViewModel.cancelarRecoleccion()
                        notificationsViewModel.cancelarRecoleccion()
                        mainViewModel.logout()
                        navController.navigate("login") { popUpTo(0) }
                    },
                    onExit = { activity?.finish() }, // Cierra la app
                    onDeleteAccount = {
                        scope.launch { drawerState.close() }
                        mainViewModel.onDeleteAccountRequest() // Inicia el flujo de eliminación de cuenta
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