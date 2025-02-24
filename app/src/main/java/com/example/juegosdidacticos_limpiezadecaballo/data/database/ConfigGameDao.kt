package com.example.juegosdidacticos_limpiezadecaballo.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigGameEntity

@Dao
interface ConfigGameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameConfig(config: ConfigGameEntity)

    @Query("DELETE FROM config_game_table WHERE patientId = :patientId")
    suspend fun deleteGameConfigByPatientId(patientId: Int)

    @Query(
        """
        UPDATE config_game_table 
        SET gameVolume = :gameVolume, voiceVolume = :voiceVolume, musicVolume = :musicVolume
        WHERE patientId = :patientId
    """
    )
    suspend fun updateGameConfigByPatientId(
        patientId: Int,
        gameVolume: Int,
        voiceVolume: Int,
        musicVolume: Int
    )

    @Query("SELECT * FROM config_game_table WHERE patientId = :patientId")
    suspend fun getGameConfigByPatientId(patientId: Int): ConfigGameEntity?
}
