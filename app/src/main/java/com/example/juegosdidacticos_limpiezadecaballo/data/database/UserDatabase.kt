package com.example.juegosdidacticos_limpiezadecaballo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PacientEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TeraphistEntity


@Database(entities = [PacientEntity::class, TeraphistEntity::class], version = 4)
abstract class UserDatabase : RoomDatabase() {

    abstract fun pacientDao(): PacientDao
    abstract fun teraphistDao(): TeraphistDao

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
                    .fallbackToDestructiveMigration() // Borra y recrea la base de datos automáticamente
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}