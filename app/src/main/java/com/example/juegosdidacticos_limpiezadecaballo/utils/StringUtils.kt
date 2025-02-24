package com.example.juegosdidacticos_limpiezadecaballo.utils

fun String.capitalizeFirstLetter(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}