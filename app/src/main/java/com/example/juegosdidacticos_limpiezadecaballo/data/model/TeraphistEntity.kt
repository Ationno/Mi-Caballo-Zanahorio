package com.example.juegosdidacticos_limpiezadecaballo.data.model;

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar

@Entity(tableName = "teraphist_table")
data class TeraphistEntity(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @NonNull override val name: String,
        @NonNull val surname: String,
        @NonNull override val avatar: Avatar,
): NamedEntity