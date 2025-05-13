package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.iesalandalus.pi_musicaincrescendo.R

// Altura personalizada para la barra de navegación
private val BottomBarHeight = 80.dp

// Tamaño de iconos
private val IconSize = 40.dp

// Tamaño del cuadro de selección para el icono de Home
private val HomeSelectedFrame = IconSize

// Definición de cada elemento de navegación
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
    var modifier = Modifier.weight(1f)

    if (item == NavItem.Home && isSelected) {
        modifier = modifier
            .size(HomeSelectedFrame)
            .clip(CircleShape)
            .background(
                androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                CircleShape
            )
    }

    androidx.compose.material3.IconButton(
        onClick = { onClick(item.route) },
        modifier = modifier
    ) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = iconRes),
            contentDescription = item.description,
            modifier = Modifier
                .size(IconSize)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}