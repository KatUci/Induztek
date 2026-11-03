package cl.duoc.induztek.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.induztek.ui.EquipoViewModel
import cl.duoc.induztek.ui.TIPOS_EQUIPO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoEquipoScreen(
    vm: EquipoViewModel,
    onVolver: () -> Unit,
    onGuardado: () -> Unit
) {
    val form by vm.form.collectAsStateWithLifecycle()

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
            OutlinedTextField(
                value = form.codigo,
                onValueChange = vm::onCodigoChange,
                label = { Text("Código del equipo *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            CampoSeleccion(
                etiqueta = "Tipo de equipo *",
                valor = form.tipo,
                opciones = TIPOS_EQUIPO,
                onSeleccion = vm::onTipoChange,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = form.cliente,
                onValueChange = vm::onClienteChange,
                label = { Text("Cliente *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = form.instalacion,
                onValueChange = vm::onInstalacionChange,
                label = { Text("Instalación / ubicación *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = form.marcaModelo,
                onValueChange = vm::onMarcaModeloChange,
                label = { Text("Marca y modelo") },
                placeholder = { Text("Opcional") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = form.numeroSerie,
                onValueChange = vm::onNumeroSerieChange,
                label = { Text("N° de serie") },
                placeholder = { Text("Opcional") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { vm.guardar(onGuardado) },
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(if (form.id == null) "Guardar equipo" else "Actualizar equipo")
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
