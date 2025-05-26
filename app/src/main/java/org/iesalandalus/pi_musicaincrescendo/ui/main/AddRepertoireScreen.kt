package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.common.utils.ImageHelper
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.AddRepertoireViewModel

/**
 * Pantalla para añadir una obra al repertorio.
 */
@Composable
fun AddRepertoireScreen(
    modifier: Modifier = Modifier,
    viewModel: AddRepertoireViewModel = viewModel()
) {
    val title by viewModel.title.collectAsState()
    val composer by viewModel.composer.collectAsState()
    val videoUrl by viewModel.videoUrl.collectAsState()
    val isTitleValid by viewModel.isTitleValid.collectAsState()
    val isComposerValid by viewModel.isComposerValid.collectAsState()

    // Lista de instrumentos disponibles
    val instrumentosList = listOf(
        "DIRECCIÓN MUSICAL", "FLAUTÍN", "FLAUTA", "OBOE", "CORNO INGLÉS",
        "FAGOT", "CONTRAFAGOT", "REQUINTO", "CLARINETE", "CLARINETE BAJO",
        "SAXO SOPRANO", "SAXO ALTO", "SAXO TENOR", "SAXO BARÍTONO",
        "TROMPA", "FLISCORNO", "TROMPETA", "TROMBÓN", "TROMBÓN BAJO",
        "BOMBARDINO", "TUBA", "VIOLONCHELO", "CONTRABAJO", "CAJA",
        "PERCUSIÓN", "BOMBO", "PLATOS", "TIMBALES", "LÁMINAS", "BATERÍA"
    )

    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text("Título", fontWeight = FontWeight.Bold)
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

        // Compositor
        Text("Compositor", fontWeight = FontWeight.Bold)
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
            modifier = Modifier
                .padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Archivos", fontWeight = FontWeight.Bold)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(instrumentosList) { instr ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = ImageHelper.getInstrumentDrawable(instr)),
                            contentDescription = instr,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = instr,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}