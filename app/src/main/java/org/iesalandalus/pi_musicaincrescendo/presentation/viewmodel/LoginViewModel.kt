package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.iesalandalus.pi_musicaincrescendo.common.utils.Validator

/**
 * ViewModel para la lógica de inicio de sesión.
 * Incluye validación de email y contraseña, y método para resetear campos.
 */
class LoginViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> get() = _email

    private val _isEmailValid = MutableStateFlow(true)
    val isEmailValid: StateFlow<Boolean> get() = _isEmailValid

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> get() = _password

    private val _isPasswordValid = MutableStateFlow(true)
    val isPasswordValid: StateFlow<Boolean> get() = _isPasswordValid

    /**
     * Al cambiar email, validamos formato.
     */
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _isEmailValid.value = Validator.isEmailValid(newEmail)
    }

    /**
     * Al cambiar contraseña, validamos criterios mínimos.
     */
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _isPasswordValid.value = Validator.isPasswordValid(newPassword)
    }

    /**
     * Resetea todos los campos de login.
     * Se llamará antes de navegar a registro para que no persistan valores.
     */
    fun resetFields() {
        _email.value = ""
        _isEmailValid.value = true
        _password.value = ""
        _isPasswordValid.value = true
    }

    fun onLogin() {
        // TODO: Implementar lógica de login con Firebase
    }
}