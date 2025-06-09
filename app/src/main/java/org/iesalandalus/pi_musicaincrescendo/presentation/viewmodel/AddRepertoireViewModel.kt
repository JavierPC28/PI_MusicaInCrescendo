package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.iesalandalus.pi_musicaincrescendo.data.repository.NotificationRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.data.repository.RepertoireRepositoryImpl
import org.iesalandalus.pi_musicaincrescendo.domain.usecase.*

/**
 * ViewModel para la pantalla de añadir repertorio.
 */
class AddRepertoireViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val repertoireRepository = RepertoireRepositoryImpl()
    private val addRepertoireUseCase: AddRepertoireUseCase =
        AddRepertoireUseCase(repertoireRepository)
    private val getRepertoireByIdUseCase: GetRepertoireByIdUseCase =
        GetRepertoireByIdUseCase(repertoireRepository)
    private val updateRepertoireUseCase: UpdateRepertoireUseCase =
        UpdateRepertoireUseCase(repertoireRepository)
    private val checkRepertoireExistsUseCase: CheckRepertoireExistsUseCase =
        CheckRepertoireExistsUseCase(repertoireRepository)
    private val addNotificationUseCase: AddNotificationUseCase =
        AddNotificationUseCase(NotificationRepositoryImpl())

    private val workId: String? = savedStateHandle["workId"]

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

    // Almacena los instrumentos que ya tenían un fichero al editar
    private val _existingInstruments = MutableStateFlow<Set<String>>(emptySet())
    val existingInstruments: StateFlow<Set<String>> = _existingInstruments

    private val _isFilesValid = MutableStateFlow(true)
    val isFilesValid: StateFlow<Boolean> = _isFilesValid

    private val _saveSuccess = MutableStateFlow<String?>(null)
    val saveSuccess: StateFlow<String?> = _saveSuccess

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError

    private val _shouldNavigateBack = MutableStateFlow(false)
    val shouldNavigateBack: StateFlow<Boolean> = _shouldNavigateBack

    init {
        workId?.let {
            loadWorkForEdit(it)
        }
    }

    private fun loadWorkForEdit(id: String) {
        viewModelScope.launch {
            try {
                val work = getRepertoireByIdUseCase(id)
                if (work != null) {
                    _title.value = work.title
                    _composer.value = work.composer
                    _videoUrl.value = work.videoUrl ?: ""
                    _existingInstruments.value = work.instrumentFiles.keys
                } else {
                    _saveError.value = "No se pudo encontrar la obra para editar."
                }
            } catch (e: Exception) {
                _saveError.value = "Error al cargar la obra: ${e.message}"
            }
        }
    }

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
        _instrumentFiles.value = _instrumentFiles.value.toMutableMap().apply {
            put(instrument, uri)
        }
        _isFilesValid.value = true // Se valida en el guardado
    }

    private fun validateFields(): Boolean {
        val tituloOk = _title.value.isNotBlank()
        val compositorOk = _composer.value.isNotBlank()
        // Para edición, puede que no se suban nuevos ficheros pero sí haya existentes
        val filesOk = _instrumentFiles.value.isNotEmpty() || _existingInstruments.value.isNotEmpty()

        _isTitleValid.value = tituloOk
        _isComposerValid.value = compositorOk
        _isFilesValid.value = filesOk

        return tituloOk && compositorOk && filesOk
    }

    fun onSave() {
        if (!validateFields()) return

        viewModelScope.launch {
            try {
                val workTitle = _title.value.trim()
                if (workId == null) {
                    // --- CREAR NUEVA OBRA ---
                    val exists = checkRepertoireExistsUseCase(workTitle, _composer.value.trim())
                    if (exists) {
                        _saveError.value = "Ya existe una obra con el mismo título y compositor."
                        return@launch
                    }

                    val dateSaved = System.currentTimeMillis()
                    addRepertoireUseCase(
                        title = workTitle,
                        composer = _composer.value.trim(),
                        videoUrl = _videoUrl.value.trim().ifEmpty { null },
                        instrumentFiles = _instrumentFiles.value,
                        dateSaved = dateSaved
                    )
                    addNotificationUseCase("Se ha añadido la obra \"$workTitle\" al repertorio")
                    _saveSuccess.value = "Repertorio guardado correctamente"
                } else {
                    if (_instrumentFiles.value.isEmpty() && _existingInstruments.value.isNotEmpty()) {
                        _saveError.value =
                            "Para editar, debe seleccionar al menos un fichero nuevo."
                        return@launch
                    }

                    updateRepertoireUseCase(
                        workId = workId,
                        title = workTitle,
                        composer = _composer.value.trim(),
                        videoUrl = _videoUrl.value.trim().ifEmpty { null },
                        instrumentFiles = _instrumentFiles.value
                    )
                    addNotificationUseCase("Se ha actualizado la obra \"$workTitle\" en el repertorio")
                    _saveSuccess.value = "Repertorio actualizado correctamente"
                }

                _shouldNavigateBack.value = true
            } catch (e: Exception) {
                _saveError.value = e.message
            }
        }
    }

    fun onNavigationHandled() {
        _shouldNavigateBack.value = false
        _saveSuccess.value = null
        _saveError.value = null
    }
}