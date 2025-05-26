package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.AddRepertoireViewModel

@Composable
fun AddRepertoireScreen(
    modifier: Modifier = Modifier,
    viewModel: AddRepertoireViewModel = viewModel()
) {
    val title by viewModel.title.collectAsState()
    val composer by viewModel.composer.collectAsState()
    val videoUrl by viewModel.videoUrl.collectAsState()

    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Título", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = title,
            onValueChange = viewModel::onTitleChange,
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

        Spacer(modifier = Modifier.height(4.dp))

        Text("Compositor", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = composer,
            onValueChange = viewModel::onComposerChange,
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

        Spacer(modifier = Modifier.height(4.dp))

        Text("Vídeo", fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = videoUrl,
            onValueChange = viewModel::onVideoUrlChange,
            placeholder = {
                Text(
                    text = "URL de YouTube",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Archivos", fontWeight = FontWeight.Bold)
    }
}