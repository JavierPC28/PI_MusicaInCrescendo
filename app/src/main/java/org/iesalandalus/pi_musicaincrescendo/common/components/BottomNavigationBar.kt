package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.iesalandalus.pi_musicaincrescendo.R

// Define la altura de la barra de navegación.
private val BottomBarHeight = 80.dp

// Define el tamaño de los iconos.
private val IconSize = 40.dp

// Define el padding para el marco del icono seleccionado.
private val FramePadding = 6.dp
private val FrameSize = IconSize + FramePadding * 2

// Define el radio de las esquinas para las imágenes y el marco.
private val ImageCornerRadius = 8.dp

/**
 * Define los elementos de navegación de la barra inferior.
 * @param route La ruta de navegación.
 * @param iconResNormal El recurso del icono para el estado normal.
 * @param iconResPressed El recurso del icono para el estado seleccionado.
 * @param description La descripción del ítem para accesibilidad.
 */
enum class NavItem(
    val route: String,
    val iconResNormal: Int,
    val iconResPressed: Int,
    val description: String
) {
    Events("events", R.drawable.events, R.drawable.events_pulsado, "Eventos"),
    Repertoire("repertoire", R.drawable.repertorio, R.drawable.repertorio_pulsado, "Repertorio"),
    Home("home", R.drawable.banda_alcolea, R.drawable.banda_alcolea, "Inicio"),
    Notifications(
        "notifications",
        R.drawable.notificaciones,
        R.drawable.notificaciones_pulsado,
        "Notificaciones"
    ),
    Profile("profile", R.drawable.ajustes, R.drawable.ajustes_pulsado, "Perfil")
}

/**
 * Barra de navegación inferior personalizada.
 * @param navController Controlador de navegación para gestionar las rutas.
 */
@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    androidx.compose.material3.BottomAppBar(
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
        modifier = Modifier.height(BottomBarHeight)
    ) {
        NavItem.entries.forEach { item ->
            BottomBarIcon(
                item = item,
                isSelected = currentRoute == item.route,
                onClick = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            // Evita acumular rutas en la pila de navegación.
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            // Asegura que solo haya una instancia de la pantalla.
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

/**
 * Icono individual para la barra de navegación inferior.
 * @param item El objeto NavItem a representar.
 * @param isSelected Indica si el ítem está seleccionado.
 * @param onClick La acción a ejecutar al pulsar el icono.
 */
@Composable
private fun RowScope.BottomBarIcon(
    item: NavItem,
    isSelected: Boolean,
    onClick: (String) -> Unit
) {
    val iconRes = if (isSelected) item.iconResPressed else item.iconResNormal
    val imageShape = RoundedCornerShape(ImageCornerRadius)

    Box(
        modifier = Modifier
            .weight(1f)
            .height(FrameSize),
        contentAlignment = Alignment.Center
    ) {
        // Muestra un marco de fondo si el ítem de inicio está seleccionado.
        if (item == NavItem.Home && isSelected) {
            Box(
                modifier = Modifier
                    .size(FrameSize)
                    .clip(imageShape)
                    .background(
                        androidx.compose.material3.MaterialTheme.colorScheme.primary
                            .copy(alpha = 0.2f)
                    )
            )
        }

        // Contenedor del icono con gestión de clics.
        Box(
            modifier = Modifier
                .size(IconSize)
                .clip(imageShape)
                .clickable { onClick(item.route) }
        ) {
            Image(
                painter = androidx.compose.ui.res.painterResource(id = iconRes),
                contentDescription = item.description,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}