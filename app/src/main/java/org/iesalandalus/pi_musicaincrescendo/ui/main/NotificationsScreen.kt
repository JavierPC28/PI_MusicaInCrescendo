package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.domain.model.Notification
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(viewModel: NotificationsViewModel = viewModel()) {
    val notifications by viewModel.notifications.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            title = { Text("Confirmar borrado") },
            text = { Text("¿Estás seguro de que quieres eliminar TODAS las notificaciones? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.onConfirmDelete() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar Todo")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissDeleteDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (notifications.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.notificaciones),
                    contentDescription = "Sin notificaciones",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No hay notificaciones",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { notification ->
                NotificationItem(
                    notification = notification,
                    formattedDate = viewModel.getFormattedDate(notification.timestamp)
                )
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, formattedDate: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.banda_alcolea),
                contentDescription = "Icono de la banda",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.text,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}