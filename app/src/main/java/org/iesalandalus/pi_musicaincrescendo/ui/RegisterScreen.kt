package org.iesalandalus.pi_musicaincrescendo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.iesalandalus.pi_musicaincrescendo.common.components.InputField
import org.iesalandalus.pi_musicaincrescendo.common.components.PrimaryButton

/**
 * Pantalla de registro.
 */
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isDirector by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputField(
            value = email,
            onValueChange = { email = it },
            label = "Correo electrónico"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            value = password,
            onValueChange = { password = it },
            label = "Contraseña"
        )
        Spacer(modifier = Modifier.height(8.dp))
        InputField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirmar contraseña"
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isDirector,
                onCheckedChange = { isDirector = it }
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "Soy director")
        }
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryButton(
            text = "Registrar",
            onClick = { /* Navegación futura */ }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateToLogin) {
            Text(text = "¿Ya tienes cuenta? Inicia sesión")
        }
    }
}