package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.*
import org.iesalandalus.pi_musicaincrescendo.domain.model.FilterOption
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.*

/**
 * Gestiona el estado y la lógica para la pantalla que muestra la lista de obras del repertorio.
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
    // Estado para el texto de búsqueda.
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    // Estado para el icono de orden (ascendente/descendente).
    private val _isIconToggled = MutableStateFlow(false)
    val isIconToggled: StateFlow<Boolean> = _isIconToggled

    // Controla la visibilidad del diálogo de filtro.
    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog

    // Opción de filtro seleccionada actualmente (título, compositor, fecha).
    private val _selectedFilterOption = MutableStateFlow(FilterOption.TITULO)
    val selectedFilterOption: StateFlow<FilterOption> = _selectedFilterOption

    // Lista completa de obras obtenidas del repositorio.
    private val _allWorks = MutableStateFlow<List<Repertoire>>(emptyList())
    private var repertoireJob: Job? = null

    // Indica si el usuario actual es un director.
    private val _isDirector = MutableStateFlow(false)
    val isDirector: StateFlow<Boolean> = _isDirector.asStateFlow()

    // Controla la visibilidad del diálogo de confirmación de borrado.
    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog

    // ID y datos de la obra que se va a eliminar.
    private val _workToDeleteId = MutableStateFlow<String?>(null)
    private var workToDelete: Repertoire? = null

    /**
     * Combina varios flujos de estado para producir la lista de repertorio filtrada y ordenada.
     */
    val repertoireList: StateFlow<List<Repertoire>> =
        combine(
            _allWorks,
            searchText,
            _isIconToggled,
            _selectedFilterOption
        ) { works, text, isToggled, filter ->
            // 1. Ordena la lista según el filtro y el orden seleccionados.
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

            // 2. Filtra la lista ordenada por el texto de búsqueda.
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
        // Carga inicial de datos.
        loadRepertoire()
        loadUserRole()
    }

    /**
     * Inicia la recolección de la lista de repertorio en tiempo real.
     */
    private fun loadRepertoire() {
        repertoireJob?.cancel()
        repertoireJob = viewModelScope.launch {
            try {
                getRepertoireUseCase().collect { works ->
                    _allWorks.value = works
                }
            } catch (_: Exception) {
                // Manejo de errores futuro.
            }
        }
    }

    /**
     * Carga el perfil del usuario para determinar si es director.
     */
    private fun loadUserRole() {
        viewModelScope.launch {
            authRepository.currentUserId()?.let { uid ->
                try {
                    val userProfile = userUseCases.getUserProfile(uid)
                    _isDirector.value = userProfile.isDirector
                } catch (_: Exception) {
                    // Manejo de errores futuro.
                }
            }
        }
    }

    /**
     * Actualiza el texto de búsqueda.
     */
    fun onSearchTextChange(newText: String) {
        _searchText.value = newText
    }

    /**
     * Cambia el orden (ascendente/descendente).
     */
    fun onToggleIcon() {
        _isIconToggled.value = !_isIconToggled.value
    }

    /**
     * Muestra el diálogo de selección de filtro.
     */
    fun onFilterIconClick() {
        _showFilterDialog.value = true
    }

    /**
     * Establece la opción de filtro seleccionada y cierra el diálogo.
     */
    fun onFilterOptionSelected(option: FilterOption) {
        _selectedFilterOption.value = option
        _showFilterDialog.value = false
    }

    /**
     * Prepara el estado para mostrar el diálogo de confirmación de borrado.
     * @param work La obra a eliminar.
     */
    fun onDeleteRequest(work: Repertoire) {
        _workToDeleteId.value = work.id
        workToDelete = work
        _showDeleteDialog.value = true
    }

    /**
     * Confirma y ejecuta la eliminación de la obra seleccionada.
     */
    fun onConfirmDelete() {
        _workToDeleteId.value?.let { id ->
            viewModelScope.launch {
                try {
                    deleteRepertoireUseCase(id)
                    workToDelete?.let { work ->
                        addNotificationUseCase("Se ha eliminado la obra \"${work.title}\" del repertorio")
                    }
                } catch (_: Exception) {
                    // Manejo de errores futuro.
                } finally {
                    // Limpia el estado del diálogo y la obra a eliminar.
                    _showDeleteDialog.value = false
                    _workToDeleteId.value = null
                    workToDelete = null
                }
            }
        }
    }

    /**
     * Cierra el diálogo de confirmación de borrado sin realizar ninguna acción.
     */
    fun onDismissDeleteDialog() {
        _showDeleteDialog.value = false
        _workToDeleteId.value = null
    }

    /**
     * Cancela la recolección de datos para liberar recursos.
     */
    fun cancelarRecoleccion() {
        repertoireJob?.cancel()
        repertoireJob = null
    }
}