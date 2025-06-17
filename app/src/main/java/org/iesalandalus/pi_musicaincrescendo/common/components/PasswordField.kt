package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import org.iesalandalus.pi_musicaincrescendo.R

/**
 * Campo de texto reutilizable para la introducción de contraseñas.
 * Permite alternar la visibilidad del contenido y muestra errores de validación.
 * @param value El valor actual del campo.
 * @param onValueChange Callback que se ejecuta cuando el valor cambia.
 * @param label El texto que se muestra como etiqueta del campo.
 * @param modifier Modificador para personalizar el estilo.
 * @param isError Indica si el valor actual es inválido.
 * @param errorMessage Mensaje de error a mostrar si isError es true.
 */
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var visible by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    painter = painterResource(
                        id = if (visible) R.drawable.ojo_abierto else R.drawable.ojo_cerrado
                    ),
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
                    // Permite mostrar la contraseña solo mientras se mantiene pulsado.
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(onPress = {
                            visible = true
                            tryAwaitRelease()
                            visible = false
                        })
                    }
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
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