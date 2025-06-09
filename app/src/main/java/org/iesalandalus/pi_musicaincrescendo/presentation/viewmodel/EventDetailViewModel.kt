package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.EventRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.data.repository.RepertoireRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.model.User
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetEventByIdUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetRepertoireByIdUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UserUseCases

data class EventDetailUiState(
    val isLoading: Boolean = true,
    val event: Event? = null,
    val repertoire: List<Repertoire> = emptyList(),
    val members: List<User> = emptyList(),
    val error: String? = null,
    val selectedTab: Int = 0 // 0: Detalles, 1: Repertorio, 2: Miembros
)

class EventDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val getEventByIdUseCase = GetEventByIdUseCase(EventRepositoryImpl())
    private val getRepertoireByIdUseCase = GetRepertoireByIdUseCase(RepertoireRepositoryImpl())
    private val userUseCases = UserUseCases(UserRepositoryImpl())

    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    private val eventId: String = checkNotNull(savedStateHandle["eventId"])

    init {
        loadEventDetails()
    }

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    private fun loadEventDetails() {
        viewModelScope.launch {
            _uiState.value = EventDetailUiState(isLoading = true)
            try {
                val event = getEventByIdUseCase(eventId)
                if (event == null) {
                    _uiState.value =
                        EventDetailUiState(isLoading = false, error = "Evento no encontrado.")
                    return@launch
                }

                val repertoireList = event.repertoireIds.keys.mapNotNull { repertoireId ->
                    getRepertoireByIdUseCase(repertoireId)
                }

                val attendingUserIds = event.asistencias.filter { it.value == "IRÉ" }.keys
                val memberList = attendingUserIds.mapNotNull { userId ->
                    try {
                        val profile = userUseCases.getUserProfile(userId)
                        User(uid = userId, profile = profile)
                    } catch (_: Exception) {
                        null
                    }
                }

                _uiState.value = EventDetailUiState(
                    isLoading = false,
                    event = event,
                    repertoire = repertoireList,
                    members = memberList,
                    selectedTab = 0
                )

            } catch (e: Exception) {
                _uiState.value = EventDetailUiState(
                    isLoading = false,
                    error = "Error al cargar los detalles: ${e.message}"
                )
            }
        }
    }
}