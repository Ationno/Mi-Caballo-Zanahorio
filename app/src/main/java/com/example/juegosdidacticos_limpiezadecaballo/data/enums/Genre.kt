package com.example.juegosdidacticos_limpiezadecaballo.data.enums

enum class Genre(private val displayGenreString: String) {
    MALE("Masculino"),
    FEMALE("Femenino");

    fun getGenreString(): String {
        return displayGenreString
    }
}