package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.model.User
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UserUseCases

/**
 * Gestiona el estado y la lógica de la pantalla de inicio (Home).
 */
class HomeViewModel(
    private val userUseCases: UserUseCases = UserUseCases(UserRepositoryImpl())
) : ViewModel() {

    // Estado para el número total de usuarios.
    private val _userCount = MutableStateFlow(0)
    val userCount: StateFlow<Int> get() = _userCount

    // Estado para la lista completa de miembros.
    private val _members = MutableStateFlow<List<User>>(emptyList())
    val members: StateFlow<List<User>> get() = _members

    // Tareas de corutina para la recolección de datos en tiempo real.
    private var userCountJob: Job? = null
    private var membersJob: Job? = null

    init {
        // Inicia la carga de datos en tiempo real al crear el ViewModel.
        cargarUserCountRealTime()
        cargarMembersRealTime()
    }

    /**
     * Carga y observa el número total de usuarios en tiempo real.
     */
    private fun cargarUserCountRealTime() {
        userCountJob = viewModelScope.launch {
            try {
                userUseCases.getUserCountRealTime().collectLatest { count ->
                    _userCount.value = count
                }
            } catch (_: Exception) {
                // Manejo de errores futuro.
            }
        }
    }

    /**
     * Carga y observa la lista de todos los miembros del grupo en tiempo real.
     */
    private fun cargarMembersRealTime() {
        membersJob = viewModelScope.launch {
            try {
                userUseCases.getUsersRealTime().collectLatest { users ->
                    _members.value = users
                }
            } catch (_: Exception) {
                // Manejo de errores futuro.
            }
        }
    }

    /**
     * Cancela las corutinas de recolección de datos para liberar recursos.
     * Se debe llamar cuando la vista ya no es visible.
     */
    fun cancelarRecoleccion() {
        userCountJob?.cancel()
        membersJob?.cancel()
        userCountJob = null
        membersJob = null
    }
}