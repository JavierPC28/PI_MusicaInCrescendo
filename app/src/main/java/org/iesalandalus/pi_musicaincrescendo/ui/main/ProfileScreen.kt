package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.ProfileViewModel

/**
 * Vista de perfil
 */
@Composable
fun ProfileScreen() {
    // ViewModel para obtener datos del usuario
    val viewModel: ProfileViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de perfil con borde
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.perfil_neutro),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre de usuario con icono de edición
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = viewModel.displayName,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = { /* Acción de editar nombre */ },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar nombre",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Texto instrucción instrumento
            Text(
                text = "Seleccione su instrumento principal...",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Pie de pantalla con fecha de registro
        Text(
            text = "En Banda Municipal de Alcolea desde el ${viewModel.registrationDateFormatted}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}