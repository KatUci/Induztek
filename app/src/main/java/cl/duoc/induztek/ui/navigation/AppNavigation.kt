package cl.duoc.induztek.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duoc.induztek.ui.EquipoViewModel
import cl.duoc.induztek.ui.LoginViewModel
import cl.duoc.induztek.ui.PruebaViewModel
import cl.duoc.induztek.ui.screen.*

// Nombres de las pantallas (rutas)
object Rutas {
    const val LOGIN = "login"
    const val EQUIPOS = "equipos"
    const val NUEVO_EQUIPO = "nuevo_equipo"
    const val FICHA = "ficha"
    const val NUEVA_PRUEBA = "nueva_prueba"
    const val CAMARA = "camara"
    const val PROTOCOLO = "protocolo"
    const val PROTOCOLOS = "protocolos"
    const val SINCRONIZAR = "sincronizar"
}

@Composable
fun AppNavigation(
    anchoPantalla: WindowWidthSizeClass,
    loginVm: LoginViewModel,
    equipoVm: EquipoViewModel,
    pruebaVm: PruebaViewModel
) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    val tecnico by loginVm.tecnico.collectAsStateWithLifecycle()
    val equipo by equipoVm.seleccionado.collectAsStateWithLifecycle()

    // Tipos de pantalla: Compact = celular, Medium = tablet vertical, Expanded = tablet horizontal
    val compacta = anchoPantalla == WindowWidthSizeClass.Compact
    val expandida = anchoPantalla == WindowWidthSizeClass.Expanded
    val mostrarMenu = rutaActual == Rutas.EQUIPOS || rutaActual == Rutas.PROTOCOLOS || rutaActual == Rutas.SINCRONIZAR

    // Cambia de pestaña en el menú (Equipos / Protocolos)
    fun irA(ruta: String) {
        nav.navigate(ruta) {
            popUpTo(Rutas.EQUIPOS) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val pantallas: @Composable () -> Unit = {
        NavHost(navController = nav, startDestination = Rutas.LOGIN) {

            composable(Rutas.LOGIN) {
                LoginScreen(
                    vm = loginVm,
                    onIngresado = {
                        nav.navigate(Rutas.EQUIPOS) { popUpTo(Rutas.LOGIN) { inclusive = true } }
                    }
                )
            }

            composable(Rutas.EQUIPOS) {
                val cerrarSesion = {
                    loginVm.cerrarSesion()
                    nav.navigate(Rutas.LOGIN) { popUpTo(0) }
                }
                if (expandida) {
                    // Tablet horizontal: lista y ficha en la misma pantalla
                    Row(Modifier.fillMaxSize()) {
                        EquiposScreen(
                            vm = equipoVm,
                            tecnico = tecnico ?: "",
                            onNuevoEquipo = { nav.navigate(Rutas.NUEVO_EQUIPO) },
                            onAbrirEquipo = { equipoVm.seleccionar(it.id) },
                            onCerrarSesion = cerrarSesion,
                            modifier = Modifier.weight(0.4f)
                        )
                        VerticalDivider()
                        FichaEquipoScreen(
                            equipoVm = equipoVm,
                            pruebaVm = pruebaVm,
                            onVolver = null,
                            onEditar = {
                                equipoVm.editar(it)
                                nav.navigate(Rutas.NUEVO_EQUIPO)
                            },
                            onNuevaPrueba = {
                                pruebaVm.nuevaPrueba(it.id)
                                nav.navigate(Rutas.NUEVA_PRUEBA)
                            },
                            onAbrirProtocolo = {
                                pruebaVm.abrirProtocolo(it.id)
                                nav.navigate(Rutas.PROTOCOLO)
                            },
                            modifier = Modifier.weight(0.6f)
                        )
                    }
                } else {
                    EquiposScreen(
                        vm = equipoVm,
                        tecnico = tecnico ?: "",
                        onNuevoEquipo = { nav.navigate(Rutas.NUEVO_EQUIPO) },
                        onAbrirEquipo = {
                            equipoVm.seleccionar(it.id)
                            nav.navigate(Rutas.FICHA)
                        },
                        onCerrarSesion = cerrarSesion
                    )
                }
            }

            composable(Rutas.NUEVO_EQUIPO) {
                NuevoEquipoScreen(
                    vm = equipoVm,
                    onVolver = { nav.popBackStack() },
                    onGuardado = { nav.popBackStack() }
                )
            }

            composable(Rutas.FICHA) {
                FichaEquipoScreen(
                    equipoVm = equipoVm,
                    pruebaVm = pruebaVm,
                    onVolver = { nav.popBackStack() },
                    onEditar = {
                        equipoVm.editar(it)
                        nav.navigate(Rutas.NUEVO_EQUIPO)
                    },
                    onNuevaPrueba = {
                        pruebaVm.nuevaPrueba(it.id)
                        nav.navigate(Rutas.NUEVA_PRUEBA)
                    },
                    onAbrirProtocolo = {
                        pruebaVm.abrirProtocolo(it.id)
                        nav.navigate(Rutas.PROTOCOLO)
                    }
                )
            }

            composable(Rutas.NUEVA_PRUEBA) {
                NuevaPruebaScreen(
                    vm = pruebaVm,
                    equipoCodigo = equipo?.codigo ?: "",
                    tecnico = tecnico ?: "Técnico",
                    pantallaAncha = !compacta,
                    onVolver = { nav.popBackStack() },
                    onAbrirCamara = { nav.navigate(Rutas.CAMARA) },
                    onGuardado = {
                        nav.navigate(Rutas.PROTOCOLO) { popUpTo(Rutas.NUEVA_PRUEBA) { inclusive = true } }
                    }
                )
            }

            composable(Rutas.CAMARA) {
                CamaraScreen(
                    equipoCodigo = equipo?.codigo ?: "",
                    onFotoTomada = { path ->
                        pruebaVm.onFotoTomada(path)
                        nav.popBackStack()
                    },
                    onVolver = { nav.popBackStack() }
                )
            }

            composable(Rutas.PROTOCOLO) {
                ProtocoloScreen(
                    vm = pruebaVm,
                    onVolver = { nav.popBackStack() }
                )
            }

            composable(Rutas.SINCRONIZAR) {
                SincronizarScreen(vm = pruebaVm)
            }

            composable(Rutas.PROTOCOLOS) {
                ProtocolosScreen(
                    vm = pruebaVm,
                    onAbrirProtocolo = {
                        pruebaVm.abrirProtocolo(it.id)
                        nav.navigate(Rutas.PROTOCOLO)
                    }
                )
            }
        }
    }

    if (compacta) {
        // Celular: menú abajo (NavigationBar)
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                if (mostrarMenu) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = rutaActual == Rutas.EQUIPOS,
                            onClick = { irA(Rutas.EQUIPOS) },
                            icon = { Icon(Icons.Filled.Bolt, contentDescription = null) },
                            label = { Text("Equipos") }
                        )
                        NavigationBarItem(
                            selected = rutaActual == Rutas.PROTOCOLOS,
                            onClick = { irA(Rutas.PROTOCOLOS) },
                            icon = { Icon(Icons.AutoMirrored.Filled.Article, contentDescription = null) },
                            label = { Text("Protocolos") }
                        )
                        NavigationBarItem(
                            selected = rutaActual == Rutas.SINCRONIZAR,
                            onClick = { irA(Rutas.SINCRONIZAR) },
                            icon = { Icon(Icons.Filled.Sync, contentDescription = null) },
                            label = { Text("Sincronizar") }
                        )
                    }
                }
            }
        ) { padding ->
            Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)) {
                pantallas()
            }
        }
    } else {
        // Tablet (medium y expanded): menú al costado (NavigationRail)
        Row(Modifier.fillMaxSize()) {
            if (mostrarMenu) {
                NavigationRail {
                    NavigationRailItem(
                        selected = rutaActual == Rutas.EQUIPOS,
                        onClick = { irA(Rutas.EQUIPOS) },
                        icon = { Icon(Icons.Filled.Bolt, contentDescription = null) },
                        label = { Text("Equipos") }
                    )
                    NavigationRailItem(
                        selected = rutaActual == Rutas.PROTOCOLOS,
                        onClick = { irA(Rutas.PROTOCOLOS) },
                        icon = { Icon(Icons.AutoMirrored.Filled.Article, contentDescription = null) },
                        label = { Text("Protocolos") }
                    )
                    NavigationRailItem(
                        selected = rutaActual == Rutas.SINCRONIZAR,
                        onClick = { irA(Rutas.SINCRONIZAR) },
                        icon = { Icon(Icons.Filled.Sync, contentDescription = null) },
                        label = { Text("Sincronizar") }
                    )
                }
            }
            Box(Modifier.weight(1f)) {
                pantallas()
            }
        }
    }
}
