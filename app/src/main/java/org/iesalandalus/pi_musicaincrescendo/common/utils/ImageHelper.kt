package org.iesalandalus.pi_musicaincrescendo.common.utils

import org.iesalandalus.pi_musicaincrescendo.R

/**
 * Objeto de utilidad para obtener imágenes de recursos según la lógica de negocio.
 */
object ImageHelper {
    /**
     * Devuelve el ID del recurso de imagen de perfil según el género y si es director.
     * @param gender Género del usuario.
     * @param isDirector Indica si el usuario es director.
     * @return ID del recurso drawable.
     */
    fun getProfileImage(gender: String, isDirector: Boolean): Int {
        return when {
            gender == "Mujer" && isDirector -> R.drawable.perfil_directora
            gender == "Mujer" -> R.drawable.perfil_alumna
            gender == "Hombre" && isDirector -> R.drawable.perfil_director
            gender == "Hombre" -> R.drawable.perfil_alumno
            else -> R.drawable.perfil_neutro
        }
    }

    /**
     * Devuelve el ID del recurso de imagen para un instrumento específico.
     * @param instrument Nombre del instrumento.
     * @return ID del recurso drawable.
     */
    fun getInstrumentDrawable(instrument: String): Int = when (instrument) {
        Constants.DIRECCION_MUSICAL -> R.drawable.batuta
        "FLAUTÍN" -> R.drawable.flautin
        "FLAUTA" -> R.drawable.flauta
        "OBOE" -> R.drawable.oboe
        "CORNO INGLÉS" -> R.drawable.clarinete
        "FAGOT" -> R.drawable.fagot
        "CONTRAFAGOT" -> R.drawable.contrafagot
        "REQUINTO", "CLARINETE" -> R.drawable.clarinete
        "CLARINETE BAJO" -> R.drawable.clarinete_bajo
        "SAXO SOPRANO", "SAXO ALTO", "SAXO TENOR", "SAXO BARÍTONO" -> R.drawable.saxofon
        "TROMPA" -> R.drawable.trompa
        "FLISCORNO", "TROMPETA" -> R.drawable.trompeta
        "TROMBÓN", "TROMBÓN BAJO" -> R.drawable.trombon
        "BOMBARDINO", "TUBA" -> R.drawable.tuba
        "VIOLONCHELO" -> R.drawable.violonchelo
        "CONTRABAJO" -> R.drawable.contrabajo
        "CAJA", "PERCUSIÓN" -> R.drawable.caja
        "BOMBO" -> R.drawable.bombo
        "PLATOS" -> R.drawable.platos
        "TIMBALES" -> R.drawable.timbales
        "LÁMINAS" -> R.drawable.laminas
        "BATERÍA" -> R.drawable.bateria
        else -> R.drawable.instrumento_generico
    }
}