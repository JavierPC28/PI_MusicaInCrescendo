package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UpdateDisplayNameUseCase
import org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel.ProfileViewModel.UiState.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val updateUseCase: UpdateDisplayNameUseCase = UpdateDisplayNameUseCase(
        UserRepositoryImpl()
    )
) : ViewModel() {

    // Estado interno de UI
    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val name: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(Idle)
    val uiState: StateFlow<UiState> = _uiState

    // Nombre actual mostrado
    private val _displayName = MutableStateFlow<String>("")
    val displayName: StateFlow<String> = _displayName

    // Fecha de registro formateada
    val registrationDateFormatted: String by lazy {
        auth.currentUser?.metadata?.creationTimestamp?.let {
            SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES")).format(Date(it))
        } ?: ""
    }

    init {
        // Carga inicial del nombre
        auth.currentUser?.let { user ->
            _displayName.value = user.email
                ?.substringBefore("@")
                ?.replaceFirstChar { it.uppercaseChar() }
                ?: ""
        }
    }

    /**
     * Lanza el flujo de actualización de nombre.
     */
    fun onUpdateName(newName: String) {
        val user = auth.currentUser ?: run {
            _uiState.value = Error("Usuario no autenticado")
            return
        }
        _uiState.value = Loading
        viewModelScope.launch {
            try {
                updateUseCase(user.uid, newName)
                _displayName.value = newName
                _uiState.value = Success(newName)
            } catch (e: Exception) {
                _uiState.value = Error(e.message ?: "Error desconocido")
            }
        }
    }
}