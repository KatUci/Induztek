package cl.duoc.induztek.ui.screen

import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File

// Pantalla 7: cámara para fotografiar la placa del equipo (CameraX)
@Composable
fun CamaraScreen(
    equipoCodigo: String,
    onFotoTomada: (String) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    var tomando by remember { mutableStateOf(false) }
    var flash by remember { mutableStateOf(false) }
    var camaraTrasera by remember { mutableStateOf(true) }

    // Conecta la cámara con la vista previa (se vuelve a conectar si se cambia de cámara)
    LaunchedEffect(camaraTrasera) {
        val provider = ProcessCameraProvider.awaitInstance(context)
        val preview = Preview.Builder().build()
        preview.surfaceProvider = previewView.surfaceProvider
        val camara = if (camaraTrasera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
        provider.unbindAll()
        provider.bindToLifecycle(lifecycleOwner, camara, preview, imageCapture)
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        // Barra de arriba: volver, título y flash
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Column(Modifier.weight(1f)) {
                Text("Foto de placa", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Text(equipoCodigo, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = {
                flash = !flash
                imageCapture.flashMode = if (flash) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
            }) {
                Icon(if (flash) Icons.Filled.FlashOn else Icons.Filled.FlashOff, contentDescription = "Flash", tint = Color.White)
            }
        }

        // Guía con esquinas para encuadrar la placa
        Box(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.82f)
                .aspectRatio(1.3f)
                .drawBehind {
                    val largo = 28.dp.toPx()
                    val grosor = 3.dp.toPx()
                    val w = size.width
                    val h = size.height
                    val c = Color.White
                    drawLine(c, Offset(0f, 0f), Offset(largo, 0f), grosor)
                    drawLine(c, Offset(0f, 0f), Offset(0f, largo), grosor)
                    drawLine(c, Offset(w, 0f), Offset(w - largo, 0f), grosor)
                    drawLine(c, Offset(w, 0f), Offset(w, largo), grosor)
                    drawLine(c, Offset(0f, h), Offset(largo, h), grosor)
                    drawLine(c, Offset(0f, h), Offset(0f, h - largo), grosor)
                    drawLine(c, Offset(w, h), Offset(w - largo, h), grosor)
                    drawLine(c, Offset(w, h), Offset(w, h - largo), grosor)
                }
        )

        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding().padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Encuadra la placa del equipo", color = Color.White)
            Spacer(Modifier.height(20.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.size(48.dp))

                // Botón para tomar la foto
                Box(
                    Modifier
                        .size(72.dp)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(if (tomando) Color.Gray else Color.White)
                        .clickable(enabled = !tomando) {
                            tomando = true
                            val carpeta = File(context.filesDir, "fotos").apply { mkdirs() }
                            val archivo = File(carpeta, "placa_${System.currentTimeMillis()}.jpg")
                            val opciones = ImageCapture.OutputFileOptions.Builder(archivo).build()
                            imageCapture.takePicture(
                                opciones,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        onFotoTomada(archivo.absolutePath)
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        tomando = false
                                        Toast.makeText(context, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                )

                // Cambiar entre cámara trasera y frontal
                IconButton(onClick = { camaraTrasera = !camaraTrasera }, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Filled.Cameraswitch, contentDescription = "Cambiar cámara", tint = Color.White)
                }
            }
        }
    }
}
