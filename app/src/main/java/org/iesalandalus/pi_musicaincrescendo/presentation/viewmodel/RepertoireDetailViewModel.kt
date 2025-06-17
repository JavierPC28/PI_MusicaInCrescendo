package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.*
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.model.UserProfile
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetRepertoireByIdUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UserUseCases

/**
 * Define el estado de la interfaz de usuario para la pantalla de detalle del repertorio.
 * @param isLoading Indica si se están cargando los datos.
 * @param repertoire La obra del repertorio a mostrar.
 * @param userProfile El perfil del usuario actual.
 * @param error Mensaje de error, si lo hubiera.
 */
data class RepertoireDetailUiState(
    val isLoading: Boolean = true,
    val repertoire: Repertoire? = null,
    val userProfile: UserProfile? = null,
    val error: String? = null
)

/**
 * Gestiona el estado y la lógica de la pantalla de detalle de una obra del repertorio.
 */
class RepertoireDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Casos de uso para obtener datos del repertorio y del usuario.
    private val getRepertoireByIdUseCase = GetRepertoireByIdUseCase(RepertoireRepositoryImpl())
    private val userUseCases = UserUseCases(UserRepositoryImpl())
    private val authRepository = AuthRepositoryImpl()

    // Flujo de estado que emite el estado actual de la UI.
    private val _uiState = MutableStateFlow(RepertoireDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Obtiene los IDs necesarios desde los argumentos de navegación.
        val workId: String? = savedStateHandle["workId"]
        val userId: String? = authRepository.currentUserId()

        // Valida que los IDs no sean nulos antes de cargar los datos.
        if (workId == null || userId == null) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "No se pudo obtener la información necesaria."
            )
        } else {
            loadData(workId, userId)
        }
    }

    /**
     * Carga los datos de la obra y el perfil del usuario de forma asíncrona.
     * @param workId El ID de la obra a cargar.
     * @param userId El ID del usuario actual.
     */
    private fun loadData(workId: String, userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Obtiene la obra y el perfil del usuario concurrentemente.
                val repertoire = getRepertoireByIdUseCase(workId)
                val userProfile = userUseCases.getUserProfile(userId)
                // Actualiza el estado de la UI con los datos cargados.
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    repertoire = repertoire,
                    userProfile = userProfile
                )
            } catch (e: Exception) {
                // Maneja errores durante la carga.
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar los datos: ${e.message}"
                )
            }
        }
    }
}