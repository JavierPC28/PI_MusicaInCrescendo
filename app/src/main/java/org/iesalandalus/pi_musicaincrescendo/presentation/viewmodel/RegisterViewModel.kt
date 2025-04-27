package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la lógica de registro.
 * Añade validación de email y contraseña.
 */
class RegisterViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> get() = _email

    private val _isEmailValid = MutableStateFlow(true)
    val isEmailValid: StateFlow<Boolean> get() = _isEmailValid

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> get() = _password

    private val _isPasswordValid = MutableStateFlow(true)
    val isPasswordValid: StateFlow<Boolean> get() = _isPasswordValid

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> get() = _confirmPassword

    // Por defecto “Seleccione su género”
    private val _gender = MutableStateFlow("Seleccione su género")
    val gender: StateFlow<String> get() = _gender

    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> get() = _isDirector

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _isEmailValid.value = validateEmail(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _isPasswordValid.value = validatePassword(newPassword)
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
        // TODO: Implementar lógica de registro con Firebase,
        // verificando isEmailValid y isPasswordValid antes de proceder.
    }

    // Validación de formato de correo
    private fun validateEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Contraseña: min 8 caracteres, comienza por letra,
    // al menos un número y un carácter especial
    private fun validatePassword(pw: String): Boolean {
        val regex = Regex("^[A-Za-z](?=.*[0-9])(?=.*[^A-Za-z0-9]).{7,}\$")
        return regex.matches(pw)
    }
}