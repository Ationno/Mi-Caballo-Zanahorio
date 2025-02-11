package com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.juegosdidacticos_limpiezadecaballo.data.database.UserDatabase
import com.example.juegosdidacticos_limpiezadecaballo.data.model.GameStateEntity

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val gameStateDao = UserDatabase.getDatabase(application).gameStateDao()

    suspend fun insertGameState(gameState: GameStateEntity) {
        gameStateDao.insertGameState(gameState)
    }
}