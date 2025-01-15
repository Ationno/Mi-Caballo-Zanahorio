package com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.MutableLiveData
import com.example.juegosdidacticos_limpiezadecaballo.data.database.UserDatabase
import com.example.juegosdidacticos_limpiezadecaballo.data.model.UserEntity
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = UserDatabase.getDatabase(application).userDao()

    // LiveData para la lista de usuarios
    val allUsers: LiveData<List<UserEntity>> = userDao.getAllUsers()

    // MutableLiveData para pasar el RecyclerView layoutManager si lo necesitas
    val layoutManager: MutableLiveData<LinearLayoutManager> = MutableLiveData()

    // Función para insertar un usuario
    fun insertUser(user: UserEntity) {
        viewModelScope.launch {
            userDao.insert(user)
        }
    }

    fun insertExampleUsers() {
        viewModelScope.launch {
            val exampleUsers = listOf(
                UserEntity(name = "John Doe", email = "john.doe@example.com"),
                UserEntity(name = "Jane Smith", email = "jane.smith@example.com"),
                UserEntity(name = "Tom Brown", email = "tom.brown@example.com"),
                UserEntity(name = "Lisa White", email = "lisa.white@example.com"),
                UserEntity(name = "Mark Black", email = "mark.black@example.com")
            )
            // Insertar usuarios de ejemplo en la base de datos
            exampleUsers.forEach {
                userDao.insert(it)
            }
        }
    }
}
