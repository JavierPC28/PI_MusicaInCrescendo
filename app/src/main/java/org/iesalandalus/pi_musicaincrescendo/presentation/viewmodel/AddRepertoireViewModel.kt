package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.RepertoireRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.AddRepertoireUseCase

/**
 * ViewModel para la pantalla de añadir repertorio.
 */
class AddRepertoireViewModel(
    private val addRepertoireUseCase: AddRepertoireUseCase = AddRepertoireUseCase(
        RepertoireRepositoryImpl()
    )
) : ViewModel() {

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

    private val _instrumentFiles = MutableStateFlow<Map<String, Uri>>(emptyMap())
    val instrumentFiles: StateFlow<Map<String, Uri>> = _instrumentFiles

    private val _isFilesValid = MutableStateFlow(true)
    val isFilesValid: StateFlow<Boolean> = _isFilesValid

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError

    private val _shouldNavigateBack = MutableStateFlow(false)
    val shouldNavigateBack: StateFlow<Boolean> = _shouldNavigateBack

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

    fun onFileSelected(instrument: String, uri: Uri) {
        // Añadimos o reemplazamos el URI en el mapa
        _instrumentFiles.value = _instrumentFiles.value.toMutableMap().apply {
            put(instrument, uri)
        }
        _isFilesValid.value = _instrumentFiles.value.isNotEmpty()
    }

    private fun validateFields(): Boolean {
        val tituloOk = _title.value.isNotBlank()
        val compositorOk = _composer.value.isNotBlank()
        val filesOk = _instrumentFiles.value.isNotEmpty()

        _isTitleValid.value = tituloOk
        _isComposerValid.value = compositorOk
        _isFilesValid.value = filesOk

        return tituloOk && compositorOk && filesOk
    }

    fun onSave() {
        if (!validateFields()) return

        viewModelScope.launch {
            try {
                val dateSaved = System.currentTimeMillis()
                addRepertoireUseCase(
                    title = _title.value.trim(),
                    composer = _composer.value.trim(),
                    videoUrl = _videoUrl.value.trim().ifEmpty { null },
                    instrumentFiles = _instrumentFiles.value,
                    dateSaved = dateSaved
                )
                _saveSuccess.value = true
                _shouldNavigateBack.value = true
            } catch (e: Exception) {
                _saveError.value = e.message
            }
        }
    }

    fun onNavigationHandled() {
        _shouldNavigateBack.value = false
        _saveSuccess.value = false
        _saveError.value = null
    }
}