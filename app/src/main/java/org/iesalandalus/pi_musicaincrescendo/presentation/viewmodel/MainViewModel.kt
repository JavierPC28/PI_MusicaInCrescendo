package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepositoryImpl

class MainViewModel(
    private val authRepo: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    fun logout() {
        authRepo.logout()
    }
}