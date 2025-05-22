package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetAllUserProfilesUseCase
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetUserCountUseCase
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserProfile

class HomeViewModel(
    private val getUserCountUseCase: GetUserCountUseCase = GetUserCountUseCase(UserRepositoryImpl()),
    private val getAllUserProfilesUseCase: GetAllUserProfilesUseCase = GetAllUserProfilesUseCase(
        UserRepositoryImpl()
    )
) : ViewModel() {

    private val _userCount = MutableStateFlow(0)
    val userCount: StateFlow<Int> get() = _userCount

    private val _members = MutableStateFlow<List<UserProfile>>(emptyList())
    val members: StateFlow<List<UserProfile>> get() = _members

    init {
        cargarUserCount()
        cargarMembers()
    }

    private fun cargarUserCount() {
        viewModelScope.launch {
            val count = getUserCountUseCase()
            _userCount.value = count
        }
    }

    private fun cargarMembers() {
        viewModelScope.launch {
            val list = getAllUserProfilesUseCase()
            // Sólo nos importa el perfil, no el UID
            _members.value = list.map { it.second }
        }
    }
}