package com.example.juegosdidacticos_limpiezadecaballo.data.enums

enum class Difficulty(private val displayDifficulty: String) {
    EASY("Facil"),
    MEDIUM("Media"),
    HARD("Dificil");

    fun getDisplayDifficulty(): String {
        return displayDifficulty
    }
}