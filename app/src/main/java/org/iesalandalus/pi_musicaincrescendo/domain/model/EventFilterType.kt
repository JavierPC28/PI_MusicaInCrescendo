package org.iesalandalus.pi_musicaincrescendo.domain.model

/**
 * Enum que representa los tipos de filtro para la lista de eventos.
 * @param displayName Nombre del filtro para mostrar en la UI.
 */
enum class EventFilterType(val displayName: String) {
    TODOS("Todos"),
    CONCIERTO("Conciertos"),
    ENSAYO("Ensayos")
}