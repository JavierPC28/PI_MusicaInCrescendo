package org.iesalandalus.pi_musicaincrescendo.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.iesalandalus.pi_musicaincrescendo.common.utils.Constants
import org.iesalandalus.pi_musicaincrescendo.domain.model.Event
import org.iesalandalus.pi_musicaincrescendo.domain.repository.EventRepository

/**
 * Implementación de [EventRepository] que utiliza Firebase Realtime Database.
 * @param auth Instancia de FirebaseAuth para la autenticación.
 * @param database Instancia de FirebaseDatabase para el acceso a datos.
 */
class EventRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : EventRepository {

    private companion object {
        const val ERROR_USER_NOT_AUTHENTICATED = "Usuario no autenticado"
    }

    /**
     * Añade un nuevo evento a la base de datos.
     */
    override suspend fun addEvent(
        title: String,
        description: String?,
        type: String,
        date: String,
        startTime: String,
        endTime: String,
        location: String,
        coordinates: String?,
        repertoire: Map<String, String>
    ) {
        auth.currentUser?.uid ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)
        val eventRef = database.reference.child("events").child(Constants.GROUP_ID).push()

        val eventData = mutableMapOf<String, Any>(
            "title" to title,
            "type" to type,
            "date" to date,
            "startTime" to startTime,
            "endTime" to endTime,
            "location" to location,
            "repertoireIds" to repertoire
        )

        description?.let { eventData["description"] = it }
        coordinates?.let { eventData["coordinates"] = it }

        eventRef.setValue(eventData).await()
    }

    /**
     * Obtiene una lista de eventos en tiempo real.
     * @return Un Flow que emite la lista de eventos cada vez que hay cambios.
     */
    override fun getEventsRealTime(): Flow<List<Event>> = callbackFlow {
        auth.currentUser?.uid ?: run {
            close(Exception(ERROR_USER_NOT_AUTHENTICATED))
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
                cancel(
                    message = "Firebase listener cancelled at events",
                    cause = error.toException()
                )
            }
        }
        eventsRef.addValueEventListener(listener)

        awaitClose { eventsRef.removeEventListener(listener) }
    }

    /**
     * Obtiene un evento específico por su ID.
     * @param eventId El ID del evento a obtener.
     * @return El objeto [Event] o null si no se encuentra.
     */
    override suspend fun getEventById(eventId: String): Event? {
        auth.currentUser?.uid ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)
        val snapshot = database.reference
            .child("events")
            .child(Constants.GROUP_ID)
            .child(eventId)
            .get()
            .await()
        return snapshot.getValue(Event::class.java)?.copy(id = snapshot.key ?: "")
    }

    /**
     * Actualiza los datos de un evento existente.
     * @param event El evento con los datos actualizados.
     */
    override suspend fun updateEvent(event: Event) {
        auth.currentUser?.uid ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)
        val eventRef = database.reference
            .child("events")
            .child(Constants.GROUP_ID)
            .child(event.id)

        val eventData = mapOf(
            "title" to event.title,
            "description" to event.description,
            "type" to event.type,
            "date" to event.date,
            "startTime" to event.startTime,
            "endTime" to event.endTime,
            "location" to event.location,
            "coordinates" to event.coordinates,
            "repertoireIds" to event.repertoireIds,
            "asistencias" to event.asistencias
        )

        eventRef.updateChildren(eventData).await()
    }

    /**
     * Elimina un evento de la base de datos.
     * @param eventId El ID del evento a eliminar.
     */
    override suspend fun deleteEvent(eventId: String) {
        auth.currentUser?.uid ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)
        database.reference
            .child("events")
            .child(Constants.GROUP_ID)
            .child(eventId)
            .removeValue()
            .await()
    }

    /**
     * Actualiza el estado de asistencia de un usuario a un evento.
     * @param eventId El ID del evento.
     * @param userId El ID del usuario.
     * @param status El nuevo estado de asistencia.
     */
    override suspend fun updateAttendance(eventId: String, userId: String, status: String) {
        auth.currentUser?.uid ?: throw Exception(ERROR_USER_NOT_AUTHENTICATED)
        database.reference
            .child("events")
            .child(Constants.GROUP_ID)
            .child(eventId)
            .child("asistencias")
            .child(userId)
            .setValue(status)
            .await()
    }
}