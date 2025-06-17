package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import com.google.firebase.auth.AuthResult
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepository

/**
 * Caso de uso para iniciar sesión con correo y contraseña.
 * @param repo El repositorio de autenticación.
 */
class LoginUseCase(private val repo: AuthRepository) {
    /**
     * Ejecuta el inicio de sesión.
     * @param email El correo del usuario.
     * @param password La contraseña del usuario.
     * @return El resultado de la autenticación de Firebase.
     */
    suspend operator fun invoke(email: String, password: String): AuthResult {
        return repo.login(email, password)
    }
}