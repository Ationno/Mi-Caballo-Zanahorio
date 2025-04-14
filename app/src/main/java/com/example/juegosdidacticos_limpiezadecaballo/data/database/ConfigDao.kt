package com.example.juegosdidacticos_limpiezadecaballo.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Voices
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigEntity

@Dao
interface ConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: ConfigEntity)

    @Query("DELETE FROM config_table WHERE patientId = :patientId")
    suspend fun deleteConfigByPatientId(patientId: Int)

    @Query(
        """
        UPDATE config_table 
        SET difficulty = :difficulty, voices = :voices, clues = :clues, subDifficulty = :subDifficulty
        WHERE patientId = :patientId
    """
    )
    suspend fun updateConfigByPatientId(
        patientId: Int,
        difficulty: Difficulty,
        subDifficulty: Difficulty,
        voices: Voices,
        clues: Boolean
    )

    @Query("SELECT * FROM config_table WHERE patientId = :patientId")
    suspend fun getConfigByPatientId(patientId: Int): ConfigEntity?
}
