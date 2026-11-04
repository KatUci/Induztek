package cl.duoc.induztek.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.induztek.ui.*
import cl.duoc.induztek.ui.theme.Ambar
import cl.duoc.induztek.ui.theme.AmbarBorde
import cl.duoc.induztek.ui.theme.Verde
import cl.duoc.induztek.util.TipoVibracion
import cl.duoc.induztek.util.vibrar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaPruebaScreen(
    vm: PruebaViewModel,
    equipoCodigo: String,
    tecnico: String,
    pantallaAncha: Boolean,
    onVolver: () -> Unit,
    onAbrirCamara: () -> Unit,
    onGuardado: (Long) -> Unit
) {
    val form by vm.form.collectAsStateWithLifecycle()
    val anterior by vm.anterior.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Pide el permiso de cámara (pantalla 6). Si lo rechaza, la prueba se guarda sin foto.
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) onAbrirCamara()
        else Toast.makeText(context, "Si rechazas el permiso, la prueba se guarda igual, sin foto.", Toast.LENGTH_LONG).show()
    }
    val tomarFoto = {
        val tienePermiso = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        if (tienePermiso) onAbrirCamara() else pedirPermiso.launch(Manifest.permission.CAMERA)
    }

    // Vibra al guardar la prueba o cuando algún campo tiene error
    val hayErrores = form.errorAtTierra != null || form.errorBtTierra != null ||
        form.errorAtBt != null || form.errorResultado != null
    LaunchedEffect(hayErrores) { if (hayErrores) vibrar(context, TipoVibracion.ERROR) }
    LaunchedEffect(form.estado) { if (form.estado == EstadoGuardado.GUARDADO) vibrar(context, TipoVibracion.EXITO) }

    if (form.error != null) {
        AlertDialog(
            onDismissRequest = { vm.limpiarError() },
            confirmButton = { TextButton(onClick = { vm.limpiarError() }) { Text("OK") } },
            title = { Text("Error") },
            text = { Text(form.error ?: "") }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Nueva prueba")
                        Text(
                            "$equipoCodigo · ${formatoFecha(System.currentTimeMillis())} (automática)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Filled.Close, contentDescription = "Cancelar")
                    }
                }
            )
        },
        bottomBar = {
            // Botón fijo abajo, como en el mockup
            Surface(color = MaterialTheme.colorScheme.background) {
                Box(Modifier.navigationBarsPadding().imePadding().padding(16.dp)) {
                    BotonGuardar(estado = form.estado, onClick = { vm.guardar(tecnico, onGuardado) })
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            CampoSeleccion(
                etiqueta = "Tipo de prueba *",
                valor = form.tipoPrueba,
                opciones = TIPOS_PRUEBA,
                onSeleccion = vm::onTipoPruebaChange,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            // En tablet (medium/expanded) los 3 campos van en una fila; en celular, uno debajo del otro
            if (pantallaAncha) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CampoMedicion("AT–tierra (GΩ) *", form.atTierra, vm::onAtTierraChange, form.errorAtTierra, anterior?.atTierra, Modifier.weight(1f))
                    CampoMedicion("BT–tierra (GΩ) *", form.btTierra, vm::onBtTierraChange, form.errorBtTierra, anterior?.btTierra, Modifier.weight(1f))
                    CampoMedicion("AT–BT (GΩ) *", form.atBt, vm::onAtBtChange, form.errorAtBt, anterior?.atBt, Modifier.weight(1f))
                }
            } else {
                CampoMedicion("AT–tierra (GΩ) *", form.atTierra, vm::onAtTierraChange, form.errorAtTierra, anterior?.atTierra)
                CampoMedicion("BT–tierra (GΩ) *", form.btTierra, vm::onBtTierraChange, form.errorBtTierra, anterior?.btTierra)
                CampoMedicion("AT–BT (GΩ) *", form.atBt, vm::onAtBtChange, form.errorAtBt, anterior?.atBt)
            }

            CampoSeleccion(
                etiqueta = "Resultado *",
                valor = form.resultado,
                opciones = RESULTADOS,
                onSeleccion = vm::onResultadoChange,
                modifier = Modifier.fillMaxWidth(),
                error = form.errorResultado
            )

            Text(
                "Fotos de evidencia",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Foto tomada (aparece con animación)
                AnimatedVisibility(visible = form.fotoPath != null) {
                    Box(Modifier.size(96.dp)) {
                        FotoMiniatura(form.fotoPath ?: "", 96.dp)
                        Box(
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.55f))
                                .clickable { vm.quitarFoto() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Quitar foto", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                // Cuadro "Placa" cuando todavía no hay foto
                if (form.fotoPath == null) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(96.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Icon(Icons.Outlined.Image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Placa", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                // Cuadro "Tomar" con borde de líneas cortadas
                Column(
                    Modifier
                        .size(96.dp)
                        .bordeDiscontinuo(MaterialTheme.colorScheme.primary)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { tomarFoto() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Tomar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = form.observaciones,
                onValueChange = vm::onObservacionesChange,
                label = { Text("Observaciones") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

// Campo de una medición con la comparación contra la prueba anterior
@Composable
private fun CampoMedicion(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    error: String?,
    anterior: Double?,
    modifier: Modifier = Modifier
) {
    val actual = leerNumero(valor)
    val alerta = actual != null && anterior != null && esAlerta(actual, anterior)

    // El borde cambia de color suavemente cuando el valor bajó mucho (animación)
    val colorBorde by animateColorAsState(
        targetValue = if (alerta) AmbarBorde else MaterialTheme.colorScheme.outline,
        label = "colorBorde"
    )

    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        singleLine = true,
        isError = error != null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = colorBorde,
            focusedBorderColor = if (alerta) AmbarBorde else MaterialTheme.colorScheme.primary
        ),
        supportingText = {
            when {
                error != null -> MensajeError(error)
                anterior != null -> Row(verticalAlignment = Alignment.CenterVertically) {
                    // El ícono de alerta aparece con animación
                    AnimatedVisibility(visible = alerta) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = Ambar, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                    }
                    Text(
                        if (actual != null) textoComparacion(actual, anterior) else "Anterior ${formatoNumero(anterior)}",
                        color = if (alerta) Ambar else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth().padding(bottom = 4.dp)
    )
}

// Botón con animación: Guardar → Guardando… → Guardado
@Composable
private fun BotonGuardar(estado: EstadoGuardado, onClick: () -> Unit) {
    Button(
        onClick = { if (estado == EstadoGuardado.LISTO) onClick() },
        colors = if (estado == EstadoGuardado.GUARDADO) ButtonDefaults.buttonColors(containerColor = Verde) else ButtonDefaults.buttonColors(),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        AnimatedContent(targetState = estado, label = "botonGuardar") { e ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (e) {
                    EstadoGuardado.LISTO -> Text("Guardar y generar protocolo")
                    EstadoGuardado.GUARDANDO -> {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Guardando…")
                    }
                    EstadoGuardado.GUARDADO -> {
                        Icon(Icons.Filled.Check, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardado")
                    }
                }
            }
        }
    }
}
