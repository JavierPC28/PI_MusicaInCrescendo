package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen() {
    val viewModel: ProfileViewModel = viewModel()
    val displayName by viewModel.displayName.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(displayName) }

    // Diálogo de edición
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = RoundedCornerShape(8.dp), tonalElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Editar nombre de usuario", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            viewModel.onUpdateName(newName.trim())
                        }) {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }

    // Monitorizar resultado de la actualización
    LaunchedEffect(uiState) {
        when (uiState) {
            is ProfileViewModel.UiState.Success -> showDialog = false
            is ProfileViewModel.UiState.Error -> {
                // Mostrar error
                (uiState as ProfileViewModel.UiState.Error).message.let { _ -> }
            }

            else -> { /* Idle o Loading: no-op */
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Imagen de perfil...
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            ) {
                // Imagen estática o según género/director
                androidx.compose.foundation.Image(
                    painter = painterResource(id = org.iesalandalus.pi_musicaincrescendo.R.drawable.perfil_neutro),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(8.dp))
            // Nombre con icono de edición:
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = displayName, style = MaterialTheme.typography.headlineSmall)
                IconButton(onClick = { newName = displayName; showDialog = true }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar nombre")
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Seleccione su instrumento principal...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = "En Banda Municipal de Alcolea desde el ${viewModel.registrationDateFormatted}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}