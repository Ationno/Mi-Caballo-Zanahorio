package com.example.juegosdidacticos_limpiezadecaballo.data.model

import android.os.Parcelable
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar

interface NamedEntity : Parcelable {
    val name: String
    val avatar: Avatar
    val id: Int
}