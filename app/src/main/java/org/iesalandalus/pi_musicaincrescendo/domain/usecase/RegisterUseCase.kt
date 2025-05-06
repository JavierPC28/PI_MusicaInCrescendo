package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import com.google.firebase.auth.AuthResult
import org.iesalandalus.pi_musicaincrescendo.data.repository.AuthRepository

class RegisterUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        return repo.register(email, password)
    }
}