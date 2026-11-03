package cl.duoc.induztek.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.duoc.induztek.ui.PruebaViewModel
import cl.duoc.induztek.ui.codigoProtocolo
import cl.duoc.induztek.ui.formatoFecha
import cl.duoc.induztek.util.ProtocoloPdf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocoloScreen(
    vm: PruebaViewModel,
    onVolver: () -> Unit
) {
    val datos by vm.protocolo.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(datos?.let { codigoProtocolo(it.prueba) } ?: "Protocolo")
                        Text(
                            "Guardado en el teléfono",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            // Botones fijos abajo: Ver PDF y Compartir
            datos?.let { d ->
                Surface(color = MaterialTheme.colorScheme.background) {
                    Row(
                        Modifier.navigationBarsPadding().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { ProtocoloPdf.abrir(context, d) },
                            modifier = Modifier.weight(1f).height(52.dp)
                        ) {
                            Icon(Icons.Filled.PictureAsPdf, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Ver PDF")
                        }
                        Button(
                            onClick = { ProtocoloPdf.compartir(context, d) },
                            modifier = Modifier.weight(1.4f).height(52.dp)
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Compartir")
                        }
                    }
                }
            }
        }
    ) { padding ->
        val d = datos
        if (d == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        val p = d.prueba

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("INDUZTEK · Protocolo de prueba", fontWeight = FontWeight.Bold)
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Row {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Dato("Equipo", "${p.equipoCodigo} · ${d.equipo?.tipo ?: ""}")
                            Dato("Cliente", d.equipo?.cliente ?: "—")
                            Dato("Lugar", d.equipo?.instalacion ?: "—")
                            Dato("Fecha", formatoFecha(p.fecha))
                            Dato("Técnico", p.tecnico)
                        }
                        p.fotoPath?.let { FotoMiniatura(it, 88.dp) }
                    }
                    Spacer(Modifier.height(12.dp))
                    TablaComparacion(actual = p, anterior = d.anterior, encabezadoConAnios = false)
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Resultado: ")
                        EstadoChip(p.resultado)
                    }
                    p.observaciones?.let {
                        Spacer(Modifier.height(8.dp))
                        Dato("Observaciones", it)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
