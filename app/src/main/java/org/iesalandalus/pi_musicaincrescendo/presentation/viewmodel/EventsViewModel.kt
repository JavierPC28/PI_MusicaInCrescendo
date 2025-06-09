package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.*
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetEventsUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UserUseCases

class EventsViewModel(
    private val getEventsUseCase: GetEventsUseCase = GetEventsUseCase(EventRepositoryImpl()),
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val userUseCases: UserUseCases = UserUseCases(UserRepositoryImpl())
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> = _isDirector.asStateFlow()

    init {
        loadEvents()
        loadUserRole()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            authRepository.currentUserId()?.let { uid ->
                try {
                    val userProfile = userUseCases.getUserProfile(uid)
                    _isDirector.value = userProfile.isDirector
                } catch (_: Exception) {
                    _error.value = "Error al cargar los datos del usuario."
                }
            }
        }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getEventsUseCase().collect {
                    _events.value = it.sortedByDescending { event -> event.date }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar los eventos: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}