package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la lógica de inicio de sesión.
 * Ahora incluye validación de email y contraseña, y método para resetear campos.
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

    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> get() = _isDirector

    /**
     * Al cambiar email, validamos formato.
     */
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _isEmailValid.value = Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()
    }

    /**
     * Al cambiar contraseña, validamos criterios mínimos:
     * min 8 caracteres, comienza por letra, un dígito y un carácter especial.
     */
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        val regex = Regex("^[A-Za-z](?=.*[0-9])(?=.*[^A-Za-z0-9]).{7,}\$")
        _isPasswordValid.value = regex.matches(newPassword)
    }

    fun onDirectorChecked(checked: Boolean) {
        _isDirector.value = checked
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
        _isDirector.value = false
    }

    fun onLogin() {
        // TODO: Implementar lógica de login con Firebase
    }
}