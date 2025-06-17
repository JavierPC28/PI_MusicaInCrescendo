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
import java.util.Date
import java.util.Locale

/**
 * Gestiona el estado y la lógica de la pantalla de eventos.
 */
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

    // Almacena todos los eventos obtenidos de la base de datos.
    private val _allEvents = MutableStateFlow<List<Event>>(emptyList())
    private var eventsJob: Job? = null

    // Indica si se están cargando los eventos.
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Mensaje de error, si ocurre alguno.
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Indica si el usuario actual es un director.
    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> = _isDirector.asStateFlow()

    // Filtro activo actualmente para la lista de eventos.
    private val _activeFilter = MutableStateFlow(EventFilterType.TODOS)
    val activeFilter: StateFlow<EventFilterType> = _activeFilter.asStateFlow()

    // Controla la visibilidad del diálogo de confirmación de borrado.
    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog

    // ID y título del evento que se va a eliminar.
    private val _eventToDeleteId = MutableStateFlow<String?>(null)
    private var eventToDeleteTitle: String? = null

    // ID del usuario actualmente autenticado.
    val currentUserId: String? = authRepository.currentUserId()

    /**
     * Convierte la fecha y hora de finalización de un evento a un objeto Date.
     * @param event El evento a procesar.
     * @return El objeto Date correspondiente o null si hay un error de formato.
     */
    private fun parseEventDateTime(event: Event): Date? {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            format.parse("${event.date} ${event.endTime}")
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Expone la lista de eventos filtrada y ordenada.
     * Los eventos futuros se ordenan de más cercano a más lejano.
     * Los eventos pasados se ordenan de más reciente a más antiguo.
     */
    val filteredEvents: StateFlow<List<Event>> = combine(
        _allEvents,
        _activeFilter
    ) { events, filter ->
        // Aplica el filtro seleccionado (Todos, Concierto, Ensayo).
        val filtered = when (filter) {
            EventFilterType.TODOS -> events
            EventFilterType.CONCIERTO -> events.filter { it.type == "Concierto" }
            EventFilterType.ENSAYO -> events.filter { it.type == "Ensayo" }
        }

        val now = Date()
        // Separa los eventos en pasados y futuros.
        val (pastEvents, futureEvents) = filtered.partition {
            val eventDate = parseEventDateTime(it)
            eventDate != null && eventDate.before(now)
        }

        // Ordena ambas listas y las une.
        val sortedFuture = futureEvents.sortedBy { parseEventDateTime(it)?.time ?: 0 }
        val sortedPast = pastEvents.sortedByDescending { parseEventDateTime(it)?.time ?: 0 }

        sortedFuture + sortedPast

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        loadEvents()
        loadUserRole()
    }

    /**
     * Carga el perfil del usuario para determinar si es director.
     */
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

    /**
     * Inicia la recolección de eventos en tiempo real desde el repositorio.
     */
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

    /**
     * Establece el filtro de eventos a aplicar.
     * @param filterType El tipo de filtro a usar.
     */
    fun setFilter(filterType: EventFilterType) {
        _activeFilter.value = filterType
    }

    /**
     * Prepara el estado para mostrar el diálogo de confirmación de borrado.
     * @param eventId El ID del evento a eliminar.
     * @param eventTitle El título del evento, para usar en notificaciones.
     */
    fun onDeleteRequest(eventId: String, eventTitle: String) {
        _eventToDeleteId.value = eventId
        eventToDeleteTitle = eventTitle
        _showDeleteDialog.value = true
    }

    /**
     * Confirma y ejecuta la eliminación del evento seleccionado.
     */
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
                    // Limpia el estado del diálogo y del evento a eliminar.
                    _showDeleteDialog.value = false
                    _eventToDeleteId.value = null
                    eventToDeleteTitle = null
                }
            }
        }
    }

    /**
     * Cierra el diálogo de confirmación de borrado sin realizar ninguna acción.
     */
    fun onDismissDeleteDialog() {
        _showDeleteDialog.value = false
        _eventToDeleteId.value = null
        eventToDeleteTitle = null
    }

    /**
     * Actualiza el estado de asistencia del usuario actual para un evento.
     * @param eventId El ID del evento.
     * @param status El nuevo estado de asistencia ("IRÉ" o "NO IRÉ").
     */
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

    /**
     * Cancela la recolección de datos en tiempo real para evitar fugas de memoria.
     */
    fun cancelarRecoleccion() {
        eventsJob?.cancel()
        eventsJob = null
    }
}