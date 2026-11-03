package cl.duoc.induztek.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import java.io.File

// Lee una foto guardada en el teléfono en tamaño pequeño y la gira según cómo se tomó
fun cargarMiniatura(path: String, reduccion: Int = 8): Bitmap? {
    if (!File(path).exists()) return null
    val opciones = BitmapFactory.Options().apply { inSampleSize = reduccion }
    val bitmap = BitmapFactory.decodeFile(path, opciones) ?: return null

    val grados = when (ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
        else -> 0f
    }
    if (grados == 0f) return bitmap
    val matriz = Matrix().apply { postRotate(grados) }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
}
