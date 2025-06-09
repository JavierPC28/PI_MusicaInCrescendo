package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.Screen
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.model.EventFilterType
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.EventsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EventsScreen(navController: NavHostController, viewModel: EventsViewModel = viewModel()) {
    val events by viewModel.filteredEvents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isDirector by viewModel.isDirector.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
    val currentUserId by remember { mutableStateOf(viewModel.currentUserId) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            title = { Text("Confirmar borrado") },
            text = { Text("¿Estás seguro de que quieres eliminar este evento? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.onConfirmDelete() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDeleteDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Conciertos y ensayos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (isDirector) {
                Button(
                    onClick = {
                        navController.navigate(Screen.AddEvent.routeWithArgs())
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Añadir evento")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EventFilterType.entries.forEach { filter ->
                Button(
                    onClick = { viewModel.setFilter(filter) },
                    modifier = Modifier.weight(1f),
                    colors = if (activeFilter == filter) {
                        ButtonDefaults.buttonColors()
                    } else {
                        ButtonDefaults.outlinedButtonColors()
                    },
                    border = if (activeFilter != filter) {
                        ButtonDefaults.outlinedButtonBorder
                    } else {
                        null
                    }
                ) {
                    Text(filter.displayName)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = error!!, color = MaterialTheme.colorScheme.error)
                }
            }

            events.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.caja_vacia),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay ningún evento programado",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(events) { event ->
                        EventCard(
                            event = event,
                            isDirector = isDirector,
                            currentUserId = currentUserId,
                            onEdit = {
                                navController.navigate(Screen.AddEvent.routeWithArgs(event.id))
                            },
                            onDelete = {
                                viewModel.onDeleteRequest(event.id)
                            },
                            onUpdateAttendance = { eventId, status ->
                                viewModel.updateAttendance(eventId, status)
                            },
                            onViewDetails = { eventId ->
                                navController.navigate(Screen.EventDetail.routeWithArgs(eventId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    isDirector: Boolean,
    currentUserId: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onUpdateAttendance: (String, String) -> Unit,
    onViewDetails: (String) -> Unit
) {
    fun parseEventDateTime(dateStr: String, timeStr: String): Date? {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            format.parse("$dateStr $timeStr")
        } catch (_: Exception) {
            null
        }
    }

    val isPast = parseEventDateTime(event.date, event.endTime)?.before(Date()) == true
    val cardAlpha = if (isPast) 0.6f else 1f

    fun formatDate(dateStr: String, timeStr: String): String {
        return try {
            val dateTimeStr = "$dateStr $timeStr"
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateTime = dateTimeFormat.parse(dateTimeStr)
            val outputFormat =
                SimpleDateFormat("EEEE, d 'de' MMMM 'a las' HH:mm", Locale("es", "ES"))
            outputFormat.format(dateTime).uppercase()
        } catch (_: Exception) {
            "${event.date} a las ${event.startTime}"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .clickable { onViewDetails(event.id) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPast) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = formatDate(event.date, event.startTime),
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(32.dp))
            }

            if (isDirector) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                ) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(24.dp),
                        enabled = !isPast
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar evento")
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar evento",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            val attendanceStatus = currentUserId?.let { event.asistencias[it] }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
            ) {
                when (attendanceStatus) {
                    "IRÉ" -> Text(
                        text = "IRÉ",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )

                    "NO IRÉ" -> Text(
                        text = "NO IRÉ",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )

                    else -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onUpdateAttendance(event.id, "NO IRÉ") },
                                enabled = !isPast
                            ) {
                                Icon(
                                    Icons.Filled.Close,
                                    contentDescription = "No iré",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                            IconButton(
                                onClick = { onUpdateAttendance(event.id, "IRÉ") },
                                enabled = !isPast
                            ) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Iré",
                                    tint = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}