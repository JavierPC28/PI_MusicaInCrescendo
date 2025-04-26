package org.iesalandalus.pi_musicaincrescendo.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.iesalandalus.pi_musicaincrescendo.R

/**
 * Pantalla de registro.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit
) {
    val activity = LocalContext.current as Activity

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isDirector by remember { mutableStateOf(false) }
    var pwdVisible1 by remember { mutableStateOf(false) }
    var pwdVisible2 by remember { mutableStateOf(false) }

    val genderOptions = listOf("Hombre", "Mujer", "Prefiero no decirlo")
    var expanded by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf(genderOptions[0]) }

    BackHandler { activity.finish() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro") },
                actions = {
                    IconButton(onClick = { activity.finish() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cerrar app"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.perfil_neutro),
                contentDescription = "Perfil neutro",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Icono correo"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (pwdVisible1) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            id = if (pwdVisible1) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                        ),
                        contentDescription = if (pwdVisible1) "Ocultar contraseña" else "Mostrar contraseña",
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(onPress = {
                                pwdVisible1 = true
                                tryAwaitRelease()
                                pwdVisible1 = false
                            })
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                visualTransformation = if (pwdVisible2) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(
                            id = if (pwdVisible2) R.drawable.ic_visibility_off else R.drawable.ic_visibility
                        ),
                        contentDescription = if (pwdVisible2) "Ocultar contraseña" else "Mostrar contraseña",
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(onPress = {
                                pwdVisible2 = true
                                tryAwaitRelease()
                                pwdVisible2 = false
                            })
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedGender,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Sexo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                selectedGender = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isDirector, onCheckedChange = { isDirector = it })
                Spacer(Modifier.width(8.dp))
                Text(text = "Soy director")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Navegación futura */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Registrar")
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(text = "¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}