package org.iesalandalus.pi_musicaincrescendo.common.components

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.iesalandalus.pi_musicaincrescendo.R

private data class Grupo(
    val id: Int,
    val iconRes: Int? = null,
    val iconVector: ImageVector? = null,
    val texto: String,
    val isDestructive: Boolean = false
)

@Composable
fun DrawerContent(
    onLogout: () -> Unit,
    onExit: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val context = LocalContext.current
    val grupos = remember {
        listOf(
            Grupo(
                0,
                iconRes = R.drawable.banda_alcolea,
                texto = "Banda Municipal de Música de Alcolea"
            ),
            Grupo(1, iconRes = R.drawable.banda_ejido, texto = "Banda Sinfónica El Ejido"),
            Grupo(2, iconVector = Icons.Default.Add, texto = "Crea un grupo nuevo")
        )
    }
    val opcionesInferiores = remember {
        listOf(
            Grupo(3, iconRes = R.drawable.cerrar_sesion, texto = "Cerrar Sesión"),
            Grupo(4, iconRes = R.drawable.salir_app, texto = "Salir"),
            Grupo(
                5,
                iconRes = R.drawable.borrar,
                texto = "Eliminar Cuenta",
                isDestructive = true
            )
        )
    }
    var seleccionado by rememberSaveable { mutableIntStateOf(0) }

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

        // Lista de grupos principales
        grupos.forEach { grupo ->
            Spacer(modifier = Modifier.height(8.dp))
            GrupoItem(
                grupo = grupo,
                isSelected = (grupo.id == seleccionado),
                onClick = {
                    when (grupo.id) {
                        2 -> Toast.makeText(
                            context,
                            "Funcionalidad en desarrollo",
                            Toast.LENGTH_SHORT
                        ).show()

                        else -> seleccionado = grupo.id
                    }
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Opciones inferiores (Cerrar sesión y Salir)
        opcionesInferiores.forEach { grupo ->
            Spacer(modifier = Modifier.height(8.dp))
            GrupoItem(
                grupo = grupo,
                isSelected = false,
                onClick = {
                    when (grupo.id) {
                        3 -> onLogout()
                        4 -> onExit()
                        5 -> onDeleteAccount()
                    }
                }
            )
        }
    }
}

@Composable
private fun GrupoItem(
    grupo: Grupo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (grupo.isDestructive) Color(0xFFD32F2F) else LocalContentColor.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else
                    MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        grupo.iconRes?.let {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = grupo.texto,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } ?: grupo.iconVector?.let {
            Icon(
                imageVector = it,
                contentDescription = grupo.texto,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = grupo.texto, style = MaterialTheme.typography.bodyLarge, color = textColor)
    }
}