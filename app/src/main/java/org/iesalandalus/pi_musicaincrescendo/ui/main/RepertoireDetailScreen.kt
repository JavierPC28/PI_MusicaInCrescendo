package org.iesalandalus.pi_musicaincrescendo.ui.main

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.common.utils.ImageHelper
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.DownloadPdfUseCase
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RepertoireDetailUiState
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RepertoireDetailViewModel
import java.util.regex.Pattern

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

        // Sección 2
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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
                YoutubePlayer(videoUrl = repertoire.videoUrl)
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

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun YoutubePlayer(
    videoUrl: String
) {
    fun extractVideoId(url: String): String? {
        val pattern =
            "(?<=watch\\?v=|/videos/|embed/|youtu.be/|/v/|/e/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|youtu.be%2F|%2Fv%2F)[^#&?]*"
        val compiledPattern = Pattern.compile(pattern)
        val matcher = compiledPattern.matcher(url)
        return if (matcher.find()) matcher.group() else null
    }

    val videoId = remember(videoUrl) { extractVideoId(videoUrl) }
    val html = """
        <style>
            * { margin: 0; padding: 0; }
            html, body { height: 100%; background-color: #000; }
        </style>
        <iframe 
            width="100%" 
            height="100%" 
            src="https://www.youtube.com/embed/$videoId" 
            frameborder="0" 
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
            allowfullscreen>
        </iframe>
    """.trimIndent()

    if (videoId != null) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    loadData(html, "text/html", "utf-8")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp)),
            update = { webView ->
                webView.loadData(html, "text/html", "utf-8")
            }
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun rememberWebViewWithLifecycle(): WebView {
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }
    }

    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    val observer = remember {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> webView.onResume()
                Lifecycle.Event.ON_PAUSE -> webView.onPause()
                Lifecycle.Event.ON_DESTROY -> webView.destroy()
                else -> {/* ... */}
            }
        }
    }

    DisposableEffect(lifecycle) {
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return webView
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