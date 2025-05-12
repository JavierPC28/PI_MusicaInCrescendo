package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import org.iesalandalus.pi_musicaincrescendo.R

/**
 * Barra superior con icono hamburguesa.
 * @param title Título de la pantalla.
 * @param onMenuClick Acción al pulsar el icono.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithDrawer(
    title: String,
    onMenuClick: () -> Unit
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
        }
    )
}