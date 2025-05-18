package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen() {
    val viewModel: ProfileViewModel = viewModel()
    val displayName by viewModel.displayName.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val isDirector by viewModel.isDirector.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(displayName) }

    // Diálogo de edición
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = RoundedCornerShape(8.dp), tonalElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Edite su nombre de usuario",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
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
                        Button(onClick = { viewModel.onUpdateName(newName.trim()) }) {
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
            is ProfileViewModel.UiState.Error -> { /* Podríamos mostrar Toast si se desea */
            }

            else -> {/* Posible implementación futura */ }
        }
    }

    // Lista de instrumentos
    val instrumentos = listOf(
        "DIRECCIÓN MUSICAL", "FLAUTÍN", "FLAUTA", "OBOE", "CORNO INGLÉS",
        "FAGOT", "CONTRAFAGOT", "REQUINTO", "CLARINETE", "CLARINETE BAJO",
        "SAXOFÓN SOPRANO", "SAXOFÓN ALTO", "SAXOFÓN TENOR", "SAXOFÓN BARÍTONO",
        "TROMPA", "FLISCORNO", "TROMPETA", "TROMBÓN", "TROMBÓN BAJO",
        "BOMBARDINO", "TUBA"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de perfil dinámico según género y rol
            val imageRes = when {
                gender == "Mujer" && isDirector -> R.drawable.perfil_directora
                gender == "Mujer" && !isDirector -> R.drawable.perfil_alumna
                gender == "Hombre" && isDirector -> R.drawable.perfil_director
                gender == "Hombre" && !isDirector -> R.drawable.perfil_alumno
                else -> R.drawable.perfil_neutro
            }
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = { newName = displayName; showDialog = true }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar nombre")
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Seleccione su instrumento (máx. 3)",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(8.dp))

        // Grid scrollable de instrumentos
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(instrumentos) { instrumento ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f)
                        .clickable { /* Podríamos manejar selección */ }
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = instrumento,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = "En Banda Municipal de Alcolea desde el ${viewModel.registrationDateFormatted}",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}