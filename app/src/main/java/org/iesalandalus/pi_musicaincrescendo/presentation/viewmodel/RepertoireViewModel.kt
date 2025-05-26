package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.iesalandalus.pi_musicaincrescendo.domain.model.FilterOption

/**
 * ViewModel para la pantalla de repertorio.
 */
class RepertoireViewModel : ViewModel() {
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