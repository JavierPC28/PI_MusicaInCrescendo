package org.iesalandalus.pi_musicaincrescendo.ui

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.common.components.*
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RegisterViewModel

private val genderOptions = listOf(
    "Hombre",
    "Mujer",
    "Prefiero no decirlo"
)

@Composable
private fun ProfileImage(gender: String, isDirector: Boolean) {
    val imageRes = when {
        gender == "Mujer" && isDirector -> R.drawable.perfil_directora
        gender == "Mujer" && !isDirector -> R.drawable.perfil_alumna
        gender == "Hombre" && isDirector -> R.drawable.perfil_director
        gender == "Hombre" && !isDirector -> R.drawable.perfil_alumno
        else -> R.drawable.perfil_neutro
    }
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Imagen de perfil",
        modifier = Modifier
            .size(100.dp)
            .padding(bottom = 16.dp)
    )
}

@Composable
private fun DirectorCheckbox(isDirector: Boolean, onDirectorChecked: (Boolean) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDirectorChecked(!isDirector) }
    ) {
        Checkbox(
            checked = isDirector,
            onCheckedChange = onDirectorChecked
        )
        Spacer(Modifier.width(8.dp))
        Text(text = "Soy director")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val activity = LocalActivity.current
    val context = LocalContext.current

    val email by viewModel.email.collectAsState()
    val isEmailValid by viewModel.isEmailValid.collectAsState()
    val password by viewModel.password.collectAsState()
    val isPasswordValid by viewModel.isPasswordValid.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val isConfirmPasswordValid by viewModel.isConfirmPasswordValid.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val isDirector by viewModel.isDirector.collectAsState()
    val registrationSuccess by viewModel.registrationSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val isFieldsValid = remember(
        email, isEmailValid,
        password, isPasswordValid,
        confirmPassword, isConfirmPasswordValid
    ) {
        email.isNotBlank() && isEmailValid &&
                password.isNotBlank() && isPasswordValid &&
                confirmPassword.isNotBlank() && isConfirmPasswordValid
    }

    // Si el registro fue exitoso, navegamos a home
    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            onRegisterSuccess()
        }
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
            ProfileImage(gender = gender, isDirector = isDirector)

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

            DirectorCheckbox(
                isDirector = isDirector,
                onDirectorChecked = viewModel::onDirectorChecked
            )
            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Registrarse",
                onClick = {
                    if (gender == "-- Seleccione su género --") {
                        Toast.makeText(
                            context,
                            "Por favor, seleccione un sexo",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        viewModel.onRegister()
                    }
                },
                enabled = isFieldsValid
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onNavigateToLogin) {
                Text(text = "¿Ya tienes cuenta? Inicia sesión")
            }

            errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}