package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.iesalandalus.pi_musicaincrescendo.common.components.PrimaryButton

/**
 * Pantalla de inicio con botón temporal de cierre de sesión
 */
@Composable
fun HomeScreen(
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Bienvenido a la mejor app del mundo!",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Botón temporal para cerrar sesión (esto lo implementaré en otro lugar más adelante)
        PrimaryButton(
            text = "Cerrar Sesión",
            onClick = onLogout
        )
    }
}