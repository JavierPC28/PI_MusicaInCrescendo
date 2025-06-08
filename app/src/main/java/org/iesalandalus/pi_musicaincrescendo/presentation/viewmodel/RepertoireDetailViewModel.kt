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

data class RepertoireDetailUiState(
    val isLoading: Boolean = true,
    val repertoire: Repertoire? = null,
    val userProfile: UserProfile? = null,
    val error: String? = null
)

class RepertoireDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val getRepertoireByIdUseCase = GetRepertoireByIdUseCase(RepertoireRepositoryImpl())
    private val userUseCases = UserUseCases(UserRepositoryImpl())
    private val authRepository = AuthRepositoryImpl()

    private val _uiState = MutableStateFlow(RepertoireDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val workId: String? = savedStateHandle["workId"]
        val userId: String? = authRepository.currentUserId()

        if (workId == null || userId == null) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "No se pudo obtener la información necesaria."
            )
        } else {
            loadData(workId, userId)
        }
    }

    private fun loadData(workId: String, userId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val repertoire = getRepertoireByIdUseCase(workId)
                val userProfile = userUseCases.getUserProfile(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    repertoire = repertoire,
                    userProfile = userProfile
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar los datos: ${e.message}"
                )
            }
        }
    }
}