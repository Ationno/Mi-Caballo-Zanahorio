package com.example.juegosdidacticos_limpiezadecaballo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PacientEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TeraphistEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.GameStateEntity

@Database(entities = [PacientEntity::class, TeraphistEntity::class, GameStateEntity::class, ConfigEntity::class], version = 7)
abstract class UserDatabase : RoomDatabase() {

    abstract fun pacientDao(): PacientDao
    abstract fun teraphistDao(): TeraphistDao
    abstract fun gameStateDao(): GameStateDao
    abstract fun configDao(): ConfigDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}