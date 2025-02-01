package com.example.juegosdidacticos_limpiezadecaballo.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.juegosdidacticos_limpiezadecaballo.data.model.GameStateEntity

@Dao
interface GameStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameState(gameState: GameStateEntity)

    @Update
    suspend fun updateGameState(gameState: GameStateEntity)

    @Query("SELECT * FROM game_state_table")
    fun getAllGameStates(): LiveData<List<GameStateEntity>>
}