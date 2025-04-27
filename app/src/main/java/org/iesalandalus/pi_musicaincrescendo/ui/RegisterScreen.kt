package org.iesalandalus.pi_musicaincrescendo.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RegisterViewModel

/**
 * Pantalla de registro.
 * Valida que confirmPassword coincida con password.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateToLogin: () -> Unit
) {
    val activity = LocalActivity.current

    val email by viewModel.email.collectAsState()
    val isEmailValid by viewModel.isEmailValid.collectAsState()

    val password by viewModel.password.collectAsState()
    val isPasswordValid by viewModel.isPasswordValid.collectAsState()

    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val isConfirmPasswordValid by viewModel.isConfirmPasswordValid.collectAsState()

    val gender by viewModel.gender.collectAsState()
    val isDirector by viewModel.isDirector.collectAsState()

    val genderOptions = listOf(
        "Hombre",
        "Mujer",
        "Prefiero no decirlo"
    )

    val imageRes = when {
        gender == "Mujer" && isDirector -> R.drawable.perfil_directora
        gender == "Mujer" && !isDirector -> R.drawable.perfil_alumna
        gender == "Hombre" && isDirector -> R.drawable.perfil_director
        gender == "Hombre" && !isDirector -> R.drawable.perfil_alumno
        else -> R.drawable.perfil_neutro
    }

    BackHandler { activity?.finish() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro") },
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Imagen de perfil",
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
            Spacer(modifier = Modifier.height(8.dp))

            PasswordField(
                value = confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = "Confirmar contraseña",
                isError = !isConfirmPasswordValid,
                errorMessage = if (!isConfirmPasswordValid) "Las contraseñas no coinciden" else null
            )
            Spacer(modifier = Modifier.height(8.dp))

            GenderSelector(
                options = genderOptions,
                selected = gender,
                onSelected = viewModel::onGenderSelected
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    viewModel.onDirectorChecked(!isDirector)
                }
            ) {
                Checkbox(
                    checked = isDirector,
                    onCheckedChange = viewModel::onDirectorChecked
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Soy director")
            }
            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(text = "Registrar", onClick = viewModel::onRegister)
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(text = "¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}