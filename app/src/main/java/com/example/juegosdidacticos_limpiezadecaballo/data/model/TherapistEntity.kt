package com.example.juegosdidacticos_limpiezadecaballo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "therapist_table")
data class TherapistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    override val name: String,
    val surname: String,
    override val avatar: Avatar,
) : NamedEntity