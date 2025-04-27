package org.iesalandalus.pi_musicaincrescendo.common.utils

import android.util.Patterns

/**
 * Utilidad para validaciones de entrada.
 */
object Validator {
    /**
     * Valida el formato de correo electrónico.
     */
    fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /**
     * Valida la contraseña:
     * - Mínimo 8 caracteres
     * - Comienza por letra
     * - Al menos un dígito
     * - Al menos un carácter especial
     */
    fun isPasswordValid(password: String): Boolean {
        val regex = Regex("^[A-Za-z](?=.*[0-9])(?=.*[^A-Za-z0-9]).{7,}$")
        return regex.matches(password)
    }
}