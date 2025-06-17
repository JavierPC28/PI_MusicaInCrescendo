package org.iesalandalus.pi_musicaincrescendo.ui.main

import android.content.Intent
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.domain.model.*
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.EventDetailViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla que muestra los detalles de un evento específico.
 * @param navController Controlador de navegación para volver a la pantalla anterior.
 * @param viewModel ViewModel que gestiona el estado de esta pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    navController: NavHostController,
    viewModel: EventDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when {
                // Muestra un indicador de progreso mientras carga
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                // Muestra un mensaje de error si ocurre un problema
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                // Muestra el contenido del evento si se ha cargado correctamente
                uiState.event != null -> {
                    EventDetailContent(
                        uiState = uiState,
                        onTabSelected = { viewModel.selectTab(it) }
                    )
                }
            }
        }
    }
}

/**
 * Contenido principal de la pantalla de detalles, incluyendo el título y las pestañas.
 * @param uiState Estado actual de la UI.
 * @param onTabSelected Lambda que se ejecuta al seleccionar una pestaña.
 */
@Composable
private fun EventDetailContent(
    uiState: org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.EventDetailUiState,
    onTabSelected: (Int) -> Unit
) {
    val event = uiState.event!!
    val tabs = listOf("Detalles", "Repertorio", "Miembros")

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = event.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Pestañas de navegación
        TabRow(selectedTabIndex = uiState.selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = { Text(title) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Contenido de la pestaña seleccionada
        when (uiState.selectedTab) {
            0 -> DetailsTab(event)
            1 -> RepertoireTab(uiState.repertoire)
            2 -> MembersTab(uiState.members)
        }
    }
}

/**
 * Pestaña que muestra los detalles del evento (fecha, hora, ubicación, mapa y descripción).
 * @param event El objeto Evento a mostrar.
 */
@Composable
private fun DetailsTab(event: Event) {
    val context = LocalContext.current
    // Parsea las coordenadas para el mapa
    val coordinates = remember(event.coordinates) {
        try {
            event.coordinates?.split(',')
                ?.map { it.trim().toDouble() }
                ?.let { LatLng(it[0], it[1]) }
        } catch (_: Exception) {
            null
        }
    }
    val mapView = rememberMapViewWithLifecycle()

    // Formatea la fecha a un formato más legible
    fun formatDate(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateStr)
            if (date != null) {
                val outputFormat =
                    SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
                outputFormat.format(date).replaceFirstChar { it.uppercase() }
            } else {
                dateStr
            }
        } catch (_: Exception) {
            dateStr
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        DetailRow(icon = Icons.Default.DateRange, text = formatDate(event.date))
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.hora),
                contentDescription = "Hora del evento",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${event.startTime} - ${event.endTime}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LocationRow(
            icon = Icons.Default.LocationOn,
            location = event.location,
            onClick = {
                val uri = "geo:0,0?q=${event.location}".toUri()
                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Muestra el mapa si hay coordenadas válidas
        if (coordinates != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                AndroidView(
                    factory = { mapView },
                    modifier = Modifier.fillMaxSize(),
                    update = { mv ->
                        mv.getMapAsync { map ->
                            map.uiSettings.isZoomControlsEnabled = true
                            map.uiSettings.isScrollGesturesEnabled = false
                            map.clear()
                            map.addMarker(
                                MarkerOptions().position(coordinates).title(event.location)
                            )
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 17f))
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Muestra la descripción si no está vacía
        if (!event.description.isNullOrBlank()) {
            Text(
                text = "Descripción",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Fila genérica para mostrar un detalle con un icono y texto.
 * @param icon El icono a mostrar.
 * @param text El texto del detalle.
 */
@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

/**
 * Fila específica para la ubicación, con un botón "Cómo llegar".
 * @param icon El icono de ubicación.
 * @param location El texto de la ubicación.
 * @param onClick Acción a ejecutar al pulsar "Cómo llegar".
 */
@Composable
private fun LocationRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    location: String,
    onClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = "Ubicación",
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = location,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onClick) {
            Text("Cómo llegar")
        }
    }
}

/**
 * Pestaña que muestra el repertorio del evento.
 * @param repertoire Lista de obras del repertorio.
 */
@Composable
private fun RepertoireTab(repertoire: List<Repertoire>) {
    if (repertoire.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay repertorio asignado a este evento.")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(repertoire) { work ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(work.title, fontWeight = FontWeight.Bold)
                        Text(work.composer, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

/**
 * Pestaña que muestra los miembros que han confirmado asistencia.
 * @param members Lista de usuarios que asisten.
 */
@Composable
private fun MembersTab(members: List<User>) {
    if (members.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nadie ha confirmado su asistencia todavía.")
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(members) { user ->
                MemberRow(user = user)
            }
        }
    }
}

/**
 * Fila que muestra la información de un miembro asistente.
 * @param user El usuario a mostrar.
 */
@Composable
private fun MemberRow(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.profile.photoUrl,
                contentDescription = user.profile.displayName,
                placeholder = painterResource(
                    id = org.iesalandalus.pi_musicaincrescendo.common.utils.ImageHelper.getProfileImage(
                        gender = user.profile.gender,
                        isDirector = user.profile.isDirector
                    )
                ),
                error = painterResource(
                    id = org.iesalandalus.pi_musicaincrescendo.common.utils.ImageHelper.getProfileImage(
                        gender = user.profile.gender,
                        isDirector = user.profile.isDirector
                    )
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(user.profile.displayName, fontWeight = FontWeight.Bold)
                Text(
                    user.profile.instruments.firstOrNull() ?: "Sin instrumento",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Composable que gestiona el ciclo de vida de un MapView de Google Maps.
 * @return Una instancia de MapView.
 */
@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val mapView = remember { MapView(context).apply { id = View.generateViewId() } }

    // Vincula el ciclo de vida del MapView al del Composable
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _: LifecycleOwner, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> { /* No-op */
                }
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    return mapView
}