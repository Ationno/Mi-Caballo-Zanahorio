package com.example.juegosdidacticos_limpiezadecaballo.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PacientEntity

@Dao
interface PacientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPacient(pacient: PacientEntity)

    @Delete
    suspend fun deletePacient(pacient: PacientEntity)

    @Update
    suspend fun updatePacient(pacient: PacientEntity)

    @Query("SELECT * FROM pacient_table")
    fun getAllPacients(): LiveData<List<PacientEntity>>

    @Query("SELECT * FROM pacient_table WHERE id = :id")
    suspend fun getPacientById(id: Int): PacientEntity?

    @Query("SELECT COUNT(*) FROM pacient_table")
    suspend fun getPacientCount(): Int

}
