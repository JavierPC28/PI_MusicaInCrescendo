package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepositoryImpl

/**
 * ViewModel para la lógica de la pantalla Home,
 * incluyendo el cierre de sesión.
 */
class HomeViewModel(
    private val authRepo: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    /**
     * Cerramos la sesión del usuario
     */
    fun logout() {
        authRepo.logout()
    }
}