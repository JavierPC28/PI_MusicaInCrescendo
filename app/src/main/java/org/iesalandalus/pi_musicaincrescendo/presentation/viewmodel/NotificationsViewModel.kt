package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.*
import org.iesalandalus.pi_musicaincrescendo.domain.model.Notification
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.*
import java.util.Calendar

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

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> = _isDirector.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()


    init {
        loadUserRole()
        loadNotifications()
    }

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

    private fun loadNotifications() {
        viewModelScope.launch {
            getNotificationsUseCase().collect { notificationList ->
                _notifications.value = notificationList
            }
        }
    }

    fun onDeleteRequest() {
        _showDeleteDialog.value = true
    }

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

    fun onDismissDeleteDialog() {
        _showDeleteDialog.value = false
    }

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

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}