package com.example.juegosdidacticos_limpiezadecaballo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Voices

@Entity(tableName = "config_game_table")
data class ConfigGameEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientId: Int,
    val gameVolume: Int,
    val voiceVolume: Int,
    val musicVolume: Int
)