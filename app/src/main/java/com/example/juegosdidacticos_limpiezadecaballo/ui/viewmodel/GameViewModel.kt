package com.example.juegosdidacticos_limpiezadecaballo.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.juegosdidacticos_limpiezadecaballo.data.database.GameStateDao
import com.example.juegosdidacticos_limpiezadecaballo.data.model.GameStateEntity
import com.example.juegosdidacticos_limpiezadecaballo.utils.GameState
import kotlinx.coroutines.launch

class GameViewModel(private val gameStateDao: GameStateDao) : ViewModel() {

    fun getGameStateByCompletion(completed: Boolean): GameStateEntity {
        return gameStateDao.getGameStateByCompletion(completed)
    }

    val allGameStates: LiveData<List<GameStateEntity>> = gameStateDao.getAllGameStates()

    suspend fun insertGameState(gameState: GameState) {
        gameStateDao.insertGameState(gameState.toEntity())
    }
}