package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.*
import org.iesalandalus.pi_musicaincrescendo.domain.model.Notification
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.*
import java.util.Calendar

/**
 * Gestiona el estado y la lógica de la pantalla de notificaciones.
 */
class NotificationsViewModel(
    private val getNotificationsUseCase: GetNotificationsUseCase = GetNotificationsUseCase(
        NotificationRepositoryImpl()
    ),
    private val deleteAllNotificationsUseCase: DeleteAllNotificationsUseCase = DeleteAllNotificationsUseCase(
        NotificationRepositoryImpl()
    ),
    private val userUseCases: UserUseCases = UserUseCases(UserRepositoryImpl()),
    private val authRepository: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    // Estado para la lista de notificaciones.
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    // Indica si el usuario actual tiene rol de director.
    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> = _isDirector.asStateFlow()

    // Controla la visibilidad del diálogo de confirmación de borrado.
    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    // Mensaje de error, si ocurre alguno.
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Tarea de corutina para la recolección de notificaciones.
    private var notificationsJob: Job? = null


    init {
        // Carga inicial del rol del usuario y las notificaciones.
        loadUserRole()
        loadNotifications()
    }

    /**
     * Carga el perfil del usuario para determinar si tiene permisos de director.
     */
    private fun loadUserRole() {
        viewModelScope.launch {
            authRepository.currentUserId()?.let { uid ->
                try {
                    val userProfile = userUseCases.getUserProfile(uid)
                    _isDirector.value = userProfile.isDirector
                } catch (_: Exception) {
                    _error.value = "Error al cargar el rol del usuario."
                }
            }
        }
    }

    /**
     * Inicia la recolección de notificaciones en tiempo real.
     */
    private fun loadNotifications() {
        notificationsJob?.cancel()
        notificationsJob = viewModelScope.launch {
            getNotificationsUseCase().collect { notificationList ->
                _notifications.value = notificationList
            }
        }
    }

    /**
     * Muestra el diálogo de confirmación para eliminar las notificaciones.
     */
    fun onDeleteRequest() {
        _showDeleteDialog.value = true
    }

    /**
     * Ejecuta la eliminación de todas las notificaciones.
     */
    fun onConfirmDelete() {
        viewModelScope.launch {
            try {
                deleteAllNotificationsUseCase()
            } catch (_: Exception) {
                _error.value = "Error al eliminar las notificaciones."
            } finally {
                _showDeleteDialog.value = false
            }
        }
    }

    /**
     * Cierra el diálogo de confirmación de borrado.
     */
    fun onDismissDeleteDialog() {
        _showDeleteDialog.value = false
    }

    /**
     * Cancela la recolección de datos para liberar recursos.
     */
    fun cancelarRecoleccion() {
        notificationsJob?.cancel()
        notificationsJob = null
    }

    /**
     * Formatea un timestamp a una cadena de texto relativa (hoy, ayer, mañana) o una fecha.
     * @param timestamp El tiempo en milisegundos a formatear.
     * @return La cadena de texto con la fecha formateada.
     */
    fun getFormattedDate(timestamp: Long): String {
        val notificationDate = Calendar.getInstance().apply { timeInMillis = timestamp }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

        return when {
            isSameDay(notificationDate, today) -> "hoy"
            isSameDay(notificationDate, yesterday) -> "ayer"
            isSameDay(notificationDate, tomorrow) -> "mañana"
            else -> java.text.SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault())
                .format(notificationDate.time)
        }
    }

    /**
     * Comprueba si dos calendarios corresponden al mismo día del mismo año.
     * @param cal1 El primer calendario.
     * @param cal2 El segundo calendario.
     * @return `true` si son el mismo día, `false` en caso contrario.
     */
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] &&
                cal1[Calendar.DAY_OF_YEAR] == cal2[Calendar.DAY_OF_YEAR]
    }
}