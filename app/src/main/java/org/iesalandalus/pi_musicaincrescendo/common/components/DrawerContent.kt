package org.iesalandalus.pi_musicaincrescendo.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.iesalandalus.pi_musicaincrescendo.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

/**
 * Contenido estático del menú lateral con la lista de grupos.
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

        // Grupo seleccionado con margen y fondo redondeado
        SelectedGrupoItem(
            icon = painterResource(id = R.drawable.banda_alcolea),
            texto = "Banda Municipal de Música de Alcolea"
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Otros grupos
        GrupoItem(
            icon = painterResource(id = R.drawable.banda_ejido),
            texto = "Banda Sinfónica El Ejido"
        )

        Spacer(modifier = Modifier.height(8.dp))

        GrupoItem(
            iconVector = Icons.Default.Add,
            texto = "Crea un grupo nuevo"
        )
    }
}

/**
 * Item de grupo seleccionado con fondo destacado, márgenes y bordes redondeados.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
private fun SelectedGrupoItem(
    icon: Painter,
    texto: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Image(
            painter = icon,
            contentDescription = texto,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Item genérico de grupo no seleccionado sin divisores.
 */
@Suppress("UNUSED_PARAMETER")
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
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else if (iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = texto,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}