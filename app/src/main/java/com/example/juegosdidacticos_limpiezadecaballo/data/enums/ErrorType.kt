package com.example.juegosdidacticos_limpiezadecaballo.data.enums

enum class ErrorType(private val info: String) {
    INCORRECT_TOOL("Herramienta incorrecta."),
    INCORRECT_PART("Parte del caballo incorrecta."),
    INCORRECT_MOVEMENT("Movimiento de limpieza incorrecto.");

    fun getInfo(): String {
        return info
    }
}