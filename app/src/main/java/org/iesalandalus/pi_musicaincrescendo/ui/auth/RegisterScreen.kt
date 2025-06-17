package org.iesalandalus.pi_musicaincrescendo.ui.auth

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
import org.iesalandalus.pi_musicaincrescendo.common.components.*
import org.iesalandalus.pi_musicaincrescendo.common.utils.ImageHelper
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RegisterViewModel

// Opciones disponibles para el selector de género.
private val genderOptions = listOf(
    "Hombre",
    "Mujer",
    "Prefiero no decirlo"
)

/**
 * Composable para el checkbox que permite al usuario registrarse como director.
 * @param isDirector Estado actual del checkbox.
 * @param onDirectorChecked Callback que se ejecuta al cambiar el estado.
 */
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
        Text(text = "Soy director/a")
    }
}

/**
 * Composable que define la pantalla de registro de usuario.
 * @param viewModel El ViewModel que gestiona la lógica de registro.
 * @param onNavigateToLogin Callback para navegar de vuelta a la pantalla de login.
 * @param onRegisterSuccess Callback que se ejecuta cuando el registro es exitoso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val activity = LocalActivity.current
    val context = LocalContext.current

    // Recolecta los estados del ViewModel.
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

    // Determina si todos los campos del formulario son válidos.
    val isFieldsValid = remember(
        email, isEmailValid,
        password, isPasswordValid,
        confirmPassword, isConfirmPasswordValid
    ) {
        email.isNotBlank() && isEmailValid &&
                password.isNotBlank() && isPasswordValid &&
                confirmPassword.isNotBlank() && isConfirmPasswordValid
    }

    // Efecto que navega a la pantalla principal cuando el registro es exitoso.
    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            onRegisterSuccess()
        }
    }

    // Maneja el botón de retroceso para cerrar la actividad.
    BackHandler { activity?.finish() }

    /**
     * Encapsula la lógica del clic en el botón "Registrarse".
     * Valida que se haya seleccionado un género antes de proceder.
     */
    fun handleRegisterClick() {
        if (gender == "-- Seleccione su género --") {
            Toast.makeText(
                context,
                "Por favor, seleccione un sexo",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            viewModel.onRegister()
        }
    }

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
            // Imagen de perfil que cambia según el género y si es director.
            val profileRes = ImageHelper.getProfileImage(gender, isDirector)
            Image(
                painter = painterResource(id = profileRes),
                contentDescription = "Imagen de perfil",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )

            // Campos del formulario.
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

            // Botón principal para registrarse.
            PrimaryButton(
                text = "Registrarse",
                onClick = { handleRegisterClick() },
                enabled = isFieldsValid
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Botón para volver a la pantalla de inicio de sesión.
            TextButton(onClick = onNavigateToLogin) {
                Text(text = "¿Ya tienes cuenta? Inicia sesión")
            }

            // Muestra un mensaje de error si existe.
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