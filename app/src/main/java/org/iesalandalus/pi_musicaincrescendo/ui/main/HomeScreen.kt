package org.iesalandalus.pi_musicaincrescendo.ui.main

import android.content.Intent
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.iesalandalus.pi_musicaincrescendo.R
import org.iesalandalus.pi_musicaincrescendo.common.utils.ImageHelper
import org.iesalandalus.pi_musicaincrescendo.common.utils.ImageHelper.getInstrumentDrawable
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val miembros by homeViewModel.userCount.collectAsState()
    val membersList by homeViewModel.members.collectAsState()

    // Lista de instrumentos para ordenación
    val instrumentosList = listOf(
        "DIRECCIÓN MUSICAL", "FLAUTÍN", "FLAUTA", "OBOE", "CORNO INGLÉS",
        "FAGOT", "CONTRAFAGOT", "REQUINTO", "CLARINETE", "CLARINETE BAJO",
        "SAXO SOPRANO", "SAXO ALTO", "SAXO TENOR", "SAXO BARÍTONO",
        "TROMPA", "FLISCORNO", "TROMPETA", "TROMBÓN", "TROMBÓN BAJO",
        "BOMBARDINO", "TUBA", "VIOLONCHELO", "CONTRABAJO", "CAJA", "PERCUSIÓN",
        "BOMBO", "PLATOS", "TIMBALES", "LÁMINAS", "BATERÍA"
    )

    // Ordenamos miembros
    val sortedMembers = remember(membersList) {
        membersList.sortedBy { profile ->
            val principal = profile.instruments.firstOrNull().orEmpty()
            instrumentosList.indexOf(principal).takeIf { it >= 0 } ?: instrumentosList.size
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // HEADER siempre visible
        HeaderSection()

        // Contenido desplazable
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFEEEEEE))
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Local de ensayo",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Card mapa y dirección
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        val mapView = rememberMapViewWithLifecycle()
                        AndroidView(
                            factory = { mapView },
                            modifier = Modifier.fillMaxSize(),
                            update = { mv ->
                                mv.getMapAsync { map ->
                                    map.uiSettings.isScrollGesturesEnabled = true
                                    map.uiSettings.isZoomGesturesEnabled = true
                                    val coords = LatLng(36.85059904205266, -2.4650644298497406)
                                    map.addMarker(
                                        MarkerOptions().position(coords).title("Local de ensayo")
                                    )
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 15f))
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Centro Cultural Zaharagüi, Calle Ermita, Alcolea, España",
                            fontSize = 14.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
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
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

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

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de miembros
            Text(
                text = "Miembros ($miembros)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            sortedMembers.forEach { profile ->
                MemberRow(
                    displayName = profile.displayName,
                    gender = profile.gender,
                    isDirector = profile.isDirector,
                    instrument = profile.instruments.firstOrNull().orEmpty()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        androidx.compose.foundation.Image(
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
        androidx.compose.foundation.Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 14.sp)
    }
}

@Composable
private fun MemberRow(
    displayName: String,
    gender: String,
    isDirector: Boolean,
    instrument: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        val imageRes = ImageHelper.getProfileImage(gender, isDirector)
        androidx.compose.foundation.Image(
            painter = painterResource(id = imageRes),
            contentDescription = displayName,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        androidx.compose.foundation.Image(
            painter = painterResource(id = getInstrumentDrawable(instrument)),
            contentDescription = instrument,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayName,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = instrument.ifEmpty { "Sin instrumento" },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember(context) {
        MapView(context).apply { id = View.generateViewId() }
    }
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _: LifecycleOwner, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {/* ... */
                }
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    return mapView
}