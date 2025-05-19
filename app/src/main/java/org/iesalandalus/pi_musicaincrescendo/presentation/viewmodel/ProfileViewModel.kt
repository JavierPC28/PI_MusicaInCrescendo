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
    private val updateNameUseCase: UpdateDisplayNameUseCase = UpdateDisplayNameUseCase(
        UserRepositoryImpl()
    ),
    private val updateInstrumentsUseCase: UpdateInstrumentsUseCase = UpdateInstrumentsUseCase(
        UserRepositoryImpl()
    )
) : ViewModel() {

    // --- Estados de UI ---
    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val error: String) : UiState()
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

    // Fecha de registro formateada
    val registrationDateFormatted: String by lazy {
        auth.currentUser?.metadata?.creationTimestamp
            ?.let { ts ->
                SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES")).format(
                    Date(
                        ts
                    )
                )
            }
            ?: ""
    }

    init {
        // Carga inicial de perfil
        auth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                _uiState.value = UiState.Loading
                try {
                    val profile: UserProfile = getProfileUseCase(uid)
                    _displayName.value = profile.displayName
                    _gender.value = profile.gender
                    _isDirector.value = profile.isDirector

                    // Si es director, forzamos "DIRECCIÓN MUSICAL" siempre seleccionado
                    val inicial = mutableListOf<String>().apply {
                        if (profile.isDirector) add("DIRECCIÓN MUSICAL")
                        addAll(profile.instruments.filter { it != "DIRECCIÓN MUSICAL" })
                    }
                    _selectedInstruments.value = inicial

                    _uiState.value = UiState.Idle
                } catch (e: Exception) {
                    _uiState.value = UiState.Error("Error al cargar perfil")
                }
            }
        }
    }

    fun onUpdateName(newName: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                updateNameUseCase(uid, newName)
                _displayName.value = newName
                _uiState.value = UiState.Success("Nombre actualizado")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("No se pudo actualizar nombre")
            }
        }
    }

    fun onInstrumentToggle(instrument: String) {
        val isDir = _isDirector.value
        // No tocamos "DIRECCIÓN MUSICAL" para director
        if (isDir && instrument == "DIRECCIÓN MUSICAL") return

        val current = _selectedInstruments.value.toMutableList()
        if (current.contains(instrument)) {
            current.remove(instrument)
        } else {
            // Límite: 3 totales
            if (current.size < 3) {
                current.add(instrument)
            }
        }
        _selectedInstruments.value = current

        // Persistencia en Firebase
        auth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                try {
                    updateInstrumentsUseCase(uid, current)
                } catch (_: Exception) { /* Manejo de errores */
                }
            }
        }
    }
}