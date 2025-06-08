package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.RepertoireRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.model.FilterOption
import org.iesalandalus.pi_musicaincrescendo.domain.model.Repertoire
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.GetRepertoireUseCase

/**
 * ViewModel para la pantalla de repertorio.
 */
class RepertoireViewModel(
    private val getRepertoireUseCase: GetRepertoireUseCase = GetRepertoireUseCase(
        RepertoireRepositoryImpl()
    )
) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _isIconToggled = MutableStateFlow(false)
    val isIconToggled: StateFlow<Boolean> = _isIconToggled

    // Estado para mostrar u ocultar el diálogo de filtro
    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog

    // Opción de filtro seleccionada
    private val _selectedFilterOption = MutableStateFlow(FilterOption.TITULO)
    val selectedFilterOption: StateFlow<FilterOption> = _selectedFilterOption

    private val _allWorks = MutableStateFlow<List<Repertoire>>(emptyList())

    val repertoireList: StateFlow<List<Repertoire>> =
        combine(
            _allWorks,
            searchText,
        ) { works, text ->
            if (text.isBlank()) {
                works
            } else {
                works.filter {
                    it.title.contains(text, ignoreCase = true) || it.composer.contains(
                        text,
                        ignoreCase = true
                    )
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            getRepertoireUseCase().collect { works ->
                _allWorks.value = works
            }
        }
    }

    fun onSearchTextChange(newText: String) {
        _searchText.value = newText
    }

    fun onToggleIcon() {
        _isIconToggled.value = !_isIconToggled.value
    }

    /**
     * Muestra el diálogo de filtro.
     */
    fun onFilterIconClick() {
        _showFilterDialog.value = true
    }

    /**
     * Procesa la opción seleccionada y cierra el diálogo.
     */
    fun onFilterOptionSelected(option: FilterOption) {
        _selectedFilterOption.value = option
        _showFilterDialog.value = false
    }
}