package com.example.juegosdidacticos_limpiezadecaballo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Voices

@Entity(tableName = "config_table")
data class ConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pacientId: Int,
    val difficulty: Difficulty,
    val voices: Voices,
    val clues: Boolean
)