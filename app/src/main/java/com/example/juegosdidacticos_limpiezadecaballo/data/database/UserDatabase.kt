package com.example.juegosdidacticos_limpiezadecaballo.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.ConfigGameEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.PatientEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.TherapistEntity
import com.example.juegosdidacticos_limpiezadecaballo.data.model.GameStateEntity
import com.example.juegosdidacticos_limpiezadecaballo.utils.Converters

@Database(
    entities = [PatientEntity::class, TherapistEntity::class, GameStateEntity::class, ConfigEntity::class, ConfigGameEntity::class],
    version = 18
)
@TypeConverters(Converters::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun therapistDao(): TherapistDao
    abstract fun gameStateDao(): GameStateDao
    abstract fun configDao(): ConfigDao
    abstract fun configGameDao(): ConfigGameDao

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