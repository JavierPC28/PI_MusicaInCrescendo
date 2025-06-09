package org.iesalandalus.pi_musicaincrescendo.ui.main

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.common.utils.ImageHelper
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Sección 1: Título y Compositor
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
        ) {
            Text(
                text = repertoire.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = repertoire.composer,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Gray
            )
        }

        // Sección 2: Contenido con fondo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp)
        ) {
            // Vídeo de YouTube
            if (!repertoire.videoUrl.isNullOrBlank()) {
                Text(
                    text = "Versión a interpretar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, repertoire.videoUrl.toUri())
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.youtube),
                        contentDescription = "YouTube Logo",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver en YouTube")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Archivos
            Text(
                text = "Archivos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                userProfile.instruments.forEach { instrument ->
                    val pdfUrl = repertoire.instrumentFiles[instrument]
                    InstrumentFileCard(
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
    }
}

@Composable
private fun InstrumentFileCard(
    instrumentName: String,
    hasPdf: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = ImageHelper.getInstrumentDrawable(instrumentName)),
                contentDescription = instrumentName,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = instrumentName,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (hasPdf) {
                Icon(
                    painter = painterResource(id = R.drawable.pdf),
                    contentDescription = "Descargar PDF",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Solicitar",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}