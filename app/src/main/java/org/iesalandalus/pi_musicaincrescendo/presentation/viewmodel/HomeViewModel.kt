package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetUserCountUseCase

class HomeViewModel(
    private val getUserCountUseCase: GetUserCountUseCase = GetUserCountUseCase(UserRepositoryImpl())
) : ViewModel() {

    private val _userCount = MutableStateFlow(0)
    val userCount: StateFlow<Int> get() = _userCount

    init {
        cargarUserCount()
    }

    private fun cargarUserCount() {
        viewModelScope.launch {
            val count = getUserCountUseCase()
            _userCount.value = count
        }
    }
}