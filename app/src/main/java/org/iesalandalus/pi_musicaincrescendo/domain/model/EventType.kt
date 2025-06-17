package org.iesalandalus.pi_musicaincrescendo.domain.model

/**
 * Enum que define los posibles tipos de un evento.
 * @param displayName Nombre del tipo para mostrar en la UI.
 */
enum class EventType(val displayName: String) {
    CONCIERTO("Concierto"),
    ENSAYO("Ensayo")
}