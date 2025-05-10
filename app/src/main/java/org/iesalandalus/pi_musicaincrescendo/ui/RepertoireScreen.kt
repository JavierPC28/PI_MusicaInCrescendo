package org.iesalandalus.pi_musicaincrescendo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

/**
 * Vista de repertorio.
 */
@Composable
fun RepertoireScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Esta es la vista del repertorio",
            fontSize = 20.sp
        )
    }
}