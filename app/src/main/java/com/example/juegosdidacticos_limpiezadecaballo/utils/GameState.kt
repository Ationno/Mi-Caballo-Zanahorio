package com.example.juegosdidacticos_limpiezadecaballo.utils

import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.model.GameStateEntity

class GameState(val errors: Int, val score: Int, val difficulty: Difficulty, val timeLeft: Long,
                val timeTotal: Long, val completed: Boolean
) {
    fun toEntity(): GameStateEntity {
        return GameStateEntity(errors = errors, score = score, difficulty = difficulty, timeLeft = timeLeft, timeTotal = timeTotal, completed = completed)
    }
}