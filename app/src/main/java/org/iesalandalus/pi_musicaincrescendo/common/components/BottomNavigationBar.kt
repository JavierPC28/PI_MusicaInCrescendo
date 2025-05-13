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

// Altura personalizada para la barra de navegación
private val BottomBarHeight = 80.dp

// Tamaño de iconos y bordes
private val IconSize = 40.dp

// Espacio de marco: 3dp por cada lado para sobresalir del icono
private val FramePadding = 6.dp
private val FrameSize = IconSize + FramePadding * 2

// Radio de esquinas para imágenes y marco
private val ImageCornerRadius = 8.dp

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
                            popUpTo(navController.graph.startDestinationId) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}

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
        // Marco de fondo para el ítem seleccionado (vista Home)
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

        // Imagen cuadrada con bordes redondeados a 8dp y margen de marco
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