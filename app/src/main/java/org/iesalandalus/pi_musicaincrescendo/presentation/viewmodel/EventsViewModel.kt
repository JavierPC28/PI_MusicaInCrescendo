package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.*
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.model.EventFilterType
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventsViewModel(
    private val getEventsUseCase: GetEventsUseCase = GetEventsUseCase(EventRepositoryImpl()),
    private val deleteEventUseCase: DeleteEventUseCase = DeleteEventUseCase(EventRepositoryImpl()),
    private val updateAttendanceUseCase: UpdateAttendanceUseCase = UpdateAttendanceUseCase(
        EventRepositoryImpl()
    ),
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val userUseCases: UserUseCases = UserUseCases(UserRepositoryImpl()),
    private val addNotificationUseCase: AddNotificationUseCase = AddNotificationUseCase(
        NotificationRepositoryImpl()
    )
) : ViewModel() {

    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())
    private var eventsJob: Job? = null

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> = _isDirector.asStateFlow()

    private val _activeFilter = MutableStateFlow(EventFilterType.TODOS)
    val activeFilter: StateFlow<EventFilterType> = _activeFilter.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog
    private val _eventToDeleteId = MutableStateFlow<String?>(null)
    private var eventToDeleteTitle: String? = null

    val currentUserId: String? = authRepository.currentUserId()

    private fun parseEventDateTime(event: Event): Calendar? {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val date = format.parse("${event.date} ${event.endTime}")
            Calendar.getInstance().apply { time = date }
        } catch (_: Exception) {
            null
        }
    }

    val filteredEvents: StateFlow<List<Event>> = combine(
        _allEvents,
        _activeFilter
    ) { events, filter ->
        val filtered = when (filter) {
            EventFilterType.TODOS -> events
            EventFilterType.CONCIERTO -> events.filter { it.type == "Concierto" }
            EventFilterType.ENSAYO -> events.filter { it.type == "Ensayo" }
        }

        val now = Calendar.getInstance()
        val (pastEvents, futureEvents) = filtered.partition {
            val eventDate = parseEventDateTime(it)
            eventDate != null && eventDate.before(now)
        }

        futureEvents.sortedBy { parseEventDateTime(it) } + pastEvents.sortedByDescending {
            parseEventDateTime(
                it
            )
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


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
        eventsJob?.cancel()
        eventsJob = viewModelScope.launch {
            _isLoading.value = true
            try {
                getEventsUseCase().collect {
                    _allEvents.value = it
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar los eventos: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun setFilter(filterType: EventFilterType) {
        _activeFilter.value = filterType
    }

    fun onDeleteRequest(eventId: String, eventTitle: String) {
        _eventToDeleteId.value = eventId
        eventToDeleteTitle = eventTitle
        _showDeleteDialog.value = true
    }

    fun onConfirmDelete() {
        _eventToDeleteId.value?.let { id ->
            viewModelScope.launch {
                try {
                    deleteEventUseCase(id)
                    eventToDeleteTitle?.let { title ->
                        addNotificationUseCase("Se ha cancelado el evento \"$title\"")
                    }
                } catch (e: Exception) {
                    _error.value = "Error al eliminar el evento: ${e.message}"
                } finally {
                    _showDeleteDialog.value = false
                    _eventToDeleteId.value = null
                    eventToDeleteTitle = null
                }
            }
        }
    }

    fun onDismissDeleteDialog() {
        _showDeleteDialog.value = false
        _eventToDeleteId.value = null
        eventToDeleteTitle = null
    }

    fun updateAttendance(eventId: String, status: String) {
        if (currentUserId == null) {
            _error.value = "Usuario no identificado."
            return
        }
        viewModelScope.launch {
            try {
                updateAttendanceUseCase(eventId, currentUserId, status)
            } catch (e: Exception) {
                _error.value = "Error al actualizar asistencia: ${e.message}"
            }
        }
    }

    fun cancelarRecoleccion() {
        eventsJob?.cancel()
        eventsJob = null
    }
}