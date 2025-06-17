package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants.DIRECCION_MUSICAL
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.model.UserProfile
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UserUseCases
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Gestiona el estado y la lógica de la pantalla de perfil de usuario.
 */
class ProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userUseCases: UserUseCases = UserUseCases(UserRepositoryImpl())
) : ViewModel() {

    // Define los posibles estados de la interfaz de usuario para operaciones asíncronas.
    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        data class Success(val message: String) : UiState()
        data class Error(val error: String) : UiState()
    }

    // Flujo de estado para comunicar el estado de la UI (carga, éxito, error).
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    // Estado para el nombre de usuario.
    private val _displayName = MutableStateFlow("")
    val displayName: StateFlow<String> = _displayName

    // Estado para el género del usuario.
    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    // Indica si el usuario es director.
    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> = _isDirector

    // Estado para la URL de la foto de perfil.
    private val _photoUrl = MutableStateFlow<String?>(null)
    val photoUrl: StateFlow<String?> = _photoUrl

    // Estado para la lista de instrumentos seleccionados por el usuario.
    private val _selectedInstruments = MutableStateFlow<List<String>>(emptyList())
    val selectedInstruments: StateFlow<List<String>> = _selectedInstruments

    // Fecha de registro del usuario, formateada perezosamente una sola vez.
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
        // Carga el perfil del usuario al iniciar el ViewModel.
        auth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                _uiState.value = UiState.Loading
                try {
                    val profile: UserProfile = userUseCases.getUserProfile(uid)
                    _displayName.value = profile.displayName
                    _gender.value = profile.gender
                    _isDirector.value = profile.isDirector
                    _photoUrl.value = profile.photoUrl

                    // Asegura que "DIRECCIÓN MUSICAL" esté presente y sea el primero para directores.
                    val inicial = mutableListOf<String>().apply {
                        if (profile.isDirector) add(DIRECCION_MUSICAL)
                        addAll(profile.instruments.filter { it != DIRECCION_MUSICAL })
                    }
                    _selectedInstruments.value = inicial

                    _uiState.value = UiState.Idle
                } catch (_: Exception) {
                    _uiState.value = UiState.Error("Error al cargar perfil")
                }
            }
        }
    }

    /**
     * Actualiza el nombre de usuario en la base de datos.
     * @param newName El nuevo nombre a establecer.
     */
    fun onUpdateName(newName: String) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                userUseCases.updateDisplayName(uid, newName)
                _displayName.value = newName
                _uiState.value = UiState.Success("Nombre actualizado")
            } catch (_: Exception) {
                _uiState.value = UiState.Error("No se pudo actualizar nombre")
            }
        }
    }

    /**
     * Sube una nueva imagen de perfil, la actualiza en el perfil y actualiza el estado local.
     * @param uri La URI de la imagen seleccionada.
     */
    fun onProfileImageChange(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val newPhotoUrl = userUseCases.uploadProfileImage(uid, uri)
                userUseCases.updatePhotoUrl(uid, newPhotoUrl)
                _photoUrl.value = newPhotoUrl
                _uiState.value = UiState.Success("Foto de perfil actualizada")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al cambiar la foto: ${e.message}")
            }
        }
    }


    /**
     * Añade o elimina un instrumento de la lista del usuario.
     * @param instrument El instrumento a añadir o quitar.
     */
    fun onInstrumentToggle(instrument: String) {
        val isDir = _isDirector.value
        // El instrumento "DIRECCIÓN MUSICAL" no se puede deseleccionar para un director.
        if (isDir && instrument == DIRECCION_MUSICAL) return

        val current = _selectedInstruments.value.toMutableList()
        if (current.contains(instrument)) {
            current.remove(instrument)
        } else {
            // Aplica el límite de 3 instrumentos.
            if (current.size < 3) current.add(instrument)
        }
        _selectedInstruments.value = current

        // Persiste los cambios en Firebase.
        auth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                try {
                    userUseCases.updateInstruments(uid, current)
                } catch (_: Exception) { /* Manejo de error futuro */
                }
            }
        }
    }
}