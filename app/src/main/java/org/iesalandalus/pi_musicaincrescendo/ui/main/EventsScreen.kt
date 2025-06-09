package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.BorderStroke
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

private const val ATTENDANCE_STATUS_ATTENDING = "IRÉ"
private const val ATTENDANCE_STATUS_NOT_ATTENDING = "NO IRÉ"


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
        DeleteEventDialog(
            onConfirm = { viewModel.onConfirmDelete() },
            onDismiss = { viewModel.onDismissDeleteDialog() }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        EventsHeader(isDirector = isDirector) {
            navController.navigate(Screen.AddEvent.routeWithArgs())
        }

        Spacer(modifier = Modifier.height(16.dp))

        EventFilters(activeFilter = activeFilter) { filter ->
            viewModel.setFilter(filter)
        }

        Spacer(modifier = Modifier.height(16.dp))

        EventsContent(
            modifier = Modifier.weight(1f),
            isLoading = isLoading,
            error = error,
            events = events,
            isDirector = isDirector,
            currentUserId = currentUserId,
            navController = navController,
            viewModel = viewModel
        )
    }
}

@Composable
private fun EventsContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    error: String?,
    events: List<Event>,
    isDirector: Boolean,
    currentUserId: String?,
    navController: NavHostController,
    viewModel: EventsViewModel
) {
    Box(modifier = modifier.fillMaxWidth()) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error, color = MaterialTheme.colorScheme.error)
                }
            }

            events.isEmpty() -> {
                EmptyState()
            }

            else -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(events) { event ->
                        EventCard(
                            event = event,
                            isDirector = isDirector,
                            currentUserId = currentUserId,
                            onEdit = {
                                navController.navigate(Screen.AddEvent.routeWithArgs(event.id))
                            },
                            onDelete = {
                                viewModel.onDeleteRequest(event.id, event.title)
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
private fun EventsHeader(isDirector: Boolean, onAddEvent: () -> Unit) {
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
                onClick = onAddEvent,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Añadir evento")
            }
        }
    }
}

@Composable
private fun EventFilters(
    activeFilter: EventFilterType,
    onFilterSelected: (EventFilterType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EventFilterType.entries.forEach { filter ->
            val isSelected = activeFilter == filter
            Button(
                onClick = { onFilterSelected(filter) },
                modifier = Modifier.weight(1f),
                colors = if (isSelected) ButtonDefaults.buttonColors()
                else ButtonDefaults.outlinedButtonColors(),
                border = if (!isSelected) ButtonDefaults.outlinedButtonBorder(enabled = true)
                else null
            ) {
                Text(filter.displayName)
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

@Composable
private fun DeleteEventDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar borrado") },
        text = { Text("¿Estás seguro de que quieres eliminar este evento? Esta acción no se puede deshacer.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun AttendanceSection(
    status: String?,
    isPast: Boolean,
    onUpdate: (String) -> Unit
) {
    when (status) {
        ATTENDANCE_STATUS_ATTENDING -> Text(
            text = ATTENDANCE_STATUS_ATTENDING,
            color = Color(0xFF2E7D32),
            fontWeight = FontWeight.Bold
        )

        ATTENDANCE_STATUS_NOT_ATTENDING -> Text(
            text = ATTENDANCE_STATUS_NOT_ATTENDING,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )

        else -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onUpdate(ATTENDANCE_STATUS_NOT_ATTENDING) },
                    enabled = !isPast
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "No iré",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
                IconButton(
                    onClick = { onUpdate(ATTENDANCE_STATUS_ATTENDING) },
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
    val cardAlpha = if (isPast) 0.7f else 1f

    fun formatDate(dateStr: String, timeStr: String): String {
        return try {
            val dateTimeStr = "$dateStr $timeStr"
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateTime = dateTimeFormat.parse(dateTimeStr)
            if (dateTime != null) {
                val outputFormat =
                    SimpleDateFormat("EEEE, d 'de' MMMM 'a las' HH:mm", Locale("es", "ES"))
                outputFormat.format(dateTime).uppercase()
            } else {
                "${event.date} a las ${event.startTime}"
            }
        } catch (_: Exception) {
            "${event.date} a las ${event.startTime}"
        }
    }

    val cardBorder = if (isPast) {
        null
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha)
            .clickable { onViewDetails(event.id) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = cardBorder,
        colors = CardDefaults.cardColors(
            containerColor = if (isPast) Color.LightGray
            else MaterialTheme.colorScheme.surfaceContainer
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
                    color = MaterialTheme.colorScheme.primary,
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

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
            ) {
                AttendanceSection(
                    status = currentUserId?.let { event.asistencias[it] },
                    isPast = isPast,
                    onUpdate = { status -> onUpdateAttendance(event.id, status) }
                )
            }
        }
    }
}