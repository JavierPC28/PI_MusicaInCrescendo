package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val selectedInstruments by viewModel.selectedInstruments.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(displayName) }

    // Diálogo de edición de nombre
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

    // Cerrar diálogo tras éxito o manejar error
    LaunchedEffect(uiState) {
        when (uiState) {
            is ProfileViewModel.UiState.Success -> showDialog = false
            is ProfileViewModel.UiState.Error -> {}
            else -> {}
        }
    }

    // Lista completa de instrumentos
    val instrumentos = listOf(
        "DIRECCIÓN MUSICAL", "FLAUTÍN", "FLAUTA", "OBOE", "CORNO INGLÉS",
        "FAGOT", "CONTRAFAGOT", "REQUINTO", "CLARINETE", "CLARINETE BAJO",
        "SAXOFÓN SOPRANO", "SAXOFÓN ALTO", "SAXOFÓN TENOR", "SAXOFÓN BARÍTONO",
        "TROMPA", "FLISCORNO", "TROMPETA", "TROMBÓN", "TROMBÓN BAJO",
        "BOMBARDINO", "TUBA"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Sección superior: imagen y nombre
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de perfil según género y rol
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
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            ) {
                Image(
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
                IconButton(onClick = {
                    newName = displayName
                    showDialog = true
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar nombre"
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Seleccione su instrumento (máx. 3)",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Text(
            "Instrumento principal: ⭐",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(8.dp)
        ) {
            Column {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(instrumentos.size) { index ->
                        val instrumento = instrumentos[index]
                        val isSelected = selectedInstruments.contains(instrumento)
                        val oro = Color(0xFFFFD700)

                        Card(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable { viewModel.onInstrumentToggle(instrumento) }
                                .border(
                                    width = if (isSelected) 4.dp else 0.dp,
                                    color = if (isSelected) oro else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = instrumento,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(4.dp)
                                )
                                if (isSelected) {
                                    Icon(
                                        painter = painterResource(R.drawable.estrella),
                                        contentDescription = "Seleccionado",
                                        tint = oro, // estrella dorada
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Fecha de registro
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