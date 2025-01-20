package com.example.juegosdidacticos_limpiezadecaballo.data.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar

@Entity(tableName = "pacient_table")
data class PacientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @NonNull override val name: String,
    @NonNull val surname: String,
    val age: Int,
    val observations: String?,
    @NonNull override val avatar: Avatar,
    @NonNull val genre: String
) : NamedEntity