package org.iesalandalus.pi_musicaincrescendo.common.utils

/**
 * Objeto que almacena constantes utilizadas en toda la aplicación.
 */
object Constants {
    // ID del grupo por defecto para la base de datos.
    const val GROUP_ID = "default_group"

    // Cadena para el rol de director musical.
    const val DIRECCION_MUSICAL = "DIRECCIÓN MUSICAL"

    // Límite de instrumentos para un músico.
    const val MAX_INSTRUMENTS = 3

    // Límite de instrumentos para un director (sin contar la dirección).
    const val MAX_INSTRUMENTS_DIRECTOR = 2

    // Lista de todos los instrumentos disponibles en la aplicación.
    val instrumentosList = listOf(
        DIRECCION_MUSICAL, "FLAUTÍN", "FLAUTA", "OBOE", "CORNO INGLÉS",
        "FAGOT", "CONTRAFAGOT", "REQUINTO", "CLARINETE", "CLARINETE BAJO",
        "SAXO SOPRANO", "SAXO ALTO", "SAXO TENOR", "SAXO BARÍTONO",
        "TROMPA", "FLISCORNO", "TROMPETA", "TROMBÓN", "TROMBÓN BAJO",
        "BOMBARDINO", "TUBA", "VIOLONCHELO", "CONTRABAJO", "CAJA",
        "PERCUSIÓN", "BOMBO", "PLATOS", "TIMBALES", "LÁMINAS", "BATERÍA"
    )
}