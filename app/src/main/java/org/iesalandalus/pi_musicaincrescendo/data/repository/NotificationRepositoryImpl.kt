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

class NotificationRepositoryImpl(
    database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : NotificationRepository {

    private val notificationRef =
        database.reference.child("notifications").child(Constants.GROUP_ID)

    override suspend fun addNotification(text: String, timestamp: Long) {
        val notificationId = notificationRef.push().key ?: UUID.randomUUID().toString()
        val notification = Notification(id = notificationId, text = text, timestamp = timestamp)
        notificationRef.child(notificationId).setValue(notification).await()
    }

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
                    message = "Firebase listener cancelled at notifications",
                    cause = error.toException()
                )
            }
        }
        notificationRef.addValueEventListener(listener)
        awaitClose { notificationRef.removeEventListener(listener) }
    }

    override suspend fun deleteAllNotifications() {
        notificationRef.removeValue().await()
    }
}