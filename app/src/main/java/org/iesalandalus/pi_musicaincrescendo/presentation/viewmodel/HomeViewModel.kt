package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserProfile
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.UserUseCases

class HomeViewModel(
    private val userUseCases: UserUseCases = UserUseCases(UserRepositoryImpl())
) : ViewModel() {

    private val _userCount = MutableStateFlow(0)
    val userCount: StateFlow<Int> get() = _userCount

    private val _members = MutableStateFlow<List<UserProfile>>(emptyList())
    val members: StateFlow<List<UserProfile>> get() = _members

    private var membersJob: Job? = null

    init {
        cargarUserCount()
        cargarMembersRealTime()
    }

    private fun cargarUserCount() {
        viewModelScope.launch {
            _userCount.value = userUseCases.getUserCount()
        }
    }

    private fun cargarMembersRealTime() {
        membersJob = viewModelScope.launch {
            try {
                userUseCases.getUsersRealTime().collectLatest { users ->
                    _members.value = users.map { it.second }
                }
            } catch (_: Exception) {
                // Ignoramos errores
            }
        }
    }

    fun cancelarRecoleccion() {
        membersJob?.cancel()
        membersJob = null
    }
}