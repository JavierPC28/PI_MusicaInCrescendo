package org.iesalandalus.pi_musicaincrescendo.domain.usecase

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

/**
 * Caso de uso para descargar un archivo PDF desde una URL utilizando el DownloadManager de Android.
 */
class DownloadPdfUseCase {
    /**
     * Inicia la descarga del archivo PDF.
     * @param context El contexto de la aplicación.
     * @param url La URL del archivo PDF a descargar.
     * @param title El título que se mostrará en la notificación de descarga y como nombre de archivo.
     */
    operator fun invoke(context: Context, url: String, title: String) {
        val downloadManager = context.getSystemService(DownloadManager::class.java)
        val request = DownloadManager.Request(url.toUri())
            .setTitle(title)
            .setDescription("Descargando PDF...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$title.pdf")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        downloadManager.enqueue(request)
    }
}