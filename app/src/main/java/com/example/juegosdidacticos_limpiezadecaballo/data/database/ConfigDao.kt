package com.example.juegosdidacticos_limpiezadecaballo.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Voices
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PacientEntity

@Dao
interface ConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: ConfigEntity)

    @Query("DELETE FROM config_table WHERE pacientId = :pacientId")
    suspend fun deleteConfigByPacientId(pacientId: Int)

    @Query("""
        UPDATE config_table 
        SET difficulty = :difficulty, voices = :voices, clues = :clues
        WHERE pacientId = :pacientId
    """)
    suspend fun updateConfigByPacientId(
        pacientId: Int,
        difficulty: Difficulty,
        voices: Voices,
        clues: Boolean
    )

    @Query("SELECT * FROM config_table WHERE pacientId = :pacientId")
    suspend fun getConfigByPacientId(pacientId: Int): ConfigEntity?
}
