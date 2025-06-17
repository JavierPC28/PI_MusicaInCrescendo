package org.iesalandalus.pi_musicaincrescendo.ui.auth

import android.app.Activity
import android.content.Context
import androidx.activity.compose.*
import androidx.activity.result.ActivityResultLauncher
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.common.components.*
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.LoginViewModel

/**
 * Maneja el resultado del inicio de sesión con Google.
 * @param result El resultado de la actividad de inicio de sesión.
 * @param scope El alcance de la corutina para operaciones asíncronas.
 * @param context El contexto de la aplicación.
 * @param viewModel El ViewModel para procesar el token de Google.
 */
@Suppress("DEPRECATION")
private fun handleGoogleSignIn(
    result: androidx.activity.result.ActivityResult,
    scope: CoroutineScope,
    context: Context,
    viewModel: LoginViewModel
) {
    if (result.resultCode == Activity.RESULT_OK) {
        scope.launch {
            try {
                val oneTapClient = Identity.getSignInClient(context)
                val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val googleIdToken = credential.googleIdToken
                if (googleIdToken != null) {
                    viewModel.onGoogleLogin(googleIdToken)
                } else {
                    // Manejo de error futuro.
                }
            } catch (_: ApiException) {
                // Manejo de error futuro.
            }
        }
    }
}

/**
 * Inicia el flujo de inicio de sesión con Google One Tap.
 * @param scope El alcance de la corutina.
 * @param context El contexto de la aplicación.
 * @param launcher El lanzador de actividad para mostrar la UI de inicio de sesión.
 */
@Suppress("DEPRECATION")
private fun startGoogleSignIn(
    scope: CoroutineScope,
    context: Context,
    launcher: ActivityResultLauncher<IntentSenderRequest>
) {
    scope.launch {
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
            launcher.launch(
                IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
            )
        } catch (_: Exception) {
            // Manejo de error si los servicios de Google Play no están disponibles.
        }
    }
}


/**
 * Composable que define la pantalla de inicio de sesión.
 * @param viewModel El ViewModel que gestiona la lógica de la pantalla.
 * @param onNavigateToRegister Callback para navegar a la pantalla de registro.
 * @param onLoginSuccess Callback que se ejecuta cuando el inicio de sesión es exitoso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    // Resetea el estado del ViewModel al entrar en la pantalla.
    LaunchedEffect(Unit) {
        viewModel.reset()
    }

    val activity = LocalActivity.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Recolecta los estados del ViewModel.
    val email by viewModel.email.collectAsState()
    val isEmailValid by viewModel.isEmailValid.collectAsState()
    val password by viewModel.password.collectAsState()
    val isPasswordValid by viewModel.isPasswordValid.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Determina si el formulario es válido para habilitar el botón de login.
    val isFormValid = email.isNotBlank() && isEmailValid && password.isNotBlank() && isPasswordValid

    // Prepara el lanzador para el resultado del inicio de sesión con Google.
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        handleGoogleSignIn(result, coroutineScope, context, viewModel)
    }

    // Navega cuando el inicio de sesión es exitoso.
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLoginSuccess()
        }
    }

    // Maneja el botón de retroceso para cerrar la actividad.
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

            // Campo de texto para el correo electrónico.
            EmailField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                isError = !isEmailValid,
                errorMessage = if (!isEmailValid) "Formato de correo inválido" else null
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Campo de texto para la contraseña.
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

            // Botón principal para iniciar sesión.
            PrimaryButton(
                text = "Entrar",
                onClick = viewModel::onLogin,
                enabled = isFormValid
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Botón para navegar a la pantalla de registro.
            TextButton(onClick = onNavigateToRegister) {
                Text(text = "¿No tienes cuenta? Regístrate")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Separador visual.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = "O",
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón para iniciar sesión con Google.
            GoogleSignInButton(
                onClick = { startGoogleSignIn(coroutineScope, context, googleSignInLauncher) }
            )

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