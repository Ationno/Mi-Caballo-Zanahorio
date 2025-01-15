package com.example.juegosdidacticos_limpiezadecaballo.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.juegosdidacticos_limpiezadecaballo.data.model.UserEntity

@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: UserEntity)

    @Query("SELECT * FROM user_table")
    fun getAllUsers(): LiveData<List<UserEntity>>

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()
}