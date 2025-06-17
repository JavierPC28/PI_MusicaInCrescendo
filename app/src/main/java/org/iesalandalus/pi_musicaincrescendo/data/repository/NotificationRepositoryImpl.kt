package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants
import org.iesalandalus.pi_musicaincrescendo.domain.model.Notification
import org.iesalandalus.pi_musicaincrescendo.domain.repository.NotificationRepository
import java.util.UUID

/**
 * Implementación del repositorio para gestionar las notificaciones en Firebase.
 * @param database Instancia de FirebaseDatabase.
 */
class NotificationRepositoryImpl(
    database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : NotificationRepository {

    // Referencia a la colección de notificaciones en la base de datos.
    private val notificationRef =
        database.reference.child("notifications").child(Constants.GROUP_ID)

    /**
     * Añade una nueva notificación a la base de datos.
     * @param text El contenido de la notificación.
     * @param timestamp La marca de tiempo de cuándo se creó la notificación.
     */
    override suspend fun addNotification(text: String, timestamp: Long) {
        val notificationId = notificationRef.push().key ?: UUID.randomUUID().toString()
        val notification = Notification(id = notificationId, text = text, timestamp = timestamp)
        notificationRef.child(notificationId).setValue(notification).await()
    }

    /**
     * Obtiene un Flow con la lista de notificaciones en tiempo real.
     * Las notificaciones se ordenan por fecha de forma descendente.
     * @return Un Flow que emite la lista de notificaciones.
     */
    override fun getNotificationsRealTime(): Flow<List<Notification>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notificationList = snapshot.children.mapNotNull {
                    it.getValue(Notification::class.java)?.copy(id = it.key ?: "")
                }.sortedByDescending { it.timestamp }
                trySend(notificationList)
            }

            override fun onCancelled(error: DatabaseError) {
                cancel(
                    message = "El listener de Firebase para notificaciones fue cancelado.",
                    cause = error.toException()
                )
            }
        }
        notificationRef.addValueEventListener(listener)
        // Cierra el listener cuando el Flow es cancelado.
        awaitClose { notificationRef.removeEventListener(listener) }
    }

    /**
     * Elimina todas las notificaciones del grupo actual.
     */
    override suspend fun deleteAllNotifications() {
        notificationRef.removeValue().await()
    }
}