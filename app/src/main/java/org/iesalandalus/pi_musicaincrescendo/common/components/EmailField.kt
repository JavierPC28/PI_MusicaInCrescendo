package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Campo de texto para correo electrónico con sugerencias desactivadas
 * y validación de formato.
 */
@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
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