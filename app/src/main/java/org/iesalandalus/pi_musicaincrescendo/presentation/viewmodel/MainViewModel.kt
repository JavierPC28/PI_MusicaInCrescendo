package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.data.repository.UserRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.DeleteUserAccountUseCase

/**
 * ViewModel principal que gestiona la lógica de la actividad principal,
 * como el cierre de sesión y la eliminación de la cuenta.
 */
class MainViewModel(
    private val authRepo: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    // Caso de uso para eliminar la cuenta del usuario.
    private val deleteUserAccountUseCase = DeleteUserAccountUseCase(
        authRepository = authRepo,
        userRepository = UserRepositoryImpl()
    )

    // Controla la visibilidad del primer diálogo de confirmación para eliminar cuenta.
    private val _showDeleteDialog1 = MutableStateFlow(false)
    val showDeleteDialog1 = _showDeleteDialog1.asStateFlow()

    // Controla la visibilidad del segundo diálogo de confirmación (final).
    private val _showDeleteDialog2 = MutableStateFlow(false)
    val showDeleteDialog2 = _showDeleteDialog2.asStateFlow()

    // Mensaje de error en caso de fallo al eliminar la cuenta.
    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError = _deleteError.asStateFlow()

    /**
     * Cierra la sesión del usuario actual.
     */
    fun logout() {
        authRepo.logout()
    }

    /**
     * Inicia el proceso de eliminación de cuenta mostrando el primer diálogo.
     */
    fun onDeleteAccountRequest() {
        _showDeleteDialog1.value = true
    }

    /**
     * Confirma el primer paso de eliminación y muestra el segundo diálogo.
     */
    fun onConfirmDelete1() {
        _showDeleteDialog1.value = false
        _showDeleteDialog2.value = true
    }

    /**
     * Confirma el segundo paso y ejecuta la eliminación definitiva de la cuenta.
     */
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

    /**
     * Cierra todos los diálogos de eliminación y resetea el estado de error.
     */
    fun onDismissDeleteDialogs() {
        _showDeleteDialog1.value = false
        _showDeleteDialog2.value = false
        _deleteError.value = null
    }
}