package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.*
import org.iesalandalus.pi_musicaincrescendo.domain.model.FilterOption
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.*

/**
 * ViewModel para la pantalla de repertorio.
 */
class RepertoireViewModel(
    private val getRepertoireUseCase: GetRepertoireUseCase = GetRepertoireUseCase(
        RepertoireRepositoryImpl()
    ),
    private val deleteRepertoireUseCase: DeleteRepertoireUseCase = DeleteRepertoireUseCase(
        RepertoireRepositoryImpl()
    ),
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val userUseCases: UserUseCases = UserUseCases(UserRepositoryImpl()),
    private val addNotificationUseCase: AddNotificationUseCase = AddNotificationUseCase(
        NotificationRepositoryImpl()
    )
) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _isIconToggled = MutableStateFlow(false)
    val isIconToggled: StateFlow<Boolean> = _isIconToggled

    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog

    private val _selectedFilterOption = MutableStateFlow(FilterOption.TITULO)
    val selectedFilterOption: StateFlow<FilterOption> = _selectedFilterOption

    private val _allWorks = MutableStateFlow<List<Repertoire>>(emptyList())

    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> = _isDirector.asStateFlow()

    // Estado para diálogo de borrado
    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog

    private val _workToDeleteId = MutableStateFlow<String?>(null)
    private var workToDelete: Repertoire? = null

    val repertoireList: StateFlow<List<Repertoire>> =
        combine(
            _allWorks,
            searchText,
            _isIconToggled,
            _selectedFilterOption
        ) { works, text, isToggled, filter ->
            // 1. Ordenar
            val sortedWorks = when (filter) {
                FilterOption.TITULO -> if (isToggled) works.sortedByDescending {
                    it.title.lowercase()
                } else works.sortedBy { it.title.lowercase() }

                FilterOption.COMPOSITOR -> if (isToggled) works.sortedByDescending {
                    it.composer.lowercase()
                } else works.sortedBy { it.composer.lowercase() }

                FilterOption.FECHA_PUBLICACION -> if (isToggled) works.sortedBy {
                    it.dateSaved
                } else works.sortedByDescending { it.dateSaved }
            }

            // 2. Filtrar
            if (text.isBlank()) {
                sortedWorks
            } else {
                sortedWorks.filter {
                    it.title.contains(text, ignoreCase = true) || it.composer.contains(
                        text,
                        ignoreCase = true
                    )
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            try {
                getRepertoireUseCase().collect { works ->
                    _allWorks.value = works
                }
            } catch (_: Exception) {
                // Para manejar errores en un futuro
            }
        }
        loadUserRole()
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            authRepository.currentUserId()?.let { uid ->
                try {
                    val userProfile = userUseCases.getUserProfile(uid)
                    _isDirector.value = userProfile.isDirector
                } catch (_: Exception) {
                    // Para manejar errores en un futuro
                }
            }
        }
    }

    fun onSearchTextChange(newText: String) {
        _searchText.value = newText
    }

    fun onToggleIcon() {
        _isIconToggled.value = !_isIconToggled.value
    }

    fun onFilterIconClick() {
        _showFilterDialog.value = true
    }

    fun onFilterOptionSelected(option: FilterOption) {
        _selectedFilterOption.value = option
        _showFilterDialog.value = false
    }

    fun onDeleteRequest(work: Repertoire) {
        _workToDeleteId.value = work.id
        workToDelete = work
        _showDeleteDialog.value = true
    }

    fun onConfirmDelete() {
        _workToDeleteId.value?.let { id ->
            viewModelScope.launch {
                try {
                    deleteRepertoireUseCase(id)
                    workToDelete?.let { work ->
                        addNotificationUseCase("Se ha eliminado la obra \"${work.title}\" del repertorio")
                    }
                } catch (_: Exception) {
                    // Para manejar errores en un futuro
                } finally {
                    _showDeleteDialog.value = false
                    _workToDeleteId.value = null
                    workToDelete = null
                }
            }
        }
    }

    fun onDismissDeleteDialog() {
        _showDeleteDialog.value = false
        _workToDeleteId.value = null
    }
}