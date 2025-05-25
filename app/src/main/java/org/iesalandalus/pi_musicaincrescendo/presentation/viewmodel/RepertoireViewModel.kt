package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la pantalla de repertorio.
 */
class RepertoireViewModel : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText

    private val _isIconToggled = MutableStateFlow(false)
    val isIconToggled: StateFlow<Boolean> = _isIconToggled

    fun onSearchTextChange(newText: String) {
        _searchText.value = newText
    }

    fun onToggleIcon() {
        _isIconToggled.value = !_isIconToggled.value
    }
}