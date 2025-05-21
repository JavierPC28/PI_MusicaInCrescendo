package org.iesalandalus.pi_musicaincrescendo.ui.main

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import org.iesalandalus.pi_musicaincrescendo.R

/**
 * Vista principal con cabecera, local de ensayo y enlaces a redes sociales.
 */
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Cabecera con imagen y textos
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.banda_alcolea),
                contentDescription = "Icono Banda de Alcolea",
                modifier = Modifier
                    .size(64.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "BANDA DE MÚSICA",
                    fontSize = 18.sp
                )
                Text(
                    text = "Banda Municipal de Música de Alcolea",
                    fontSize = 14.sp
                )
                Text(
                    text = "Alcolea, Almería, España",
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección de local de ensayo
        Text(
            text = "Local de ensayo",
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            // Card vacío por el momento
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Enlaces a redes sociales
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Facebook
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://m.facebook.com/BandaAlcolea".toUri()
                    )
                    context.startActivity(intent)
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook",
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "Facebook",
                    fontSize = 12.sp
                )
            }
            // Instagram
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.instagram.com/BandaAlcolea".toUri()
                    )
                    context.startActivity(intent)
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.instagram),
                    contentDescription = "Instagram",
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "Instagram",
                    fontSize = 12.sp
                )
            }
            // YouTube
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.youtube.com/BandaAlcolea".toUri()
                    )
                    context.startActivity(intent)
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.youtube),
                    contentDescription = "YouTube",
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "YouTube",
                    fontSize = 12.sp
                )
            }
        }
    }
}