package com.example.juegosdidacticos_limpiezadecaballo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import java.util.Date

@Entity(tableName = "game_state_table")
data class GameStateEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Date,
    val errors: Int,
    val score: Int,
    val difficulty: Difficulty,
    val timePlayed: Long,
    val patientId: Int
)