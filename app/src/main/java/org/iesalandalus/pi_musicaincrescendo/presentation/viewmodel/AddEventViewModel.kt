package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.*
import org.iesalandalus.pi_musicaincrescendo.domain.model.*
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.*

/**
 * ViewModel para la pantalla de añadir o editar eventos.
 * @param addEventUseCase Caso de uso para añadir un evento.
 * @param getRepertoireUseCase Caso de uso para obtener el repertorio.
 * @param getEventByIdUseCase Caso de uso para obtener un evento por ID (para edición).
 * @param updateEventUseCase Caso de uso para actualizar un evento.
 * @param addNotificationUseCase Caso de uso para crear notificaciones.
 */
class AddEventViewModel(
    private val addEventUseCase: AddEventUseCase = AddEventUseCase(EventRepositoryImpl()),
    private val getRepertoireUseCase: GetRepertoireUseCase = GetRepertoireUseCase(
        RepertoireRepositoryImpl()
    ),
    private val getEventByIdUseCase: GetEventByIdUseCase = GetEventByIdUseCase(EventRepositoryImpl()),
    private val updateEventUseCase: UpdateEventUseCase = UpdateEventUseCase(EventRepositoryImpl()),
    private val addNotificationUseCase: AddNotificationUseCase = AddNotificationUseCase(
        NotificationRepositoryImpl()
    )
) : ViewModel() {

    // Almacena el ID del evento si estamos en modo edición.
    private var eventId: String? = null

    // Almacena el estado original del evento antes de la edición.
    private var originalEvent: Event? = null

    // Estados para cada campo del formulario del evento.
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

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

    private val _coordinates = MutableStateFlow("")
    val coordinates: StateFlow<String> = _coordinates.asStateFlow()

    // Lista completa de obras del repertorio para seleccionar.
    private val _allRepertoire = MutableStateFlow<List<Repertoire>>(emptyList())
    val allRepertoire: StateFlow<List<Repertoire>> = _allRepertoire.asStateFlow()

    // Obras seleccionadas para este evento.
    private val _selectedRepertoire = MutableStateFlow<Map<String, String>>(emptyMap())
    val selectedRepertoire: StateFlow<Map<String, String>> = _selectedRepertoire.asStateFlow()

    // Estado para comunicar el éxito o error de la operación de guardado.
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    /**
     * Un [StateFlow] que indica si el formulario es válido.
     * Combina los estados de los campos clave y aplica las reglas de validación.
     */
    val isFormValid: StateFlow<Boolean> = combine(
        _title,
        _eventType,
        _date,
        _startTime,
        _endTime,
        _location,
        _selectedRepertoire,
        _coordinates
    ) { values ->
        val titleValue = values[0] as String
        val type = values[1] as? EventType
        val dateValue = values[2] as String
        val startTimeValue = values[3] as String
        val endTimeValue = values[4] as String
        val locationValue = values[5] as String
        val repertoireValue = values[6] as Map<*, *>
        val coordinatesValue = values[7] as String

        // Expresión regular para validar coordenadas (lat,lon).
        val coordinatesRegex = """^-?\d{1,3}(\.\d+)?,\s*-?\d{1,3}(\.\d+)?$""".toRegex()

        titleValue.isNotBlank() &&
                type != null &&
                dateValue.isNotBlank() &&
                startTimeValue.isNotBlank() &&
                endTimeValue.isNotBlank() &&
                locationValue.isNotBlank() &&
                repertoireValue.isNotEmpty() &&
                (coordinatesValue.isBlank() || coordinatesRegex.matches(coordinatesValue.trim()))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        // Al iniciar, carga la lista de repertorio disponible.
        viewModelScope.launch {
            getRepertoireUseCase().collect {
                _allRepertoire.value = it
            }
        }
    }

    /**
     * Carga los datos de un evento existente para su edición.
     * @param id El ID del evento a cargar. Si es nulo, no hace nada.
     */
    fun loadEventForEditing(id: String?) {
        if (id == null || id == eventId) return
        this.eventId = id
        viewModelScope.launch {
            try {
                val event = getEventByIdUseCase(id)
                if (event != null) {
                    originalEvent = event
                    _title.value = event.title
                    _description.value = event.description ?: ""
                    _eventType.value = EventType.entries.find { it.displayName == event.type }
                    _date.value = event.date
                    _startTime.value = event.startTime
                    _endTime.value = event.endTime
                    _location.value = event.location
                    _coordinates.value = event.coordinates ?: ""
                    _selectedRepertoire.value = event.repertoireIds
                } else {
                    _saveError.value = "No se pudo encontrar el evento para editar."
                }
            } catch (e: Exception) {
                _saveError.value = "Error al cargar el evento: ${e.message}"
            }
        }
    }

    // Funciones para actualizar el estado desde la UI.
    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onDescriptionChange(newDescription: String) {
        if (newDescription.length <= 500) {
            _description.value = newDescription
        }
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

    fun onCoordinatesChange(newCoordinates: String) {
        _coordinates.value = newCoordinates
    }

    /**
     * Añade o quita una obra de la lista de repertorio seleccionado para el evento.
     * @param repertoireItem La obra a añadir o quitar.
     * @param isSelected `true` para añadir, `false` para quitar.
     */
    fun onRepertoireToggle(repertoireItem: Repertoire, isSelected: Boolean) {
        val current = _selectedRepertoire.value.toMutableMap()
        if (isSelected) {
            current[repertoireItem.id] = repertoireItem.title
        } else {
            current.remove(repertoireItem.id)
        }
        _selectedRepertoire.value = current
    }

    /**
     * Guarda el evento. Decide si crear uno nuevo o actualizar uno existente
     * basándose en si `eventId` es nulo.
     */
    fun onSaveEvent() {
        if (!isFormValid.value) return

        viewModelScope.launch {
            try {
                val eventTitle = _title.value.trim()
                if (eventId == null) {
                    // Crea un nuevo evento.
                    val params = AddEventParams(
                        title = eventTitle,
                        description = _description.value.trim().ifEmpty { null },
                        type = _eventType.value!!.displayName,
                        date = _date.value,
                        startTime = _startTime.value,
                        endTime = _endTime.value,
                        location = _location.value.trim(),
                        coordinates = _coordinates.value.trim().ifEmpty { null },
                        repertoire = _selectedRepertoire.value
                    )
                    addEventUseCase(params)
                    addNotificationUseCase("Se ha creado el evento \"$eventTitle\"")
                } else {
                    // Actualiza un evento existente.
                    val updatedEvent = Event(
                        id = eventId!!,
                        title = eventTitle,
                        description = _description.value.trim().ifEmpty { null },
                        type = _eventType.value!!.displayName,
                        date = _date.value,
                        startTime = _startTime.value,
                        endTime = _endTime.value,
                        location = _location.value.trim(),
                        coordinates = _coordinates.value.trim().ifEmpty { null },
                        repertoireIds = _selectedRepertoire.value,
                        asistencias = originalEvent?.asistencias ?: emptyMap()
                    )
                    updateEventUseCase(updatedEvent)
                    addNotificationUseCase("Se ha actualizado el evento \"$eventTitle\"")
                }
                _saveSuccess.value = true
            } catch (e: Exception) {
                _saveError.value = "Error al guardar el evento: ${e.message}"
            }
        }
    }

    /**
     * Resetea los estados de éxito y error, típicamente después de una navegación.
     */
    fun onNavigationHandled() {
        _saveSuccess.value = false
        _saveError.value = null
    }
}