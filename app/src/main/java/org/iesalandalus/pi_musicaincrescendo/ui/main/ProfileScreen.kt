package org.iesalandalus.pi_musicaincrescendo.ui.main

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants.DIRECCION_MUSICAL
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants.MAX_INSTRUMENTS
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants.MAX_INSTRUMENTS_DIRECTOR
import org.iesalandalus.pi_musicaincrescendo.common.utils.ImageHelper
import org.iesalandalus.pi_musicaincrescendo.common.utils.ImageHelper.getInstrumentDrawable
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.ProfileViewModel
import org.iesalandalus.pi_musicaincrescendo.ui.theme.colorOro

/**
 * Pantalla de perfil del usuario.
 * Permite ver y editar el nombre, la foto de perfil y los instrumentos.
 */
@Composable
fun ProfileScreen() {
    val vm: ProfileViewModel = viewModel()
    val displayName by vm.displayName.collectAsState()
    val gender by vm.gender.collectAsState()
    val isDirector by vm.isDirector.collectAsState()
    val photoUrl by vm.photoUrl.collectAsState()
    val selectedInstruments by vm.selectedInstruments.collectAsState()
    val uiState by vm.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    // Lanzador para seleccionar una imagen de la galería.
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { vm.onProfileImageChange(it) }
    }

    // Cierra el diálogo de edición de nombre si la actualización fue exitosa.
    LaunchedEffect(uiState) {
        if (uiState is ProfileViewModel.UiState.Success) showDialog = false
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Cabecera con foto y nombre.
        ProfileHeader(
            displayName = displayName,
            gender = gender,
            isDirector = isDirector,
            photoUrl = photoUrl,
            onEditName = { showDialog = true },
            onEditPhoto = { imagePickerLauncher.launch("image/*") }
        )

        // Instrucciones para la selección de instrumentos.
        InstrumentInstructions(isDirector = isDirector)

        // Cuadrícula de instrumentos.
        Box(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray.copy(alpha = 0.6f))
                .padding(8.dp)
        ) {
            InstrumentGrid(
                selected = selectedInstruments,
                isDirector = isDirector,
                onToggle = vm::onInstrumentToggle
            )
        }

        Spacer(Modifier.height(12.dp))

        // Fecha de registro.
        Text(
            "En Banda Municipal de Alcolea desde el ${vm.registrationDateFormatted}",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Diálogo para editar el nombre.
        if (showDialog) {
            EditNameDialog(
                currentName = displayName,
                onConfirm = { vm.onUpdateName(it) },
                onDismiss = { showDialog = false }
            )
        }

        // Indicador de carga.
        if (uiState is ProfileViewModel.UiState.Loading) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }
        }
    }
}

/**
 * Cabecera de la pantalla de perfil.
 * @param displayName Nombre a mostrar.
 * @param gender Género para la imagen por defecto.
 * @param isDirector Si el usuario es director.
 * @param photoUrl URL de la foto de perfil.
 * @param onEditName Lambda para editar el nombre.
 * @param onEditPhoto Lambda para editar la foto.
 */
@Composable
private fun ProfileHeader(
    displayName: String,
    gender: String,
    isDirector: Boolean,
    photoUrl: String?,
    onEditName: () -> Unit,
    onEditPhoto: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            // Imagen de perfil principal.
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        RoundedCornerShape(12.dp)
                    )
            ) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(ImageHelper.getProfileImage(gender, isDirector)),
                    error = painterResource(ImageHelper.getProfileImage(gender, isDirector))
                )
            }
            // Botón para editar la foto.
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .clickable { onEditPhoto() },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar foto",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        // Nombre de usuario y botón de edición.
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(displayName, style = MaterialTheme.typography.headlineSmall)
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar nombre",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { onEditName() }
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

/**
 * Muestra las instrucciones sobre cómo seleccionar los instrumentos.
 * @param isDirector Indica si el usuario es director para mostrar el límite correcto.
 */
@Composable
private fun InstrumentInstructions(isDirector: Boolean) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            "Seleccione su instrumento (máx. ${if (isDirector) MAX_INSTRUMENTS_DIRECTOR else MAX_INSTRUMENTS})",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Instrumento principal: ⭐",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
    }
}

/**
 * Cuadrícula para mostrar y seleccionar instrumentos.
 * @param selected Lista de instrumentos actualmente seleccionados.
 * @param isDirector Si el usuario es director.
 * @param onToggle Lambda para seleccionar/deseleccionar un instrumento.
 */
@Composable
private fun InstrumentGrid(
    selected: List<String>,
    isDirector: Boolean,
    onToggle: (String) -> Unit
) {
    Box(
        Modifier.fillMaxWidth()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(Constants.instrumentosList.size) { i ->
                val instr = Constants.instrumentosList[i]
                val isSelected = selected.contains(instr)
                val isPrincipal = selected.firstOrNull() == instr
                val disabled = instr == DIRECCION_MUSICAL && !isDirector

                // Define el borde según el estado (deshabilitado, principal, seleccionado, normal).
                val stroke = when {
                    disabled -> BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )

                    isPrincipal -> BorderStroke(4.dp, colorOro)
                    isSelected -> BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
                    else -> BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .aspectRatio(1f)
                        .alpha(if (disabled) 0.5f else 1f)
                        .clickable(enabled = !disabled) { onToggle(instr) }
                        .border(stroke, RoundedCornerShape(8.dp))
                ) {
                    Box(Modifier.fillMaxSize()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(getInstrumentDrawable(instr)),
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
                        // Muestra una estrella si es el instrumento principal.
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
}

/**
 * Diálogo para editar el nombre del usuario.
 * @param currentName Nombre actual.
 * @param onConfirm Lambda para confirmar el nuevo nombre.
 * @param onDismiss Lambda para cerrar el diálogo.
 */
@Composable
private fun EditNameDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var tempName by remember { mutableStateOf(currentName) }
    Dialog(onDismissRequest = onDismiss) {
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
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancelar") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { onConfirm(tempName.trim()) }) { Text("Guardar") }
                }
            }
        }
    }
}