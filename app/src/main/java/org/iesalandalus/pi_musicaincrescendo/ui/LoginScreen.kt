package org.iesalandalus.pi_musicaincrescendo.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.common.components.*
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.LoginViewModel

/**
 * Pantalla de inicio de sesión.
 * Añadimos un LaunchedEffect para resetear campos al entrar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToRegister: () -> Unit
) {
    // Aquí reseteamos automáticamente al componer por primera vez la pantalla
    LaunchedEffect(Unit) {
        viewModel.resetFields()
    }

    val activity = LocalActivity.current

    val email by viewModel.email.collectAsState()
    val isEmailValid by viewModel.isEmailValid.collectAsState()

    val password by viewModel.password.collectAsState()
    val isPasswordValid by viewModel.isPasswordValid.collectAsState()

    // Determinamos si el formulario es válido para habilitar el botón
    val isFormValid = email.isNotBlank() && isEmailValid && password.isNotBlank() && isPasswordValid

    BackHandler { activity?.finish() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio de Sesión") },
                actions = {
                    IconButton(onClick = { activity?.finish() }) {
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

            EmailField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                isError = !isEmailValid,
                errorMessage = if (!isEmailValid) "Formato de correo inválido" else null
            )
            Spacer(modifier = Modifier.height(8.dp))

            PasswordField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = "Contraseña",
                isError = !isPasswordValid,
                errorMessage = if (!isPasswordValid)
                    "Debe empezar por letra, tener ≥8 caracteres, un número y un carácter especial"
                else null
            )
            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Entrar",
                onClick = viewModel::onLogin,
                enabled = isFormValid
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    onNavigateToRegister()
                }
            ) {
                Text(text = "¿No tienes cuenta? Regístrate")
            }
        }
    }
}