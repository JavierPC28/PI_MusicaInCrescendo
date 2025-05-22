package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.common.utils.Validator
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.RegisterUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UserUseCases

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase = RegisterUseCase(AuthRepositoryImpl()),
    private val userUseCases: UserUseCases = UserUseCases(UserRepositoryImpl())
) : ViewModel() {

    // --- Estados de formulario ---
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _isEmailValid = MutableStateFlow(true)
    val isEmailValid: StateFlow<Boolean> = _isEmailValid

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isPasswordValid = MutableStateFlow(true)
    val isPasswordValid: StateFlow<Boolean> = _isPasswordValid

    private val _confirmPassword = MutableStateFlow("")
    val confirmPassword: StateFlow<String> = _confirmPassword

    private val _isConfirmPasswordValid = MutableStateFlow(true)
    val isConfirmPasswordValid: StateFlow<Boolean> = _isConfirmPasswordValid

    private val _gender = MutableStateFlow("-- Seleccione su género --")
    val gender: StateFlow<String> = _gender

    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> = _isDirector

    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // --- Actualizaciones de campos ---
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

    // --- Lógica de registro ---
    fun onRegister() {
        if (!_isEmailValid.value || !_isPasswordValid.value || !_isConfirmPasswordValid.value) {
            _errorMessage.value = "Compruebe los datos introducidos"
            return
        }
        viewModelScope.launch {
            try {
                // 1) Registramos en Firebase Authentication
                val result: AuthResult = registerUseCase(
                    email.value.trim(),
                    password.value.trim()
                )
                val uid = result.user?.uid
                    ?: throw Exception("No se obtuvo el UID del usuario")

                // 2) Creamos el perfil inicial en Realtime Database
                val defaultName = email.value
                    .substringBefore("@")
                    .replaceFirstChar { it.uppercaseChar() }
                userUseCases.createUserProfile(
                    uid,
                    defaultName,
                    gender.value,
                    isDirector.value
                )

                // 3) Señalamos éxito
                _registrationSuccess.value = true

            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }
}