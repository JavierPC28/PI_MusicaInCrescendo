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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.ProfileViewModel
import org.iesalandalus.pi_musicaincrescendo.ui.theme.colorOro

@Composable
fun ProfileScreen() {
    val vm: ProfileViewModel = viewModel()
    val displayName by vm.displayName.collectAsState()
    val gender by vm.gender.collectAsState()
    val isDirector by vm.isDirector.collectAsState()
    val selectedInstruments by vm.selectedInstruments.collectAsState()
    val uiState by vm.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(displayName) }

    // Diálogo edición nombre
    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(shape = RoundedCornerShape(8.dp), tonalElevation = 8.dp) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Edite su nombre de usuario",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
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
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            vm.onUpdateName(newName.trim())
                        }) { Text("Guardar") }
                    }
                }
            }
        }
    }

    // Cerramos diálogo tras éxito
    LaunchedEffect(uiState) {
        if (uiState is ProfileViewModel.UiState.Success) showDialog = false
    }

    // Lista de instrumentos
    val instrumentos = listOf(
        "DIRECCIÓN MUSICAL", "FLAUTÍN", "FLAUTA", "OBOE", "CORNO INGLÉS",
        "FAGOT", "CONTRAFAGOT", "REQUINTO", "CLARINETE", "CLARINETE BAJO",
        "SAXO SOPRANO", "SAXO ALTO", "SAXO TENOR", "SAXO BARÍTONO",
        "TROMPA", "FLISCORNO", "TROMPETA", "TROMBÓN", "TROMBÓN BAJO",
        "BOMBARDINO", "TUBA"
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Imagen y nombre
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
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
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(displayName, style = MaterialTheme.typography.headlineSmall)
                IconButton(onClick = {
                    newName = displayName
                    showDialog = true
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar nombre")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Seleccione su instrumento (máx. ${if (isDirector) 2 else 3})",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            "Instrumento principal: ⭐",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(instrumentos.size) { i ->
                    val instr = instrumentos[i]
                    val isSelected = selectedInstruments.contains(instr)
                    val isPrincipal = selectedInstruments.firstOrNull() == instr
                    val isDirection = instr == "DIRECCIÓN MUSICAL"
                    val disabled = isDirection && !isDirector

                    // Determinar borde
                    val borderStroke = when {
                        disabled -> BorderStroke(1.dp, Color.LightGray)
                        isPrincipal -> BorderStroke(4.dp, colorOro)
                        isSelected -> BorderStroke(3.dp, Color.DarkGray)
                        else -> BorderStroke(1.dp, Color.DarkGray)
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .aspectRatio(1f)
                            .alpha(if (disabled) 0.5f else 1f)
                            .clickable(enabled = !disabled) { vm.onInstrumentToggle(instr) }
                            .border(borderStroke, RoundedCornerShape(8.dp))
                    ) {
                        Box(Modifier.fillMaxSize()) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Image(
                                    painter = painterResource(id = getInstrumentDrawable(instr)),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )
                                Text(
                                    instr,
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                            if (isPrincipal) {
                                Icon(
                                    painter = painterResource(R.drawable.estrella),
                                    contentDescription = "Principal",
                                    tint = colorOro,
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

        Spacer(Modifier.height(12.dp))

        Text(
            "En Banda Municipal de Alcolea desde el ${vm.registrationDateFormatted}",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun getInstrumentDrawable(instrument: String): Int {
    return when (instrument) {
        "DIRECCIÓN MUSICAL" -> R.drawable.batuta
        "FLAUTÍN" -> R.drawable.flautin
        "FLAUTA" -> R.drawable.flauta
        "OBOE" -> R.drawable.oboe
        "CORNO INGLÉS" -> R.drawable.clarinete
        "FAGOT" -> R.drawable.fagot
        "CONTRAFAGOT" -> R.drawable.contrafagot
        "REQUINTO" -> R.drawable.clarinete
        "CLARINETE" -> R.drawable.clarinete
        "CLARINETE BAJO" -> R.drawable.clarinete_bajo
        "SAXO SOPRANO" -> R.drawable.saxofon
        "SAXO ALTO" -> R.drawable.saxofon
        "SAXO TENOR" -> R.drawable.saxofon
        "SAXO BARÍTONO" -> R.drawable.saxofon
        "TROMPA" -> R.drawable.trompa
        "FLISCORNO" -> R.drawable.trompeta
        "TROMPETA" -> R.drawable.trompeta
        "TROMBÓN" -> R.drawable.trombon
        "TROMBÓN BAJO" -> R.drawable.trombon
        "BOMBARDINO" -> R.drawable.tuba
        "TUBA" -> R.drawable.tuba
        else -> R.drawable.instrumento_generico
    }
}