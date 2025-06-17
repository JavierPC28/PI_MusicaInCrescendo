package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.MenuAnchorType

/**
 * Composable que presenta un menú desplegable para la selección de género.
 * @param options La lista de opciones de género a mostrar.
 * @param selected La opción actualmente seleccionada.
 * @param onSelected Callback que se ejecuta cuando se selecciona una nueva opción.
 * @param modifier Modificador para personalizar el estilo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderSelector(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = { /* No se permite cambio directo */ },
            readOnly = true,
            label = { Text("Sexo") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            // Itera sobre las opciones y crea un DropdownMenuItem para cada una.
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}