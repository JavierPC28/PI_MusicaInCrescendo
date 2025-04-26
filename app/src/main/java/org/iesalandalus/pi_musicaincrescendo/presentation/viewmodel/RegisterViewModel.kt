package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la lógica de registro.
 */
class RegisterViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> get() = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> get() = _password

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> get() = _confirmPassword

    private val _gender = MutableStateFlow("Hombre")
    val gender: StateFlow<String> get() = _gender

    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> get() = _isDirector

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChange(newPassword: String) {
        _confirmPassword.value = newPassword
    }

    fun onGenderSelected(newGender: String) {
        _gender.value = newGender
    }

    fun onDirectorChecked(checked: Boolean) {
        _isDirector.value = checked
    }

    fun onRegister() {
        // TODO: Implementar lógica de registro con Firebase
    }
}