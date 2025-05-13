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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.iesalandalus.pi_musicaincrescendo.R

private data class Grupo(
    val id: Int,
    val iconRes: Int? = null,
    val iconVector: ImageVector? = null,
    val texto: String
)

@Composable
fun DrawerContent(
    onLogout: () -> Unit,
    onExit: () -> Unit
) {
    val context = LocalContext.current

    val grupos = remember {
        listOf(
            Grupo(
                id = 0,
                iconRes = R.drawable.banda_alcolea,
                texto = "Banda Municipal de Música de Alcolea"
            ),
            Grupo(
                id = 1,
                iconRes = R.drawable.banda_ejido,
                texto = "Banda Sinfónica El Ejido"
            ),
            Grupo(
                id = 2,
                iconVector = Icons.Default.Add,
                texto = "Crea un grupo nuevo"
            )
        )
    }

    val opcionesInferiores = remember {
        listOf(
            Grupo(
                id = 3,
                iconRes = R.drawable.cerrar_sesion,
                texto = "Cerrar Sesión"
            ),
            Grupo(
                id = 4,
                iconRes = R.drawable.salir_app,
                texto = "Salir"
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

        grupos.forEach { grupo ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = if (grupo.id == seleccionado)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else
                            MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        when (grupo.id) {
                            2 -> Toast.makeText(
                                context,
                                "Funcionalidad en desarrollo",
                                Toast.LENGTH_SHORT
                            ).show()

                            else -> seleccionado = grupo.id
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (grupo.iconRes != null) {
                    Image(
                        painter = painterResource(id = grupo.iconRes),
                        contentDescription = grupo.texto,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else if (grupo.iconVector != null) {
                    Icon(
                        imageVector = grupo.iconVector,
                        contentDescription = grupo.texto,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = grupo.texto, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        opcionesInferiores.forEach { grupo ->
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        when (grupo.id) {
                            3 -> onLogout()
                            4 -> onExit()
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = grupo.iconRes!!),
                    contentDescription = grupo.texto,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = grupo.texto, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}