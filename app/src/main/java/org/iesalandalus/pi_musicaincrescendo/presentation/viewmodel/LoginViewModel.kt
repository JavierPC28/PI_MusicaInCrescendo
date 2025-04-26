package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la lógica de inicio de sesión.
 */
class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> get() = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> get() = _password

    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> get() = _isDirector

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onDirectorChecked(checked: Boolean) {
        _isDirector.value = checked
    }

    fun onLogin() {
        // TODO: Implementar lógica de login con Firebase
    }
}