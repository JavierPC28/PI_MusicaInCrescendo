package org.iesalandalus.pi_musicaincrescendo.ui.main

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.DownloadPdfUseCase
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RepertoireDetailViewModel
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RepertoireDetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepertoireDetailScreen(
    navController: NavHostController,
    viewModel: RepertoireDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val downloadPdfUseCase = remember { DownloadPdfUseCase() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.repertoire != null && uiState.userProfile != null -> {
                    RepertoireDetailContent(
                        state = uiState,
                        onDownloadPdf = { url, title ->
                            downloadPdfUseCase(context, url, title)
                            Toast.makeText(context, "Iniciando descarga...", Toast.LENGTH_SHORT)
                                .show()
                        },
                        onRequestPdf = {
                            Toast.makeText(
                                context,
                                "Solicitud enviada al director",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RepertoireDetailContent(
    state: RepertoireDetailUiState,
    onDownloadPdf: (url: String, title: String) -> Unit,
    onRequestPdf: () -> Unit
) {
    val repertoire = state.repertoire!!
    val userProfile = state.userProfile!!
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título y compositor
        item {
            Text(
                text = repertoire.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = repertoire.composer,
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Vídeo de YouTube
        if (!repertoire.videoUrl.isNullOrBlank()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Versión a interpretar",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, repertoire.videoUrl.toUri())
                            context.startActivity(intent)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.youtube_completo),
                        contentDescription = "YouTube",
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ver vídeo en YouTube",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Archivos
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Archivos",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(userProfile.instruments) { instrument ->
            val pdfUrl = repertoire.instrumentFiles[instrument]
            InstrumentFileRow(
                instrumentName = instrument,
                hasPdf = pdfUrl != null,
                onClick = {
                    if (pdfUrl != null) {
                        onDownloadPdf(pdfUrl, "${repertoire.title} - $instrument")
                    } else {
                        onRequestPdf()
                    }
                }
            )
        }
    }
}

@Composable
private fun InstrumentFileRow(
    instrumentName: String,
    hasPdf: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = instrumentName,
            modifier = Modifier.weight(1f),
            fontSize = 18.sp
        )

        if (hasPdf) {
            Image(
                painter = painterResource(id = R.drawable.pdf),
                contentDescription = "Descargar PDF",
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = onClick)
            )
        } else {
            Text(
                text = "Solicitar",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onClick)
            )
        }
    }
}