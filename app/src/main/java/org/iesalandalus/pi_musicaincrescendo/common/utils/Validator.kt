package org.iesalandalus.pi_musicaincrescendo.common.utils

import android.util.Patterns

/**
 * Objeto de utilidad para validaciones de campos de entrada.
 */
object Validator {
    /**
     * Valida si una cadena tiene formato de correo electrónico válido.
     * @param email El correo a validar.
     * @return `true` si es válido, `false` en caso contrario.
     */
    fun isEmailValid(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    /**
     * Valida si una contraseña cumple con los criterios de seguridad definidos.
     * Mínimo 8 caracteres, empieza por letra, y contiene al menos un dígito y un carácter especial.
     * @param password La contraseña a validar.
     * @return `true` si es válida, `false` en caso contrario.
     */
    fun isPasswordValid(password: String): Boolean {
        val regex = Regex("^[A-Za-z](?=.*[0-9])(?=.*[^A-Za-z0-9]).{7,}$")
        return regex.matches(password)
    }
}