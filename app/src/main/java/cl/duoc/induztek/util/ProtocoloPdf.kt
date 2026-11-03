package cl.duoc.induztek.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import cl.duoc.induztek.ui.ProtocoloDatos
import cl.duoc.induztek.ui.codigoProtocolo
import cl.duoc.induztek.ui.formatoFecha
import cl.duoc.induztek.ui.formatoNumero
import java.io.File

// Genera el PDF del protocolo en el teléfono (sin internet) y permite verlo o compartirlo
object ProtocoloPdf {

    fun crear(context: Context, datos: ProtocoloDatos): File {
        val p = datos.prueba
        val equipo = datos.equipo
        val anterior = datos.anterior
        val codigo = codigoProtocolo(p)

        val documento = PdfDocument()
        val pagina = documento.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create()) // A4
        val canvas = pagina.canvas

        val titulo = Paint().apply { textSize = 18f; isFakeBoldText = true; color = Color.rgb(31, 95, 168) }
        val subtitulo = Paint().apply { textSize = 12f; isFakeBoldText = true }
        val normal = Paint().apply { textSize = 11f }
        val gris = Paint().apply { textSize = 10f; color = Color.GRAY }
        val linea = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }

        var y = 60f
        canvas.drawText("INDUZTEK · Protocolo de prueba", 40f, y, titulo)
        y += 18f
        canvas.drawText(codigo, 40f, y, gris)
        y += 14f
        canvas.drawLine(40f, y, 555f, y, linea)
        y += 24f

        val filas = listOf(
            "Equipo" to "${p.equipoCodigo} · ${equipo?.tipo ?: ""}",
            "Cliente" to (equipo?.cliente ?: "—"),
            "Instalación" to (equipo?.instalacion ?: "—"),
            "Fecha" to formatoFecha(p.fecha),
            "Técnico" to p.tecnico,
            "Tipo de prueba" to p.tipoPrueba
        )
        filas.forEach { (etiqueta, valor) ->
            canvas.drawText(etiqueta, 40f, y, gris)
            canvas.drawText(valor, 140f, y, normal)
            y += 18f
        }

        // Foto de la placa a la derecha
        p.fotoPath?.let { path ->
            cargarMiniatura(path, 4)?.let { foto ->
                canvas.drawBitmap(foto, null, RectF(415f, 92f, 555f, 92f + 140f * foto.height / foto.width), null)
            }
        }

        y += 16f
        canvas.drawText("Mediciones (GΩ)", 40f, y, subtitulo)
        y += 20f
        canvas.drawText("Medición", 40f, y, gris)
        canvas.drawText("Anterior", 220f, y, gris)
        canvas.drawText("Actual", 340f, y, gris)
        y += 6f
        canvas.drawLine(40f, y, 555f, y, linea)
        y += 16f
        listOf(
            Triple("AT–tierra", anterior?.atTierra, p.atTierra),
            Triple("BT–tierra", anterior?.btTierra, p.btTierra),
            Triple("AT–BT", anterior?.atBt, p.atBt)
        ).forEach { (nombre, antes, ahora) ->
            canvas.drawText(nombre, 40f, y, normal)
            canvas.drawText(antes?.let { formatoNumero(it) } ?: "—", 220f, y, normal)
            canvas.drawText(formatoNumero(ahora), 340f, y, normal)
            y += 18f
        }

        y += 16f
        canvas.drawText("Resultado: ${p.resultado}", 40f, y, subtitulo)
        p.observaciones?.let {
            y += 22f
            canvas.drawText("Observaciones", 40f, y, gris)
            y += 16f
            canvas.drawText(it, 40f, y, normal)
        }

        canvas.drawText("Generado en terreno con Induztek Protocolos · Datos ficticios (DSY1105)", 40f, 800f, gris)
        documento.finishPage(pagina)

        val carpeta = File(context.cacheDir, "protocolos").apply { mkdirs() }
        val archivo = File(carpeta, "$codigo.pdf")
        archivo.outputStream().use { documento.writeTo(it) }
        documento.close()
        return archivo
    }

    private fun uri(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    // Abre el menú "Compartir" de Android (correo, WhatsApp, Drive, etc.)
    fun compartir(context: Context, datos: ProtocoloDatos) {
        val archivo = crear(context, datos)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri(context, archivo))
            putExtra(Intent.EXTRA_SUBJECT, "Protocolo ${archivo.nameWithoutExtension}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir protocolo"))
    }

    // Abre el PDF con el visor que tenga el teléfono
    fun abrir(context: Context, datos: ProtocoloDatos) {
        val archivo = crear(context, datos)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri(context, archivo), "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No hay una app para ver PDF. Usa Compartir.", Toast.LENGTH_LONG).show()
        }
    }
}
