package org.iesalandalus.pi_musicaincrescendo.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.common.components.EmailField
import org.iesalandalus.pi_musicaincrescendo.common.components.PasswordField
import org.iesalandalus.pi_musicaincrescendo.common.components.GenderSelector
import org.iesalandalus.pi_musicaincrescendo.common.components.PrimaryButton
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RegisterViewModel

/**
 * Pantalla de registro.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = viewModel(),
    onNavigateToLogin: () -> Unit
) {
    val activity = LocalActivity.current

    val email = viewModel.email.collectAsState().value
    val password = viewModel.password.collectAsState().value
    val confirmPassword = viewModel.confirmPassword.collectAsState().value
    val gender = viewModel.gender.collectAsState().value
    val isDirector = viewModel.isDirector.collectAsState().value
    val genderOptions = listOf("Hombre", "Mujer", "Prefiero no decirlo")

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
                painter = painterResource(id = R.drawable.perfil_neutro),
                contentDescription = "Perfil neutro",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 16.dp)
            )

            EmailField(value = email, onValueChange = viewModel::onEmailChange)
            Spacer(modifier = Modifier.height(8.dp))

            PasswordField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = "Contraseña"
            )
            Spacer(modifier = Modifier.height(8.dp))

            PasswordField(
                value = confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChange,
                label = "Confirmar contraseña"
            )
            Spacer(modifier = Modifier.height(8.dp))

            GenderSelector(
                options = genderOptions,
                selected = gender,
                onSelected = viewModel::onGenderSelected
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isDirector, onCheckedChange = viewModel::onDirectorChecked)
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