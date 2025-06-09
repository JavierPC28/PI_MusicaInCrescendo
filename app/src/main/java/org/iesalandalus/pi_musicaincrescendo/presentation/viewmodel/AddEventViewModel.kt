package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.EventRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.data.repository.RepertoireRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.model.EventType
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.AddEventUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetEventByIdUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetRepertoireUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UpdateEventUseCase

class AddEventViewModel(
    private val addEventUseCase: AddEventUseCase = AddEventUseCase(EventRepositoryImpl()),
    private val getRepertoireUseCase: GetRepertoireUseCase = GetRepertoireUseCase(
        RepertoireRepositoryImpl()
    ),
    private val getEventByIdUseCase: GetEventByIdUseCase = GetEventByIdUseCase(EventRepositoryImpl()),
    private val updateEventUseCase: UpdateEventUseCase = UpdateEventUseCase(EventRepositoryImpl())
) : ViewModel() {

    private var eventId: String? = null
    private var originalEvent: Event? = null

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _eventType = MutableStateFlow<EventType?>(null)
    val eventType: StateFlow<EventType?> = _eventType.asStateFlow()

    private val _date = MutableStateFlow("")
    val date: StateFlow<String> = _date.asStateFlow()

    private val _startTime = MutableStateFlow("")
    val startTime: StateFlow<String> = _startTime.asStateFlow()

    private val _endTime = MutableStateFlow("")
    val endTime: StateFlow<String> = _endTime.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _allRepertoire = MutableStateFlow<List<Repertoire>>(emptyList())
    val allRepertoire: StateFlow<List<Repertoire>> = _allRepertoire.asStateFlow()

    private val _selectedRepertoire = MutableStateFlow<Map<String, String>>(emptyMap())
    val selectedRepertoire: StateFlow<Map<String, String>> = _selectedRepertoire.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    val isFormValid: StateFlow<Boolean> = combine(
        _title,
        _eventType,
        _date,
        _startTime,
        _endTime,
        _location,
        _selectedRepertoire
    ) { values ->
        val titleValue = values[0] as String
        val type = values[1] as? EventType
        val dateValue = values[2] as String
        val startTimeValue = values[3] as String
        val endTimeValue = values[4] as String
        val locationValue = values[5] as String
        val repertoireValue = values[6] as Map<*, *>

        titleValue.isNotBlank() &&
                type != null &&
                dateValue.isNotBlank() &&
                startTimeValue.isNotBlank() &&
                endTimeValue.isNotBlank() &&
                locationValue.isNotBlank() &&
                repertoireValue.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            getRepertoireUseCase().collect {
                _allRepertoire.value = it
            }
        }
    }

    fun loadEventForEditing(id: String?) {
        if (id == null || id == eventId) return
        this.eventId = id
        viewModelScope.launch {
            try {
                val event = getEventByIdUseCase(id)
                if (event != null) {
                    originalEvent = event
                    _title.value = event.title
                    _eventType.value = EventType.entries.find { it.displayName == event.type }
                    _date.value = event.date
                    _startTime.value = event.startTime
                    _endTime.value = event.endTime
                    _location.value = event.location
                    _selectedRepertoire.value = event.repertoireIds
                } else {
                    _saveError.value = "No se pudo encontrar el evento para editar."
                }
            } catch (e: Exception) {
                _saveError.value = "Error al cargar el evento: ${e.message}"
            }
        }
    }

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onEventTypeSelected(type: EventType) {
        _eventType.value = type
    }

    fun onDateSelected(newDate: String) {
        _date.value = newDate
    }

    fun onStartTimeSelected(newTime: String) {
        _startTime.value = newTime
    }

    fun onEndTimeSelected(newTime: String) {
        _endTime.value = newTime
    }

    fun onLocationChange(newLocation: String) {
        _location.value = newLocation
    }

    fun onRepertoireToggle(repertoireItem: Repertoire, isSelected: Boolean) {
        val current = _selectedRepertoire.value.toMutableMap()
        if (isSelected) {
            current[repertoireItem.id] = repertoireItem.title
        } else {
            current.remove(repertoireItem.id)
        }
        _selectedRepertoire.value = current
    }

    fun onSaveEvent() {
        if (!isFormValid.value) return

        viewModelScope.launch {
            try {
                if (eventId == null) {
                    addEventUseCase(
                        title = _title.value.trim(),
                        type = _eventType.value!!.displayName,
                        date = _date.value,
                        startTime = _startTime.value,
                        endTime = _endTime.value,
                        location = _location.value.trim(),
                        repertoire = _selectedRepertoire.value
                    )
                } else {
                    val updatedEvent = Event(
                        id = eventId!!,
                        title = _title.value.trim(),
                        type = _eventType.value!!.displayName,
                        date = _date.value,
                        startTime = _startTime.value,
                        endTime = _endTime.value,
                        location = _location.value.trim(),
                        repertoireIds = _selectedRepertoire.value,
                        asistencias = originalEvent?.asistencias ?: emptyMap()
                    )
                    updateEventUseCase(updatedEvent)
                }
                _saveSuccess.value = true
            } catch (e: Exception) {
                _saveError.value = "Error al guardar el evento: ${e.message}"
            }
        }
    }

    fun onNavigationHandled() {
        _saveSuccess.value = false
        _saveError.value = null
    }
}