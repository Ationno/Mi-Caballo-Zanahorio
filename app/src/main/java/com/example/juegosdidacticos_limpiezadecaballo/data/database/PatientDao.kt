package com.example.juegosdidacticos_limpiezadecaballo.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity): Long

    @Delete
    suspend fun deletePatient(patient: PatientEntity)

    @Update
    suspend fun updatePatient(patient: PatientEntity)

    @Query("SELECT * FROM patient_table")
    fun getAllPatients(): LiveData<List<PatientEntity>>

    @Query("SELECT * FROM patient_table WHERE id = :id")
    suspend fun getPatientById(id: Int): PatientEntity?

    @Query("SELECT COUNT(*) FROM patient_table")
    suspend fun getPatientCount(): Int

}
