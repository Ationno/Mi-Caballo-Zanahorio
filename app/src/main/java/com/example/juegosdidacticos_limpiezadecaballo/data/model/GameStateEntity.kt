package com.example.juegosdidacticos_limpiezadecaballo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty

@Entity(tableName = "game_state_table")
data class GameStateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val errors: Int,
    val score: Int,
    val difficulty: Difficulty,
    val timeLeft: Long,
    val timeTotal: Long,
)