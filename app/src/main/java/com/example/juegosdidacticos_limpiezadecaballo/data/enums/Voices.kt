package com.example.juegosdidacticos_limpiezadecaballo.data.enums

enum class Voices(private val displayVoiceString: String) {
    MASCULINE("Masculina"),
    FEMININE("Femenina");

    fun getVoiceString(): String {
        return displayVoiceString
    }
}