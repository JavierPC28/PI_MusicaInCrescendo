package org.iesalandalus.pi_musicaincrescendo.ui.main

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    // Tamaño común para los iconos de redes sociales
    val socialIconSize = 32.dp

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
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "BANDA DE MÚSICA",
                    fontSize = 14.sp,
                    color = Color(0xFFFFA500)
                )
                Text(
                    text = "Banda Municipal de Música de Alcolea",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
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
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Card interno vacío por el momento
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                ) {}
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Centro Cultural Zaharagüi, Calle Ermita, Alcolea, España",
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis // Para ajustar a dos líneas
                    )
                    Text(
                        text = "Cómo llegar",
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            // Intent a mapa para llegar
                            val uri =
                                "geo:0,0?q=Centro Cultural Zaharagüi, Calle Ermita, Alcolea, España".toUri()
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Enlaces a redes sociales
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Facebook
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.facebook.com/BandaAlcolea".toUri()
                    )
                    context.startActivity(intent)
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.facebook),
                    contentDescription = "Facebook",
                    modifier = Modifier.size(socialIconSize)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Facebook",
                    fontSize = 12.sp
                )
            }
            // Instagram
            Row(
                verticalAlignment = Alignment.CenterVertically,
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
                    modifier = Modifier.size(socialIconSize)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Instagram",
                    fontSize = 12.sp
                )
            }
            // YouTube
            Row(
                verticalAlignment = Alignment.CenterVertically,
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
                    modifier = Modifier.size(socialIconSize)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "YouTube",
                    fontSize = 12.sp
                )
            }
        }
    }
}