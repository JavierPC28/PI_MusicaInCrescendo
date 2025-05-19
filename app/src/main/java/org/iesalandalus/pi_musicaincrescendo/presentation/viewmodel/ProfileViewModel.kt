package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserProfile
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetUserProfileUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UpdateDisplayNameUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UpdateInstrumentsUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val getProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase(UserRepositoryImpl()),
    private val updateUseCase: UpdateDisplayNameUseCase = UpdateDisplayNameUseCase(
        UserRepositoryImpl()
    ),
    private val updateInstrumentsUseCase: UpdateInstrumentsUseCase = UpdateInstrumentsUseCase(UserRepositoryImpl())
) : ViewModel() {

    // Estado interno de UI
    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val name: String) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> = _isDirector

    private val _selectedInstruments = MutableStateFlow<List<String>>(emptyList())
    val selectedInstruments: StateFlow<List<String>> = _selectedInstruments

    val registrationDateFormatted: String by lazy {
        auth.currentUser?.metadata?.creationTimestamp?.let {
            SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES")).format(Date(it))
        } ?: ""
    }

    init {
        // Cargamos perfil al iniciar
        auth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                try {
                    _uiState.value = UiState.Loading
                    val profile: UserProfile = getProfileUseCase(uid)
                    _displayName.value = profile.displayName
                    _gender.value = profile.gender
                    _isDirector.value = profile.isDirector
                    _selectedInstruments.value = profile.instruments
                    _uiState.value = UiState.Idle
                } catch (e: Exception) {
                    _uiState.value = UiState.Error(e.message ?: "Error al cargar perfil")
                }
            }
        }
    }

    /**
     * Lanza el flujo de actualización de nombre.
     */
    fun onUpdateName(newName: String) {
        val user = auth.currentUser ?: run {
            _uiState.value = UiState.Error("Usuario no autenticado")
            return
        }
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                updateUseCase(user.uid, newName)
                _displayName.value = newName
                _uiState.value = UiState.Success(newName)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun onInstrumentToggle(instrument: String) {
        val current = _selectedInstruments.value.toMutableList()
        if (current.contains(instrument)) {
            current.remove(instrument)
        } else if (current.size < 3) {
            current.add(instrument)
        }
        _selectedInstruments.value = current

        auth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                try {
                    updateInstrumentsUseCase(uid, current)
                } catch (_: Exception) {
                    // Manejo de errores
                }
            }
        }
    }
}