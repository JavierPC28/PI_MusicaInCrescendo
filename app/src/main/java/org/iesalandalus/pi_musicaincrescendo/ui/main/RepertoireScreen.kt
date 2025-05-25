package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.RepertoireViewModel

/**
 * Vista de repertorio actualizada.
 */
@Composable
fun RepertoireScreen(
    viewModel: RepertoireViewModel = viewModel()
) {
    // Estados de UI desde ViewModel
    val searchText by viewModel.searchText.collectAsState()
    val isIconToggled by viewModel.isIconToggled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Encabezado con título y botón
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
            Button(
                onClick = { /* Acción añadir tema */ },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Añadir tema")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fila de búsqueda e iconos
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Barra de búsqueda
            OutlinedTextField(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                maxLines = 1,
                placeholder = {
                    Text(
                        text = "Buscar por título o compositor",
                        fontSize = 14.sp
                    )
                },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) }
            )

            // Icono que alterna (drawable)
            Icon(
                painter = painterResource(
                    id = if (isIconToggled) R.drawable.descendente else R.drawable.ascendente
                ),
                contentDescription = "Orden ascendente/descendente",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { viewModel.onToggleIcon() },
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Icono adicional (filtro)
            Icon(
                painter = painterResource(id = R.drawable.filtro),
                contentDescription = "Filtro para organizar temas",
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido desplazable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
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
    }
}