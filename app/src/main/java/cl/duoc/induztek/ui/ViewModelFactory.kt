package cl.duoc.induztek.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.induztek.data.AppDatabase
import cl.duoc.induztek.data.EquipoRepository
import cl.duoc.induztek.data.PruebaRepository


class EquipoViewModelFactory(app: Application) : ViewModelProvider.Factory {
    private val repo by lazy {
        val dao = AppDatabase.get(app).equipoDao()
        EquipoRepository(dao)
    }
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        EquipoViewModel(repo) as T
}

class PruebaViewModelFactory(app: Application) : ViewModelProvider.Factory {
    private val repo by lazy {
        val db = AppDatabase.get(app)
        PruebaRepository(db.pruebaDao(), db.equipoDao())
    }
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        PruebaViewModel(repo) as T
}
