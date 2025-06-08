package org.iesalandalus.pi_musicaincrescendo.ui.main

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants
import org.iesalandalus.pi_musicaincrescendo.common.utils.ImageHelper
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.AddRepertoireViewModel

/**
 * Pantalla para añadir una obra al repertorio.
 */
@Composable
fun AddRepertoireScreen(
    modifier: Modifier = Modifier,
    viewModel: AddRepertoireViewModel,
    navController: NavHostController
) {
    val title by viewModel.title.collectAsState()
    val composer by viewModel.composer.collectAsState()
    val videoUrl by viewModel.videoUrl.collectAsState()
    val isTitleValid by viewModel.isTitleValid.collectAsState()
    val isComposerValid by viewModel.isComposerValid.collectAsState()
    val instrumentFiles by viewModel.instrumentFiles.collectAsState()
    val existingInstruments by viewModel.existingInstruments.collectAsState()
    val isFilesValid by viewModel.isFilesValid.collectAsState()
    val saveSuccessMessage by viewModel.saveSuccess.collectAsState()
    val saveError by viewModel.saveError.collectAsState()
    val shouldNavigateBack by viewModel.shouldNavigateBack.collectAsState()
    val context = LocalContext.current

    var currentInstrument by remember { mutableStateOf<String?>(null) }
    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            currentInstrument?.let { instr ->
                viewModel.onFileSelected(instr, selectedUri)
            }
        }
    }

    LaunchedEffect(saveSuccessMessage) {
        saveSuccessMessage?.let { message ->
            Toast.makeText(
                context,
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(saveError) {
        saveError?.let { mensaje ->
            Toast.makeText(context, "Error al guardar: $mensaje", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(shouldNavigateBack) {
        if (shouldNavigateBack) {
            navController.popBackStack()
            viewModel.onNavigationHandled()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text("Título", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = title,
                onValueChange = viewModel::onTitleChange,
                isError = !isTitleValid,
                placeholder = {
                    Text(
                        text = "Las Bodas de Luis Alonso, Sonata Claro de Luna...",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (!isTitleValid) {
                Text(
                    text = "El título no puede estar vacío",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Compositor", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = composer,
                onValueChange = viewModel::onComposerChange,
                isError = !isComposerValid,
                placeholder = {
                    Text(
                        text = "Manuel de Falla, Beethoven...",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (!isComposerValid) {
                Text(
                    text = "El compositor no puede estar vacío",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Vídeo", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(id = R.drawable.youtube_completo),
                    contentDescription = "YouTube",
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = videoUrl,
                onValueChange = viewModel::onVideoUrlChange,
                placeholder = {
                    Text(
                        text = "URL de YouTube (opcional)",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Introduce un enlace de vídeo de YouTube",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }


        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Archivos", fontWeight = FontWeight.Bold)
            if (!isFilesValid) {
                Text(
                    text = "Debe seleccionar al menos un archivo PDF",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(Constants.instrumentosList) { instr ->
            // Un fichero está "seleccionado" si se acaba de escoger (en instrumentFiles)
            // o si ya existía al editar la obra (en existingInstruments)
            val isNewlySelected = instrumentFiles.containsKey(instr)
            val hadFileBefore = existingInstruments.contains(instr)
            val fileSelected = isNewlySelected || hadFileBefore

            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = if (fileSelected) CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) else CardDefaults.cardColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        currentInstrument = instr
                        pdfPicker.launch("application/pdf")
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = ImageHelper.getInstrumentDrawable(
                                instr
                            )
                        ),
                        contentDescription = instr,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = instr,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    if (fileSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "PDF seleccionado",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}