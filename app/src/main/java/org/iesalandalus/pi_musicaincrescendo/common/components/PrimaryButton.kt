package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Botón principal reutilizable con un estilo coherente en toda la aplicación.
 * @param text El texto que se mostrará en el botón.
 * @param onClick La acción a ejecutar al pulsar el botón.
 * @param modifier Modificador para personalizar el estilo.
 * @param enabled Controla si el botón está habilitado o no.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text)
    }
}