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
 * Campo de texto para contraseña con visibilidad controlada
 * y validación de criterios mínimos.
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
                        id = if (visible) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                    ),
                    contentDescription = if (visible) "Ocultar contraseña" else "Mostrar contraseña",
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