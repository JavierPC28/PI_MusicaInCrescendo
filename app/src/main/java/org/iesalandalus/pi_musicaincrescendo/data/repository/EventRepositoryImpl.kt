package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

class EventRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : EventRepository {
    override suspend fun addEvent(
        title: String,
        type: String,
        date: String,
        startTime: String,
        endTime: String,
        location: String,
        repertoire: Map<String, String>
    ) {
        auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
        val eventRef = database.reference.child("events").child(Constants.GROUP_ID).push()

        val eventData = mapOf(
            "title" to title,
            "type" to type,
            "date" to date,
            "startTime" to startTime,
            "endTime" to endTime,
            "location" to location,
            "repertoireIds" to repertoire
        )

        eventRef.setValue(eventData).await()
    }

    override fun getEventsRealTime(): Flow<List<Event>> = callbackFlow {
        auth.currentUser?.uid ?: run {
            close(Exception("Usuario no autenticado"))
            return@callbackFlow
        }

        val eventsRef = database.reference.child("events").child(Constants.GROUP_ID)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val eventList = snapshot.children.mapNotNull { dataSnapshot ->
                    dataSnapshot.getValue(Event::class.java)?.copy(
                        id = dataSnapshot.key ?: ""
                    )
                }
                trySend(eventList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        eventsRef.addValueEventListener(listener)

        awaitClose { eventsRef.removeEventListener(listener) }
    }

    override suspend fun getEventById(eventId: String): Event? {
        auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
        val snapshot = database.reference
            .child("events")
            .child(Constants.GROUP_ID)
            .child(eventId)
            .get()
            .await()
        return snapshot.getValue(Event::class.java)?.copy(id = snapshot.key ?: "")
    }

    override suspend fun updateEvent(event: Event) {
        auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
        val eventRef = database.reference
            .child("events")
            .child(Constants.GROUP_ID)
            .child(event.id)

        val eventData = mapOf(
            "title" to event.title,
            "type" to event.type,
            "date" to event.date,
            "startTime" to event.startTime,
            "endTime" to event.endTime,
            "location" to event.location,
            "repertoireIds" to event.repertoireIds
        )

        eventRef.updateChildren(eventData).await()
    }

    override suspend fun deleteEvent(eventId: String) {
        auth.currentUser?.uid ?: throw Exception("Usuario no autenticado")
        database.reference
            .child("events")
            .child(Constants.GROUP_ID)
            .child(eventId)
            .removeValue()
            .await()
    }
}