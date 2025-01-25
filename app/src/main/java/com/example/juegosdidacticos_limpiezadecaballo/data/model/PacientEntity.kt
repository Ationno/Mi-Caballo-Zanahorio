package com.example.juegosdidacticos_limpiezadecaballo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "pacient_table")
data class PacientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    override val name: String,
    val surname: String,
    val age: Int,
    val observations: String?,
    override val avatar: Avatar,
    val genre: String,
    val difficulty: Difficulty
) : NamedEntity