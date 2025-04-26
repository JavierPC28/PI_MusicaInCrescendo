package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import org.iesalandalus.pi_musicaincrescendo.R

/**
 * Campo de texto para contraseña con visibilidad controlada.
 */
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            Icon(
                painter = painterResource(
                    id = if (visible) R.drawable.ic_visibility_off else R.drawable.ic_visibility
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
        modifier = modifier.fillMaxWidth()
    )
}