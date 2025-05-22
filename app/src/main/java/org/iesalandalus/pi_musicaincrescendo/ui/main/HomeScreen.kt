package org.iesalandalus.pi_musicaincrescendo.ui.main

import android.content.Intent
import android.view.View
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.net.toUri
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val homeViewModel: HomeViewModel = viewModel()
    val miembros by homeViewModel.userCount.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // CABECERA FIJA
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                    fontSize = 12.sp,
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

        // RESTO SCROLLEABLE EN GRIS
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
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
                val mapView = rememberMapViewWithLifecycle()
                AndroidView(
                    factory = { mapView },
                    modifier = Modifier.fillMaxSize(),
                    update = { mv ->
                        mv.getMapAsync { map ->
                            map.uiSettings.isZoomControlsEnabled = true
                            val coords = LatLng(36.85059904205266, -2.4650644298497406)
                            map.addMarker(MarkerOptions().position(coords).title("Local de ensayo"))
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 15f))
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Centro Cultural Zaharagüi, Calle Ermita, Alcolea, España",
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 12.dp)
                )
                Text(
                    text = "Cómo llegar",
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        val uri =
                            "geo:0,0?q=Centro Cultural Zaharagüi, Calle Ermita, Alcolea, España".toUri()
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    },
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Redes sociales
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                SocialLink(
                    iconRes = R.drawable.facebook,
                    label = "Facebook",
                    url = "https://www.facebook.com/BandaAlcolea"
                )
                SocialLink(
                    iconRes = R.drawable.instagram,
                    label = "Instagram",
                    url = "https://www.instagram.com/bandaalcolea"
                )
                SocialLink(
                    iconRes = R.drawable.youtube,
                    label = "YouTube",
                    url = "https://www.youtube.com/@bandamunicipaldemusicadeal9891"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Miembros ($miembros)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SocialLink(iconRes: Int, label: String, url: String) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        }
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 14.sp)
    }
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = View.generateViewId()
        }
    }
    DisposableEffect(Unit) {
        MapsInitializer.initialize(context)
        onDispose { }
    }
    return mapView
}