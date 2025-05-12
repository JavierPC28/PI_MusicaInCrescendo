package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.painter.Painter
import org.iesalandalus.pi_musicaincrescendo.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

/**
 * Contenido estático del menú lateral con la lista de grupos.
 * Solo muestra el grupo Banda Municipal de Música de Alcolea como seleccionado.
 */
@Composable
fun DrawerContent() {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "MIS GRUPOS",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
        )

        // Grupo seleccionado
        SelectedGrupoItem(
            icon = painterResource(id = R.drawable.banda_alcolea),
            texto = "Banda Municipal de Música de Alcolea"
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Otros grupos sin selección
        GrupoItem(
            icon = painterResource(id = R.drawable.banda_ejido),
            texto = "Banda Sinfónica El Ejido"
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        GrupoItem(
            iconVector = Icons.Default.Add,
            texto = "Crea un grupo nuevo"
        )
    }
}

/**
 * Item de grupo seleccionado con fondo destacado.
 */
@Composable
private fun SelectedGrupoItem(
    icon: Painter,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Image(
            painter = icon,
            contentDescription = texto,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Item genérico de grupo no seleccionado.
 */
@Composable
private fun GrupoItem(
    icon: Painter? = null,
    iconVector: ImageVector? = null,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (icon != null) {
            Image(
                painter = icon,
                contentDescription = texto,
                modifier = Modifier.size(40.dp)
            )
        } else if (iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = texto,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}