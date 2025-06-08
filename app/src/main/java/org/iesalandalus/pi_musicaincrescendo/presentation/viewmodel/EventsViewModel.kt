package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.EventRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetEventsUseCase

class EventsViewModel(
    private val getEventsUseCase: GetEventsUseCase = GetEventsUseCase(EventRepositoryImpl())
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadEvents()
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