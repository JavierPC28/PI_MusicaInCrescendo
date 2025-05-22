package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    init {
        cargarUserCount()
        cargarMembers()
    }

    private fun cargarUserCount() {
        viewModelScope.launch {
            _userCount.value = userUseCases.getUserCount()
        }
    }

    private fun cargarMembers() {
        viewModelScope.launch {
            _members.value = userUseCases.getAllUserProfiles().map { it.second }
        }
    }
}