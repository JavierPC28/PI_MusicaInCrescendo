package org.iesalandalus.pi_musicaincrescendo.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.iesalandalus.pi_musicaincrescendo.ui.main.*

/**
 * Wrapper de HomeScreen que recibe el callback de logout del ViewModel.
 */
@Composable
fun HomeScreenWrapper(onLogout: () -> Unit) {
    HomeScreen(
        onLogout = onLogout
    )
}

/**
 * Wrapper de EventsScreen.
 */
@Composable
fun EventsScreenWrapper(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        EventsScreen()
    }
}

/**
 * Wrapper de RepertoireScreen.
 */
@Composable
fun RepertoireScreenWrapper(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        RepertoireScreen()
    }
}

/**
 * Wrapper de NotificationsScreen.
 */
@Composable
fun NotificationsScreenWrapper(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        NotificationsScreen()
    }
}

/**
 * Wrapper de ProfileScreen.
 */
@Composable
fun ProfileScreenWrapper(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        ProfileScreen()
    }
}