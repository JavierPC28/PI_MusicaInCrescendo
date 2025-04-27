package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.iesalandalus.pi_musicaincrescendo.common.utils.Validator

/**
 * ViewModel para la lógica de registro.
 * Añade validación de email, contraseña y comprobación de que password == confirmPassword.
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

    private val _isConfirmPasswordValid = MutableStateFlow(true)
    val isConfirmPasswordValid: StateFlow<Boolean> get() = _isConfirmPasswordValid

    private val _gender = MutableStateFlow("-- Seleccione su género --")
    val gender: StateFlow<String> get() = _gender

    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> get() = _isDirector

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _isEmailValid.value = Validator.isEmailValid(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _isPasswordValid.value = Validator.isPasswordValid(newPassword)
        _isConfirmPasswordValid.value = (_confirmPassword.value == newPassword)
    }

    fun onConfirmPasswordChange(newPassword: String) {
        _confirmPassword.value = newPassword
        _isConfirmPasswordValid.value = (newPassword == _password.value)
    }

    fun onGenderSelected(newGender: String) {
        _gender.value = newGender
    }

    fun onDirectorChecked(checked: Boolean) {
        _isDirector.value = checked
    }

    fun onRegister() {
        // TODO: Implementar lógica de registro con Firebase,
        // verificando isEmailValid, isPasswordValid e isConfirmPasswordValid antes de proceder.
    }
}