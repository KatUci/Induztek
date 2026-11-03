package cl.duoc.induztek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import cl.duoc.induztek.ui.EquipoViewModel
import cl.duoc.induztek.ui.EquipoViewModelFactory
import cl.duoc.induztek.ui.LoginViewModel
import cl.duoc.induztek.ui.PruebaViewModel
import cl.duoc.induztek.ui.PruebaViewModelFactory
import cl.duoc.induztek.ui.navigation.AppNavigation
import cl.duoc.induztek.ui.theme.InduztekProtocolosTheme


class MainActivity : ComponentActivity() {
    private val loginVm: LoginViewModel by viewModels()
    private val equipoVm: EquipoViewModel by viewModels { EquipoViewModelFactory(application) }
    private val pruebaVm: PruebaViewModel by viewModels { PruebaViewModelFactory(application) }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Compact, Medium o Expanded según el ancho de la pantalla
            val windowSizeClass = calculateWindowSizeClass(this)
            InduztekProtocolosTheme {
                AppNavigation(
                    anchoPantalla = windowSizeClass.widthSizeClass,
                    loginVm = loginVm,
                    equipoVm = equipoVm,
                    pruebaVm = pruebaVm
                )
            }
        }
    }
}
