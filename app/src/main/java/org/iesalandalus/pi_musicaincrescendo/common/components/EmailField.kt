package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions

/**
 * Campo de texto reutilizable para la introducción de un correo electrónico.
 * Incluye validación visual de formato y un icono representativo.
 * @param value El valor actual del campo.
 * @param onValueChange Callback que se ejecuta cuando el valor cambia.
 * @param modifier Modificador para personalizar el estilo.
 * @param isError Indica si el valor actual es inválido.
 * @param errorMessage Mensaje de error a mostrar si isError es true.
 */
@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Correo electrónico") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Icono correo"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                autoCorrectEnabled = false
            ),
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )
        // Muestra el mensaje de error si es necesario.
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}