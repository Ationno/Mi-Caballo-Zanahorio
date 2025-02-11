package com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.juegosdidacticos_limpiezadecaballo.data.database.UserDatabase
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Difficulty
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Voices
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TherapistEntity
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val patientDao = UserDatabase.getDatabase(application).patientDao()
    private val therapistDao = UserDatabase.getDatabase(application).therapistDao()
    private val configDao = UserDatabase.getDatabase(application).configDao()

    val allPatients: LiveData<List<PatientEntity>> = patientDao.getAllPatients()
    val allTherapists: LiveData<List<TherapistEntity>> = therapistDao.getAllTherapists()

    suspend fun insertExamplePatients() {
        val patientCount = patientDao.getPatientCount()
        if (patientCount == 0) {
            viewModelScope.launch {
                for (i in 1..4) {
                    val config = ConfigEntity(
                        patientId = i,
                        difficulty = Difficulty.HARD,
                        voices = Voices.FEMININE,
                        clues = true
                    )

                    configDao.insertConfig(config)
                }
                val examplePatients = listOf(
                    PatientEntity(
                        name = "John",
                        surname = "Doe",
                        age = 30,
                        observations = "Observación 1",
                        avatar = Avatar.FIRST,
                        genre = "Masculino",
                    ),
                    PatientEntity(
                        name = "Jane",
                        surname = "Smith",
                        age = 25,
                        observations = "Observación 2",
                        avatar = Avatar.SECOND,
                        genre = "Femenino",
                    ),
                    PatientEntity(
                        name = "Tom",
                        surname = "Brown",
                        age = 35,
                        observations = "Observación 3",
                        avatar = Avatar.THIRD,
                        genre = "Masculino",
                    ),
                    PatientEntity(
                        name = "Lisa",
                        surname = "White",
                        age = 28,
                        observations = "Observación 4",
                        avatar = Avatar.FOURTH,
                        genre = "Femenino",
                    ),
                )
                examplePatients.forEach {
                    patientDao.insertPatient(it)
                }
            }
        }
    }

    suspend fun insertExampleTherapists() {
        val therapistCount = therapistDao.getTherapistCount()
        if (therapistCount == 0) {
            viewModelScope.launch {
                val exampleTherapists = listOf(
                    TherapistEntity(name = "William", surname = "Smith", avatar = Avatar.FIRST),
                    TherapistEntity(name = "Emily", surname = "Johnson", avatar = Avatar.SECOND),
                    TherapistEntity(name = "David", surname = "Brown", avatar = Avatar.THIRD),
                )
                exampleTherapists.forEach {
                    therapistDao.insertTherapist(it)
                }
            }
        }
    }

    suspend fun getConfigByPatientId(patientId: Int): ConfigEntity? {
        return configDao.getConfigByPatientId(patientId)
    }
}
