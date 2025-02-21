package com.example.juegosdidacticos_limpiezadecaballo.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TherapistEntity

@Dao
interface TherapistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTherapist(therapist: TherapistEntity)

    @Update
    suspend fun updateTherapist(therapist: TherapistEntity)

    @Delete
    suspend fun deleteTherapist(therapist: TherapistEntity)

    @Query("SELECT * FROM therapist_table")
    fun getAllTherapists(): LiveData<List<TherapistEntity>>

    @Query("SELECT COUNT(*) FROM therapist_table")
    suspend fun getTherapistCount(): Int
}