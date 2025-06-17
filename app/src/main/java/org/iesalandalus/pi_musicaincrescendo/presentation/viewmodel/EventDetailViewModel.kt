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

// Estado de la asistencia que se considera como confirmada.
private const val ATTENDANCE_STATUS_ATTENDING = "IRÉ"

/**
 * Define el estado de la interfaz de usuario para la pantalla de detalles del evento.
 * @param isLoading Indica si se están cargando los datos.
 * @param event El objeto Evento a mostrar.
 * @param repertoire La lista de obras de repertorio asociadas al evento.
 * @param members La lista de usuarios que asisten al evento.
 * @param error Mensaje de error, si lo hubiera.
 * @param selectedTab Índice de la pestaña actualmente seleccionada (Detalles, Repertorio, Miembros).
 */
data class EventDetailUiState(
    val isLoading: Boolean = true,
    val event: Event? = null,
    val repertoire: List<Repertoire> = emptyList(),
    val members: List<User> = emptyList(),
    val error: String? = null,
    val selectedTab: Int = 0
)

/**
 * Gestiona el estado y la lógica para la pantalla de detalles de un evento.
 */
class EventDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Casos de uso para obtener datos de eventos, repertorio y usuarios.
    private val getEventByIdUseCase = GetEventByIdUseCase(EventRepositoryImpl())
    private val getRepertoireByIdUseCase = GetRepertoireByIdUseCase(RepertoireRepositoryImpl())
    private val userUseCases = UserUseCases(UserRepositoryImpl())

    // Flujo de estado para la UI.
    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    // ID del evento, obtenido de los argumentos de navegación.
    private val eventId: String = checkNotNull(savedStateHandle["eventId"])

    init {
        // Carga inicial de los detalles del evento.
        loadEventDetails()
    }

    /**
     * Cambia la pestaña seleccionada en la interfaz.
     * @param index El índice de la nueva pestaña a seleccionar.
     */
    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    /**
     * Carga todos los detalles necesarios para el evento, incluyendo repertorio y asistentes.
     */
    private fun loadEventDetails() {
        viewModelScope.launch {
            _uiState.value = EventDetailUiState(isLoading = true)
            try {
                // Obtiene el evento principal por su ID.
                val event = getEventByIdUseCase(eventId)
                if (event == null) {
                    _uiState.value =
                        EventDetailUiState(isLoading = false, error = "Evento no encontrado.")
                    return@launch
                }

                // Obtiene las obras del repertorio a partir de sus IDs.
                val repertoireList = event.repertoireIds.keys.mapNotNull { repertoireId ->
                    getRepertoireByIdUseCase(repertoireId)
                }

                // Filtra los IDs de los usuarios que han confirmado asistencia.
                val attendingUserIds =
                    event.asistencias.filter { it.value == ATTENDANCE_STATUS_ATTENDING }.keys

                // Obtiene los perfiles de los usuarios asistentes.
                val memberList = attendingUserIds.mapNotNull { userId ->
                    try {
                        val profile = userUseCases.getUserProfile(userId)
                        User(uid = userId, profile = profile)
                    } catch (_: Exception) {
                        null // Ignora usuarios que no se puedan cargar.
                    }
                }

                // Actualiza el estado de la UI con todos los datos cargados.
                _uiState.value = EventDetailUiState(
                    isLoading = false,
                    event = event,
                    repertoire = repertoireList,
                    members = memberList,
                    selectedTab = 0
                )

            } catch (e: Exception) {
                // Maneja errores durante la carga de datos.
                _uiState.value = EventDetailUiState(
                    isLoading = false,
                    error = "Error al cargar los detalles: ${e.message}"
                )
            }
        }
    }
}