package org.iesalandalus.pi_musicaincrescendo.ui.main

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.iesalandalus.pi_musicaincrescendo.domain.model.EventType
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.AddEventViewModel
import org.iesalandalus.pi_musicaincrescendo.common.components.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    navController: NavHostController,
    viewModel: AddEventViewModel
) {
    val context = LocalContext.current

    val eventType by viewModel.eventType.collectAsState()
    val date by viewModel.date.collectAsState()
    val startTime by viewModel.startTime.collectAsState()
    val endTime by viewModel.endTime.collectAsState()
    val location by viewModel.location.collectAsState()
    val allRepertoire by viewModel.allRepertoire.collectAsState()
    val selectedRepertoire by viewModel.selectedRepertoire.collectAsState()

    val saveSuccess by viewModel.saveSuccess.collectAsState()
    val saveError by viewModel.saveError.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val startTimePickerState = rememberTimePickerState()
    val endTimePickerState = rememberTimePickerState()

    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "Evento guardado correctamente", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            viewModel.onNavigationHandled()
        }
    }

    LaunchedEffect(saveError) {
        saveError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onNavigationHandled()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tipo de evento
        item {
            Text("Tipo de Evento", fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth()) {
                EventType.entries.forEach { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { viewModel.onEventTypeSelected(type) }
                            .padding(end = 16.dp)
                    ) {
                        RadioButton(
                            selected = eventType == type,
                            onClick = { viewModel.onEventTypeSelected(type) }
                        )
                        Text(type.displayName)
                    }
                }
            }
        }

        // Fecha y Hora
        item {
            Text("Fecha y Hora", fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.clickable { showDatePicker = true }) {
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showStartTimePicker = true }) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hora de inicio") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showEndTimePicker = true }) {
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hora de finalización") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                }
            }
        }

        // Localización
        item {
            Text("Localización", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = location,
                onValueChange = viewModel::onLocationChange,
                label = { Text("Ej: Centro Cultural Zaharagüi...") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Obras del repertorio
        item {
            Text("Repertorio", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
        }
        if (allRepertoire.isEmpty()) {
            item {
                Text(
                    "No hay obras en el repertorio. Añade alguna para poder crear un evento.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            items(allRepertoire) { work ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onRepertoireToggle(
                                work,
                                !selectedRepertoire.containsKey(work.id)
                            )
                        }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = selectedRepertoire.containsKey(work.id),
                        onCheckedChange = {
                            viewModel.onRepertoireToggle(work, it)
                        }
                    )
                    Text(text = work.title, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Calendar.getInstance().apply {
                                timeInMillis = millis
                            }
                            val formatter =
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            viewModel.onDateSelected(formatter.format(selectedDate.time))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Start Time Picker Dialog
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val time =
                            String.format(
                                "%02d:%02d",
                                startTimePickerState.hour,
                                startTimePickerState.minute
                            )
                        viewModel.onStartTimeSelected(time)
                        showStartTimePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) { Text("Cancelar") }
            }
        ) {
            TimePicker(state = startTimePickerState)
        }
    }

    // End Time Picker Dialog
    if (showEndTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val time =
                            String.format(
                                "%02d:%02d",
                                endTimePickerState.hour,
                                endTimePickerState.minute
                            )
                        viewModel.onEndTimeSelected(time)
                        showEndTimePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) { Text("Cancelar") }
            }
        ) {
            TimePicker(state = endTimePickerState)
        }
    }
}