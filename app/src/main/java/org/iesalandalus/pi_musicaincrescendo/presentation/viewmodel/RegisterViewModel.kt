package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants
import org.iesalandalus.pi_musicaincrescendo.common.utils.Validator
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.RegisterUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UserUseCases

/**
 * Gestiona el estado y la lógica para la pantalla de registro de nuevos usuarios.
 */
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase = RegisterUseCase(AuthRepositoryImpl()),
    private val userUseCases: UserUseCases = UserUseCases(UserRepositoryImpl())
) : ViewModel() {

    // Estados de los campos del formulario de registro.
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

    // Estado para indicar si el registro fue exitoso.
    private val _registrationSuccess = MutableStateFlow(false)
    val registrationSuccess: StateFlow<Boolean> = _registrationSuccess

    // Mensaje de error en caso de fallo en el registro.
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    /**
     * Actualiza el estado del correo y su validez.
     */
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _isEmailValid.value = Validator.isEmailValid(newEmail)
    }

    /**
     * Actualiza la contraseña y valida tanto su formato como la coincidencia con la confirmación.
     */
    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _isPasswordValid.value = Validator.isPasswordValid(newPassword)
        _isConfirmPasswordValid.value = (_confirmPassword.value == newPassword)
    }

    /**
     * Actualiza la confirmación de contraseña y valida que coincida con la contraseña.
     */
    fun onConfirmPasswordChange(newPassword: String) {
        _confirmPassword.value = newPassword
        _isConfirmPasswordValid.value = (newPassword == _password.value)
    }

    /**
     * Actualiza el género seleccionado.
     */
    fun onGenderSelected(newGender: String) {
        _gender.value = newGender
    }

    /**
     * Actualiza si el usuario se registra como director.
     */
    fun onDirectorChecked(checked: Boolean) {
        _isDirector.value = checked
    }

    /**
     * Inicia el proceso de registro del usuario.
     */
    fun onRegister() {
        // Valida que todos los campos sean correctos antes de proceder.
        if (!_isEmailValid.value || !_isPasswordValid.value || !_isConfirmPasswordValid.value) {
            _errorMessage.value = "Compruebe los datos introducidos"
            return
        }
        viewModelScope.launch {
            try {
                // 1. Registra al usuario en Firebase Authentication.
                val result: AuthResult = registerUseCase(
                    email.value.trim(),
                    password.value.trim()
                )
                val uid = result.user?.uid
                    ?: throw Exception("No se obtuvo el UID del usuario")

                // 2. Crea un perfil de usuario inicial en Realtime Database.
                val defaultName = email.value
                    .substringBefore("@")
                    .replaceFirstChar { it.uppercaseChar() }

                // Asigna "DIRECCIÓN MUSICAL" si el usuario es director.
                val initialInstruments = mutableListOf<String>().apply {
                    if (isDirector.value) {
                        add(Constants.DIRECCION_MUSICAL)
                    }
                }

                userUseCases.createUserProfile(
                    uid,
                    defaultName,
                    gender.value,
                    isDirector.value,
                    initialInstruments
                )

                // 3. Indica que el registro fue exitoso.
                _registrationSuccess.value = true

            } catch (e: Exception) {
                // Captura y muestra errores de registro.
                _errorMessage.value = e.message
            }
        }
    }
}