package com.example.juegosdidacticos_limpiezadecaballo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Genre
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "patient_table")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    override val name: String,
    val surname: String,
    val age: Int,
    val observations: String?,
    override val avatar: Avatar,
    val genre: Genre,
) : NamedEntity