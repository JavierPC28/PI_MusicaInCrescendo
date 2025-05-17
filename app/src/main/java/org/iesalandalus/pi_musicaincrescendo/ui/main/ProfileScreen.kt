package org.iesalandalus.pi_musicaincrescendo.ui.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
            // Imagen de perfil en forma circular con borde
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = R.drawable.perfil_neutro),
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre de usuario
            Text(
                text = viewModel.displayName,
                style = MaterialTheme.typography.headlineSmall
            )

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