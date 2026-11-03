package cl.duoc.induztek.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {

    @Query("SELECT * FROM equipos ORDER BY id DESC")
    fun getAll(): Flow<List<Equipo>>

    @Query("SELECT * FROM equipos WHERE codigo LIKE '%' || :texto || '%' OR cliente LIKE '%' || :texto || '%' ORDER BY id DESC")
    fun buscar(texto: String): Flow<List<Equipo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(equipo: Equipo): Long

    @Update
    suspend fun update(equipo: Equipo)

    @Delete
    suspend fun delete(equipo: Equipo)

    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): Equipo?
}