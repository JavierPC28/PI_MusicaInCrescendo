package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import com.google.firebase.auth.AuthResult
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepository

/**
 * Caso de uso para registrar un nuevo usuario con correo y contraseña.
 * @param repo El repositorio de autenticación.
 */
class RegisterUseCase(private val repo: AuthRepository) {
    /**
     * Ejecuta el registro.
     * @param email El correo del nuevo usuario.
     * @param password La contraseña para el nuevo usuario.
     * @return El resultado del registro de Firebase.
     */
    suspend operator fun invoke(email: String, password: String): AuthResult {
        return repo.register(email, password)
    }
}