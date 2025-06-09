package org.iesalandalus.pi_musicaincrescendo.ui.auth

import android.app.Activity
import androidx.activity.compose.*
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.common.components.*
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    // Resetea estado al entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.reset()
    }

    val activity = LocalActivity.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val email by viewModel.email.collectAsState()
    val isEmailValid by viewModel.isEmailValid.collectAsState()

    val password by viewModel.password.collectAsState()
    val isPasswordValid by viewModel.isPasswordValid.collectAsState()

    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Habilita el botón solo si los campos están completos y válidos
    val isFormValid = email.isNotBlank() && isEmailValid && password.isNotBlank() && isPasswordValid

    // Launcher para el resultado de Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            coroutineScope.launch {
                try {
                    val oneTapClient = Identity.getSignInClient(context)
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val googleIdToken = credential.googleIdToken
                    if (googleIdToken != null) {
                        viewModel.onGoogleLogin(googleIdToken)
                    } else {
                        // Para manejar errores
                    }
                } catch (_: ApiException) {
                    // Para manejar errores
                }
            }
        }
    }

    // Función para iniciar el flujo de Google Sign-In
    fun startGoogleSignIn() {
        coroutineScope.launch {
            val oneTapClient = Identity.getSignInClient(context)
            val signInRequest =
                com.google.android.gms.auth.api.identity.BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setServerClientId(context.getString(R.string.google_user_key))
                            .setFilterByAuthorizedAccounts(false)
                            .build()
                    )
                    .build()

            try {
                val result = oneTapClient.beginSignIn(signInRequest).await()
                googleSignInLauncher.launch(
                    IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                )
            } catch (_: Exception) {
                // Manejar error, por ejemplo, si no hay servicios de Google Play
            }
        }
    }

    // Si el login fue exitoso, navegamos a home
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLoginSuccess()
        }
    }

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

            TextButton(onClick = onNavigateToRegister) {
                Text(text = "¿No tienes cuenta? Regístrate")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de inicio de sesión con Google
            GoogleSignInButton(
                onClick = { startGoogleSignIn() }
            )

            // Mensaje de error debajo del formulario
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