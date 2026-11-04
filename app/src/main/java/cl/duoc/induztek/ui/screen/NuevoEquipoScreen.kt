package cl.duoc.induztek.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.induztek.ui.EquipoViewModel
import cl.duoc.induztek.ui.TIPOS_EQUIPO
import cl.duoc.induztek.util.TipoVibracion
import cl.duoc.induztek.util.vibrar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoEquipoScreen(
    vm: EquipoViewModel,
    onVolver: () -> Unit,
    onGuardado: () -> Unit
) {
    val form by vm.form.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Vibra cuando faltan campos obligatorios
    LaunchedEffect(form.tieneErrores) { if (form.tieneErrores) vibrar(context, TipoVibracion.ERROR) }

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
                title = { Text(if (form.id == null) "Nuevo equipo" else "Editar equipo") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            CampoTexto(
                etiqueta = "Código del equipo *",
                valor = form.codigo,
                onCambio = vm::onCodigoChange,
                error = form.errorCodigo,
                icono = Icons.Outlined.QrCode
            )
            Spacer(Modifier.height(4.dp))
            CampoSeleccion(
                etiqueta = "Tipo de equipo *",
                valor = form.tipo,
                opciones = TIPOS_EQUIPO,
                onSeleccion = vm::onTipoChange,
                modifier = Modifier.fillMaxWidth(),
                error = form.errorTipo
            )
            Spacer(Modifier.height(4.dp))
            CampoTexto(
                etiqueta = "Cliente *",
                valor = form.cliente,
                onCambio = vm::onClienteChange,
                error = form.errorCliente,
                icono = Icons.Outlined.Business
            )
            Spacer(Modifier.height(4.dp))
            CampoTexto(
                etiqueta = "Instalación / ubicación *",
                valor = form.instalacion,
                onCambio = vm::onInstalacionChange,
                error = form.errorInstalacion,
                icono = Icons.Outlined.LocationOn
            )
            Spacer(Modifier.height(4.dp))
            CampoTexto(
                etiqueta = "Marca y modelo",
                valor = form.marcaModelo,
                onCambio = vm::onMarcaModeloChange,
                error = null,
                icono = Icons.Outlined.Info,
                placeholder = "Opcional"
            )
            Spacer(Modifier.height(4.dp))
            CampoTexto(
                etiqueta = "N° de serie",
                valor = form.numeroSerie,
                onCambio = vm::onNumeroSerieChange,
                error = null,
                icono = Icons.Outlined.Tag,
                placeholder = "Opcional"
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    vm.guardar {
                        vibrar(context, TipoVibracion.EXITO)
                        onGuardado()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(if (form.id == null) "Guardar equipo" else "Actualizar equipo")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
