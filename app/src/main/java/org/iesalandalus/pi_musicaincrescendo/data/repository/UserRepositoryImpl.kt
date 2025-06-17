package org.iesalandalus.pi_musicaincrescendo.data.repository

import android.net.Uri
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants
import org.iesalandalus.pi_musicaincrescendo.domain.model.User
import org.iesalandalus.pi_musicaincrescendo.domain.model.UserProfile

/**
 * Implementación de UserRepository para gestionar datos de perfiles de usuario en Firebase.
 * @param database Instancia de FirebaseDatabase.
 * @param storage Instancia de FirebaseStorage.
 */
class UserRepositoryImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) : UserRepository {

    /**
     * Crea un perfil de usuario en la base de datos con los datos proporcionados.
     * @param uid ID único del usuario.
     * @param displayName Nombre a mostrar.
     * @param gender Género del usuario.
     * @param isDirector `true` si el usuario es director.
     * @param instruments Lista de instrumentos que toca.
     * @param photoUrl URL de la foto de perfil (opcional).
     */
    override suspend fun createUserProfile(
        uid: String,
        displayName: String,
        gender: String,
        isDirector: Boolean,
        instruments: List<String>,
        photoUrl: String?
    ) {
        val userRef = database.getReference("users").child(uid)

        // Asegura que un director siempre tenga "DIRECCIÓN MUSICAL" y limita el número de instrumentos.
        val sanitizedInstruments =
            if (isDirector && !instruments.contains(Constants.DIRECCION_MUSICAL)) {
                listOf(Constants.DIRECCION_MUSICAL) + instruments
            } else {
                instruments
            }.take(Constants.MAX_INSTRUMENTS)

        // Construye el mapa de datos del perfil.
        val profileData = mapOf(
            "displayName" to displayName,
            "gender" to gender,
            "isDirector" to isDirector,
            "instruments" to sanitizedInstruments,
            "photoUrl" to photoUrl
        )
        userRef.setValue(profileData).await()
    }

    /**
     * Actualiza la lista de instrumentos para un usuario específico.
     * @param uid ID del usuario.
     * @param instruments Nueva lista de instrumentos.
     */
    override suspend fun updateInstruments(uid: String, instruments: List<String>) {
        database.getReference("users")
            .child(uid)
            .child("instruments")
            .setValue(instruments)
            .await()
    }

    /**
     * Actualiza el nombre de visualización de un usuario.
     * @param uid ID del usuario.
     * @param displayName Nuevo nombre a mostrar.
     */
    override suspend fun updateDisplayName(uid: String, displayName: String) {
        database.getReference("users")
            .child(uid)
            .child("displayName")
            .setValue(displayName)
            .await()
    }

    /**
     * Actualiza la URL de la foto de perfil de un usuario.
     * @param uid ID del usuario.
     * @param photoUrl Nueva URL de la foto.
     */
    override suspend fun updatePhotoUrl(uid: String, photoUrl: String) {
        database.getReference("users")
            .child(uid)
            .child("photoUrl")
            .setValue(photoUrl)
            .await()
    }

    /**
     * Sube una imagen de perfil a Firebase Storage.
     * @param uid ID del usuario, usado para nombrar el archivo.
     * @param imageUri URI de la imagen a subir.
     * @return La URL de descarga de la imagen subida.
     */
    override suspend fun uploadProfileImage(uid: String, imageUri: Uri): String {
        val storageRef = storage.reference.child("profile_images/$uid.jpg")
        storageRef.putFile(imageUri).await()
        return storageRef.downloadUrl.await().toString()
    }

    /**
     * Obtiene el perfil completo de un usuario por su ID.
     * @param uid ID del usuario.
     * @return El objeto UserProfile.
     */
    override suspend fun getUserProfile(uid: String): UserProfile {
        val snapshot = database.getReference("users").child(uid).get().await()
        return parseUserProfile(snapshot)
    }

    /**
     * Obtiene el número total de usuarios registrados.
     * @return El número de usuarios.
     */
    override suspend fun getUserCount(): Int {
        val snapshot = database.getReference("users").get().await()
        return snapshot.childrenCount.toInt()
    }

    /**
     * Obtiene una lista de todos los perfiles de usuario.
     * @return Lista de objetos User.
     */
    override suspend fun getAllUserProfiles(): List<User> {
        val snapshot = database.getReference("users").get().await()
        val result = mutableListOf<User>()
        for (child in snapshot.children) {
            val uid = child.key ?: continue
            val profile = parseUserProfile(child)
            result.add(User(uid = uid, profile = profile))
        }
        return result
    }

    /**
     * Obtiene el número de usuarios en tiempo real mediante un Flow.
     * @return Un Flow que emite el recuento de usuarios cada vez que cambia.
     */
    override fun getUserCountRealTime(): Flow<Int> = callbackFlow {
        val usersRef = database.getReference("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount.toInt())
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(
                    message = "El listener de Firebase para el contador de usuarios fue cancelado.",
                    cause = error.toException()
                )
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    /**
     * Obtiene una lista de todos los usuarios en tiempo real mediante un Flow.
     * @return Un Flow que emite la lista completa de usuarios cada vez que hay cambios.
     */
    override fun getUsersRealTime(): Flow<List<User>> = callbackFlow {
        val usersRef = database.getReference("users")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (child in snapshot.children) {
                    val uid = child.key ?: continue
                    val profile = parseUserProfile(child)
                    users.add(User(uid = uid, profile = profile))
                }
                trySend(users)
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(
                    message = "El listener de Firebase para usuarios fue cancelado.",
                    cause = error.toException()
                )
            }
        }
        usersRef.addValueEventListener(listener)
        awaitClose { usersRef.removeEventListener(listener) }
    }

    /**
     * Elimina el perfil de un usuario de la base de datos.
     * @param uid ID del usuario a eliminar.
     */
    override suspend fun deleteUserProfile(uid: String) {
        database.getReference("users").child(uid).removeValue().await()
    }

    /**
     * Comprueba si un usuario con un UID específico ya existe en la base de datos.
     * @param uid ID del usuario a comprobar.
     * @return `true` si el usuario existe, `false` en caso contrario.
     */
    override suspend fun userExists(uid: String): Boolean {
        val snapshot = database.getReference("users").child(uid).get().await()
        return snapshot.exists()
    }

    /**
     * Parsea un DataSnapshot para convertirlo en un objeto UserProfile.
     * @param snapshot El snapshot de Firebase a parsear.
     * @return Un objeto UserProfile con los datos extraídos.
     */
    private fun parseUserProfile(snapshot: DataSnapshot): UserProfile {
        return UserProfile(
            displayName = snapshot.child("displayName").getValue(String::class.java) ?: "",
            gender = snapshot.child("gender").getValue(String::class.java) ?: "",
            isDirector = snapshot.child("isDirector").getValue(Boolean::class.java) == true,
            instruments = snapshot.child("instruments").children.mapNotNull { it.getValue(String::class.java) },
            photoUrl = snapshot.child("photoUrl").getValue(String::class.java)
        )
    }
}