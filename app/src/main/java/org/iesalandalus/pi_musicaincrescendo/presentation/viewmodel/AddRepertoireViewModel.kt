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
 * Gestiona el estado y la lógica para añadir o editar una obra del repertorio.
 */
class AddRepertoireViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Casos de uso para interactuar con los datos del repertorio y notificaciones.
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
    private val checkRepertoireExistsForUpdateUseCase: CheckRepertoireExistsForUpdateUseCase =
        CheckRepertoireExistsForUpdateUseCase(repertoireRepository)

    // ID de la obra, si se está editando.
    private val workId: String? = savedStateHandle["workId"]

    // Estado para el título de la obra.
    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title

    // Estado para el compositor de la obra.
    private val _composer = MutableStateFlow("")
    val composer: StateFlow<String> = _composer

    // Estado para la URL del vídeo de la obra.
    private val _videoUrl = MutableStateFlow("")
    val videoUrl: StateFlow<String> = _videoUrl

    // Validez del campo título.
    private val _isTitleValid = MutableStateFlow(true)
    val isTitleValid: StateFlow<Boolean> = _isTitleValid

    // Validez del campo compositor.
    private val _isComposerValid = MutableStateFlow(true)
    val isComposerValid: StateFlow<Boolean> = _isComposerValid

    // Mapa de archivos PDF de instrumentos recién seleccionados.
    private val _instrumentFiles = MutableStateFlow<Map<String, Uri>>(emptyMap())
    val instrumentFiles: StateFlow<Map<String, Uri>> = _instrumentFiles

    // Conjunto de instrumentos que ya tenían un archivo PDF (en modo edición).
    private val _existingInstruments = MutableStateFlow<Set<String>>(emptySet())
    val existingInstruments: StateFlow<Set<String>> = _existingInstruments

    // Validez de los archivos (al menos uno debe ser seleccionado).
    private val _isFilesValid = MutableStateFlow(true)
    val isFilesValid: StateFlow<Boolean> = _isFilesValid

    // Mensaje de éxito tras guardar.
    private val _saveSuccess = MutableStateFlow<String?>(null)
    val saveSuccess: StateFlow<String?> = _saveSuccess

    // Mensaje de error si falla el guardado.
    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError

    // Señal para navegar hacia atrás tras un guardado exitoso.
    private val _shouldNavigateBack = MutableStateFlow(false)
    val shouldNavigateBack: StateFlow<Boolean> = _shouldNavigateBack

    // Estado de carga para operaciones asíncronas.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Si se proporciona un workId, carga los datos de la obra para editar.
        workId?.let {
            loadWorkForEdit(it)
        }
    }

    /**
     * Carga los datos de una obra existente para su edición.
     * @param id El identificador único de la obra.
     */
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

    /**
     * Actualiza el estado del título y su validez.
     * @param new El nuevo valor del título.
     */
    fun onTitleChange(new: String) {
        _title.value = new
        _isTitleValid.value = new.isNotBlank()
    }

    /**
     * Actualiza el estado del compositor y su validez.
     * @param new El nuevo valor del compositor.
     */
    fun onComposerChange(new: String) {
        _composer.value = new
        _isComposerValid.value = new.isNotBlank()
    }

    /**
     * Actualiza el estado de la URL del vídeo.
     * @param new La nueva URL del vídeo.
     */
    fun onVideoUrlChange(new: String) {
        _videoUrl.value = new
    }

    /**
     * Añade o actualiza el archivo PDF para un instrumento.
     * @param instrument El nombre del instrumento.
     * @param uri La URI del archivo PDF seleccionado.
     */
    fun onFileSelected(instrument: String, uri: Uri) {
        _instrumentFiles.value = _instrumentFiles.value.toMutableMap().apply {
            put(instrument, uri)
        }
        _isFilesValid.value = true
    }

    /**
     * Valida los campos del formulario antes de guardar.
     * @return `true` si todos los campos requeridos son válidos, `false` en caso contrario.
     */
    private fun validateFields(): Boolean {
        val tituloOk = _title.value.isNotBlank()
        val compositorOk = _composer.value.isNotBlank()
        val filesOk = _instrumentFiles.value.isNotEmpty() || _existingInstruments.value.isNotEmpty()

        _isTitleValid.value = tituloOk
        _isComposerValid.value = compositorOk
        _isFilesValid.value = filesOk

        return tituloOk && compositorOk && filesOk
    }

    /**
     * Procesa el guardado de la obra, ya sea creando una nueva o actualizando una existente.
     */
    fun onSave() {
        if (!validateFields()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val workTitle = _title.value.trim()
                val workComposer = _composer.value.trim()

                // Si no hay workId, es una obra nueva.
                if (workId == null) {
                    val exists = checkRepertoireExistsUseCase(workTitle, workComposer)
                    if (exists) {
                        _saveError.value = "Ya existe una obra con el mismo título y compositor."
                        return@launch
                    }

                    val dateSaved = System.currentTimeMillis()
                    addRepertoireUseCase(
                        title = workTitle,
                        composer = workComposer,
                        videoUrl = _videoUrl.value.trim().ifEmpty { null },
                        instrumentFiles = _instrumentFiles.value,
                        dateSaved = dateSaved
                    )
                    addNotificationUseCase("Se ha añadido la obra \"$workTitle\" al repertorio")
                    _saveSuccess.value = "Repertorio guardado correctamente"
                } else {
                    // Si hay workId, es una actualización.
                    val exists =
                        checkRepertoireExistsForUpdateUseCase(workId, workTitle, workComposer)
                    if (exists) {
                        _saveError.value = "Ya existe otra obra con el mismo título y compositor."
                        return@launch
                    }

                    updateRepertoireUseCase(
                        workId = workId,
                        title = workTitle,
                        composer = workComposer,
                        videoUrl = _videoUrl.value.trim().ifEmpty { null },
                        instrumentFiles = _instrumentFiles.value
                    )
                    addNotificationUseCase("Se ha actualizado la obra \"$workTitle\" en el repertorio")
                    _saveSuccess.value = "Repertorio actualizado correctamente"
                }

                _shouldNavigateBack.value = true
            } catch (e: Exception) {
                _saveError.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Resetea los estados de navegación y mensajes después de que la UI los haya consumido.
     */
    fun onNavigationHandled() {
        _shouldNavigateBack.value = false
        _saveSuccess.value = null
        _saveError.value = null
    }
}