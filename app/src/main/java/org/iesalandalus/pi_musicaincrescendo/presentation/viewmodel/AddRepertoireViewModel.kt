package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la pantalla de añadir repertorio.
 */
class AddRepertoireViewModel : ViewModel() {
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _composer = MutableStateFlow("")
    val composer: StateFlow<String> = _composer

    private val _videoUrl = MutableStateFlow("")
    val videoUrl: StateFlow<String> = _videoUrl

    private val _isTitleValid = MutableStateFlow(true)
    val isTitleValid: StateFlow<Boolean> = _isTitleValid

    private val _isComposerValid = MutableStateFlow(true)
    val isComposerValid: StateFlow<Boolean> = _isComposerValid

    fun onTitleChange(new: String) {
        _title.value = new
        _isTitleValid.value = new.isNotBlank()
    }

    fun onComposerChange(new: String) {
        _composer.value = new
        _isComposerValid.value = new.isNotBlank()
    }

    fun onVideoUrlChange(new: String) {
        _videoUrl.value = new
    }

    /**
     * Validación global de campos antes de guardar.
     */
    fun validateFields(): Boolean {
        val tituloOk = _title.value.isNotBlank()
        val compositorOk = _composer.value.isNotBlank()
        _isTitleValid.value = tituloOk
        _isComposerValid.value = compositorOk
        return tituloOk && compositorOk
    }
}