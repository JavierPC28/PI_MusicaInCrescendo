package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la pantalla de añadir obra.
 */
class AddRepertoireViewModel : ViewModel() {
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    private val _composer = MutableStateFlow("")
    val composer: StateFlow<String> = _composer

    private val _videoUrl = MutableStateFlow("")
    val videoUrl: StateFlow<String> = _videoUrl

    fun onTitleChange(new: String) {
        _title.value = new
    }

    fun onComposerChange(new: String) {
        _composer.value = new
    }

    fun onVideoUrlChange(new: String) {
        _videoUrl.value = new
    }

    fun onSave() {
        // Aquí lanzaríamos el caso de uso de guardar la obra
    }
}