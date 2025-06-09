package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.Screen
import org.iesalandalus.pi_musicaincrescendo.domain.model.FilterOption
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RepertoireViewModel

/**
 * Diálogo de selección de filtro.
 */
@Composable
private fun FilterDialog(
    selectedFilter: FilterOption,
    onOptionSelected: (FilterOption) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Ordenar por") },
        text = {
            Column {
                FilterOption.entries.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = option == selectedFilter,
                            onClick = { onOptionSelected(option) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (option) {
                                FilterOption.TITULO -> "Título"
                                FilterOption.COMPOSITOR -> "Compositor"
                                FilterOption.FECHA_PUBLICACION -> "Fecha de publicación"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = { /* Sin botón de confirmar */ },
        dismissButton = { /* Sin botón de cancelar */ }
    )
}

@Composable
private fun WorkItem(
    work: Repertoire,
    isDirector: Boolean,
    onViewDetails: (String) -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (Repertoire) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onViewDetails(work.id) },
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = work.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = work.composer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (isDirector) {
                IconButton(onClick = { onEdit(work.id) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar obra")
                }
                IconButton(onClick = { onDelete(work) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar obra",
                        tint = MaterialTheme.colorScheme.error

                    )
                }
            }
        }
    }
}

/**
 * Vista de repertorio.
 */
@Composable
fun RepertoireScreen(
    navController: NavHostController,
    viewModel: RepertoireViewModel = viewModel()
) {
    val searchText by viewModel.searchText.collectAsState()
    val isIconToggled by viewModel.isIconToggled.collectAsState()
    val showFilterDialog by viewModel.showFilterDialog.collectAsState()
    val selectedFilter by viewModel.selectedFilterOption.collectAsState()
    val repertoireList by viewModel.repertoireList.collectAsState()
    val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
    val isDirector by viewModel.isDirector.collectAsState()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissDeleteDialog() },
            title =
                { Text("Confirmar borrado") },
            text = {
                Text(
                    "¿Estás seguro de que quieres eliminar esta obra? Esta acción no se puede deshacer."
                )
            },
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
                text = "Todas las obras",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (isDirector) {
                Button(
                    onClick = {
                        navController.navigate(Screen.AddEditRepertoire.routeWithArgs())
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Añadir tema")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                maxLines = 1,
                placeholder = {
                    Text(
                        text = "Buscar por título o compositor",
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.onSearchTextChange("") }
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Borrar texto")
                        }
                    }
                }
            )

            Icon(
                painter = painterResource(
                    id = if (isIconToggled) R.drawable.ascendente else R.drawable.descendente
                ),
                contentDescription = "Orden ascendente/descendente",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { viewModel.onToggleIcon() }
            )

            Icon(
                painter = painterResource(R.drawable.filtro),
                contentDescription = "Filtrar",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { viewModel.onFilterIconClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showFilterDialog) {
            FilterDialog(
                selectedFilter = selectedFilter,
                onOptionSelected = { viewModel.onFilterOptionSelected(it) },
                onDismiss = { viewModel.onFilterOptionSelected(selectedFilter) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (repertoireList.isEmpty()) {
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
                        text = "No hay ningún tema disponible",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(repertoireList) { work ->
                    WorkItem(
                        work = work,
                        isDirector = isDirector,
                        onViewDetails = { workId ->
                            navController.navigate(Screen.RepertoireDetail.routeWithArgs(workId))
                        },
                        onEdit = { workId ->
                            navController.navigate(Screen.AddEditRepertoire.routeWithArgs(workId))
                        },
                        onDelete = { workId ->
                            viewModel.onDeleteRequest(workId)
                        }
                    )
                }
            }
        }
    }
}