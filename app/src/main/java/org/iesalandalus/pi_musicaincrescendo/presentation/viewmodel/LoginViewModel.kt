package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.common.utils.Validator
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.LoginUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UserUseCases

class LoginViewModel(
    private val loginUseCase: LoginUseCase = LoginUseCase(AuthRepositoryImpl()),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl(),
    private val userUseCases: UserUseCases = UserUseCases(UserRepositoryImpl())
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _isEmailValid = MutableStateFlow(true)
    val isEmailValid: StateFlow<Boolean> = _isEmailValid

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _isPasswordValid = MutableStateFlow(true)
    val isPasswordValid: StateFlow<Boolean> = _isPasswordValid

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _isEmailValid.value = Validator.isEmailValid(newEmail)
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _isPasswordValid.value = Validator.isPasswordValid(newPassword)
    }

    fun onLogin() {
        if (!_isEmailValid.value || !_isPasswordValid.value) {
            _errorMessage.value = "Compruebe los datos introducidos"
            return
        }
        viewModelScope.launch {
            try {
                loginUseCase(email.value.trim(), password.value.trim())
                _loginSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

    fun onGoogleLogin(idToken: String) {
        viewModelScope.launch {
            try {
                val authResult = authRepository.signInWithGoogle(idToken)
                val user = authResult.user
                if (user != null) {
                    // Si el usuario no existe en la base de datos, lo creamos
                    if (!userUseCases.userExists(user.uid)) {
                        userUseCases.createUserProfile(
                            uid = user.uid,
                            displayName = user.displayName ?: "Usuario sin nombre",
                            gender = "Prefiero no decirlo",
                            isDirector = false,
                            instruments = emptyList(),
                            photoUrl = user.photoUrl?.toString()
                        )
                    }
                    _loginSuccess.value = true
                } else {
                    _errorMessage.value = "No se pudo obtener el usuario de Google."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error en el inicio de sesión con Google: ${e.message}"
            }
        }
    }

    fun reset() {
        _email.value = ""
        _isEmailValid.value = true
        _password.value = ""
        _isPasswordValid.value = true
        _loginSuccess.value = false
        _errorMessage.value = null
    }
}