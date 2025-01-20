package com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.MutableLiveData
import com.example.juegosdidacticos_limpiezadecaballo.data.database.UserDatabase
import com.example.juegosdidacticos_limpiezadecaballo.data.enums.Avatar
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PacientEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TeraphistEntity
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val pacientDao = UserDatabase.getDatabase(application).pacientDao()
    private val teraphistDao = UserDatabase.getDatabase(application).teraphistDao()

    // LiveData para la lista de usuarios
    val allPacients: LiveData<List<PacientEntity>> = pacientDao.getAllPacients()
    val allTeraphists: LiveData<List<TeraphistEntity>> = teraphistDao.getAllTeraphists()

    val layoutManager: MutableLiveData<LinearLayoutManager> = MutableLiveData()

    suspend fun insertExamplePacients() {
        val pacientCount = pacientDao.getPacientCount()
        if (pacientCount == 0) {
            viewModelScope.launch {
                val examplePacients = listOf(
                    PacientEntity(
                        name = "John",
                        surname = "Doe",
                        age = 30,
                        observations = "Observación 1",
                        avatar = Avatar.FIRST,
                        genre = "Masculino"
                    ),
                    PacientEntity(
                        name = "Jane",
                        surname = "Smith",
                        age = 25,
                        observations = "Observación 2",
                        avatar = Avatar.SECOND,
                        genre = "Femenino"
                    ),
                    PacientEntity(
                        name = "Tom",
                        surname = "Brown",
                        age = 35,
                        observations = "Observación 3",
                        avatar = Avatar.THIRD,
                        genre = "Masculino"
                    ),
                    PacientEntity(
                        name = "Lisa",
                        surname = "White",
                        age = 28,
                        observations = "Observación 4",
                        avatar = Avatar.FOURTH,
                        genre = "Femenino"
                    ),
                )
                examplePacients.forEach {
                    pacientDao.insertPacient(it)
                }
            }
        }
    }

    suspend fun insertExampleTeraphists() {
        val teraphistCount = teraphistDao.getTeraphistCount()
        if (teraphistCount == 0) {
            viewModelScope.launch {
                val exampleTeraphists = listOf(
                    TeraphistEntity(name = "William", surname = "Smith", avatar = Avatar.FIRST),
                    TeraphistEntity(name = "Emily", surname = "Johnson", avatar = Avatar.SECOND),
                    TeraphistEntity(name = "David", surname = "Brown", avatar = Avatar.THIRD),
                )
                exampleTeraphists.forEach {
                    teraphistDao.insertTeraphist(it)
                }
            }
        }
    }
}
