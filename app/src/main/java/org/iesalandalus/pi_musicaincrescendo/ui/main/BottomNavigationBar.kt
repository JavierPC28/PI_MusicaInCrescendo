package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.iesalandalus.pi_musicaincrescendo.R

// Altura personalizada para la barra de navegación
private val BottomBarHeight = 80.dp

// Tamaño de iconos
private val IconSize = 40.dp

// Margen superior para alinear los iconos hacia arriba
private val IconTopPadding = 4.dp

// Definición de cada elemento de navegación
enum class NavItem(val route: String, val iconRes: Int, val description: String) {
    Events("events", R.drawable.events, "Eventos"),
    Repertoire("repertoire", R.drawable.repertorio, "Repertorio"),
    Home("home", R.drawable.banda_alcolea, "Inicio"),
    Notifications("notifications", R.drawable.notificaciones, "Notificaciones"),
    Profile("profile", R.drawable.ajustes, "Perfil")
}

@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.height(BottomBarHeight)
    ) {
        NavItem.entries.forEach { item ->
            BottomBarIcon(
                item = item,
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
    onClick: (String) -> Unit
) {
    IconButton(
        onClick = { onClick(item.route) },
        modifier = Modifier
            .weight(1f)
            .padding(top = IconTopPadding)
    ) {
        Icon(
            painter = androidx.compose.ui.res.painterResource(id = item.iconRes),
            contentDescription = item.description,
            modifier = Modifier.height(IconSize)
        )
    }
}