package org.iesalandalus.pi_musicaincrescendo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepositoryImpl
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel encargado de obtener datos del usuario actual
 */
class ProfileViewModel(
    authRepo: AuthRepositoryImpl = AuthRepositoryImpl()
) : ViewModel() {

    // Correo del usuario actual
    private val email: String = authRepo.currentUserEmail() ?: ""

    /**
     * Nombre de usuario: parte izquierda del correo con primera letra mayúscula
     */
    val displayName: String by lazy {
        email
            .substringBefore("@")
            .lowercase(Locale.getDefault())
            .replaceFirstChar { it.uppercaseChar().toString() }
    }

    // Fecha de registro en la base de datos
    private val registrationDate: Date? = authRepo.currentUserRegistrationDate()

    /**
     * Fecha formateada en español: ej. "5 de abril de 2023"
     */
    val registrationDateFormatted: String by lazy {
        registrationDate?.let {
            val localeES = Locale("es", "ES")
            val sdf = SimpleDateFormat("d 'de' MMMM 'de' yyyy", localeES)
            sdf.format(it)
        } ?: ""
    }
}