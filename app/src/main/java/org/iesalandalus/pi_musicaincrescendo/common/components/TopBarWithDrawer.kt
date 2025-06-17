package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import org.iesalandalus.pi_musicaincrescendo.R

/**
 * Barra de aplicación superior (TopAppBar) que incluye un icono para abrir un menú lateral (Drawer).
 * @param title El título a mostrar en la barra.
 * @param onMenuClick La acción a ejecutar cuando se pulsa el icono del menú.
 * @param actions Composables opcionales para acciones adicionales a la derecha.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithDrawer(
    title: String,
    onMenuClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(id = R.drawable.menu_hamburguesa),
                    contentDescription = "Abrir menú"
                )
            }
        },
        actions = actions
    )
}