package cl.duoc.induztek.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

// Tipos de vibración: una corta al guardar bien, dos cortas cuando hay errores
enum class TipoVibracion { EXITO, ERROR }

// Hace vibrar el teléfono como respuesta a lo que pasó en el formulario
fun vibrar(context: Context, tipo: TipoVibracion) {
    val vibrador: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    if (vibrador == null || !vibrador.hasVibrator()) return

    val efecto = when (tipo) {
        TipoVibracion.EXITO -> VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE)
        TipoVibracion.ERROR -> VibrationEffect.createWaveform(longArrayOf(0, 70, 90, 70), -1)
    }
    vibrador.vibrate(efecto)
}
