package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.DeleteUserAccountUseCase

class MainViewModel(
    private val authRepo: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    private val deleteUserAccountUseCase = DeleteUserAccountUseCase(
        authRepository = authRepo,
        userRepository = UserRepositoryImpl()
    )

    private val _showDeleteDialog1 = MutableStateFlow(false)
    val showDeleteDialog1 = _showDeleteDialog1.asStateFlow()

    private val _showDeleteDialog2 = MutableStateFlow(false)
    val showDeleteDialog2 = _showDeleteDialog2.asStateFlow()

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError = _deleteError.asStateFlow()

    fun logout() {
        authRepo.logout()
    }

    fun onDeleteAccountRequest() {
        _showDeleteDialog1.value = true
    }

    fun onConfirmDelete1() {
        _showDeleteDialog1.value = false
        _showDeleteDialog2.value = true
    }

    fun onConfirmDelete2() {
        viewModelScope.launch {
            try {
                deleteUserAccountUseCase()
                _showDeleteDialog2.value = false
            } catch (e: Exception) {
                _deleteError.value = "Error al eliminar la cuenta: ${e.message}"
            }
        }
    }

    fun onDismissDeleteDialogs() {
        _showDeleteDialog1.value = false
        _showDeleteDialog2.value = false
        _deleteError.value = null
    }
}